package com.github.zzm.migration.main;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.github.zzm.migration.model.Gateway;
import com.github.zzm.migration.model.Result;
import com.github.zzm.migration.model.User;
import com.github.zzm.migration.s3.RadosGatewayS3Client;
import com.github.zzm.migration.sch.SchExecutor;
import com.github.zzm.migration.yml.YamlParser;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, DatatypeConfigurationException {
        List<String> buckets = getBucketListFromSource();

        createUserOnTarget();

        User user = YamlParser.getUser();
        Gateway targetRgw = YamlParser.getTargetRgw();
        RadosGatewayS3Client targetClient =
                new RadosGatewayS3Client(user.getAccessKey(), user.getSecretKey(), targetRgw.getUrl());

        createBucketsOnTarget(buckets, targetClient);

        Gateway sourceRgw = YamlParser.getSourceRgw();
        RadosGatewayS3Client sourceClient =
                new RadosGatewayS3Client(user.getAccessKey(), user.getSecretKey(), sourceRgw.getUrl());

        printResult(uploadFilesToTarget(buckets, targetClient, sourceClient));

    }

    private static void printResult(Result result) throws DatatypeConfigurationException {
        Duration duration = DatatypeFactory.newInstance().newDuration(result.getUseTime());
        System.out.println(String.format("data migration finish. success number %d, failed number %d, use time %dm:%ds",
                result.getSuccess(), result.getFailed(), duration.getMinutes(), duration.getSeconds()));
    }

    private static Result uploadFilesToTarget(List<String> buckets,
                                            RadosGatewayS3Client targetClient, RadosGatewayS3Client sourceClient) {
        System.out.println("upload files from source to target rgw...");
        Result result = new Result();
        for (String bucketName : buckets) {
            ObjectListing objects = sourceClient.listObjects(bucketName);
            List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();

            result.addResult(uploadSingleBucketFiles(targetClient, sourceClient, bucketName, objectSummaries));
        }
        return result;
    }

    private static Result uploadSingleBucketFiles(RadosGatewayS3Client targetClient,
                                                RadosGatewayS3Client sourceClient,
                                                String bucketName, List<S3ObjectSummary> objectSummaries) {
        Result result = new Result();
        long start = System.currentTimeMillis();
        for (S3ObjectSummary object : objectSummaries) {
            try {
                S3Object file = sourceClient.getObject(bucketName, object.getKey());
                targetClient.putObject(bucketName, file.getKey(), file.getObjectContent());

                result.add1Success();
            } catch (FileNotFoundException e) {
                System.err.println(String.format("upload file failed. file is /%s/%s", bucketName, object.getKey()));
                result.add1Failed();
            }
        }
        result.addUseTime(System.currentTimeMillis() - start);
        return result;
    }

    private static void createBucketsOnTarget(List<String> buckets, RadosGatewayS3Client targetClient) {
        System.out.println("create buckets on target rgw...");
        for (String bucketName : buckets) {
            if (targetClient.isBucketExist(bucketName)) continue;

            targetClient.createBucket(bucketName);
        }
    }

    private static void createUserOnTarget() {
        System.out.println("create user on target rgw with access and secret key...");
        if (SchExecutor.checkUserExist()) {
            if (!SchExecutor.createUser()) {
                throw new RuntimeException("create user on target rgw failed.");
            }
        }
    }

    private static List<String> getBucketListFromSource() throws IOException {
        System.out.println("list bucket from source rgw...");
        List<String> buckets = SchExecutor.listBuckets();
        if (buckets == null || buckets.isEmpty()) {
            throw new RuntimeException("source gateway hasn't any buckets.");
        }
        return buckets;
    }

}

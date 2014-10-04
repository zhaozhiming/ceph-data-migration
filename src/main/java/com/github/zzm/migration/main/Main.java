package com.github.zzm.migration.main;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.github.zzm.migration.model.Result;
import com.github.zzm.migration.model.User;
import com.github.zzm.migration.s3.RadosGatewayS3Client;
import com.github.zzm.migration.sch.ShellExecutor;
import com.github.zzm.migration.yml.YamlParser;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static com.github.zzm.migration.yml.YamlParser.getSourceRgw;
import static com.github.zzm.migration.yml.YamlParser.getTargetRgw;

public class Main {
    public static void main(String[] args) throws IOException, DatatypeConfigurationException {
        User user = YamlParser.getUser();
        RadosGatewayS3Client targetClient =
                new RadosGatewayS3Client(user.getAccessKey(), user.getSecretKey(), getTargetRgw().getUrl());
        RadosGatewayS3Client sourceClient =
                new RadosGatewayS3Client(user.getAccessKey(), user.getSecretKey(), getSourceRgw().getUrl());

        List<Bucket> buckets = getBucketListFromSource(sourceClient);

        createUserOnTarget();

        createBucketsOnTarget(buckets, targetClient);

        printResult(uploadFilesToTarget(buckets, targetClient, sourceClient));
    }

    private static void printResult(Result result) throws DatatypeConfigurationException {
        Duration duration = DatatypeFactory.newInstance().newDuration(result.getUseTime());
        System.out.println(String.format("data migration finish. success number %d, failed number %d, use time %dm:%ds",
                result.getSuccess(), result.getFailed(), duration.getMinutes(), duration.getSeconds()));
    }

    private static Result uploadFilesToTarget(List<Bucket> buckets,
                                              RadosGatewayS3Client targetClient, RadosGatewayS3Client sourceClient) {
        System.out.println("upload files from source to target rgw...");
        Result result = new Result();
        for (Bucket bucket : buckets) {
            ObjectListing objects = sourceClient.listObjects(bucket.getName());
            List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();

            System.out.println(String.format("bucket prepare upload. bucket:%s, total file number:%d",
                    objects.getBucketName(), objectSummaries.size()));

            result.addResult(uploadSingleBucketFiles(targetClient, sourceClient, bucket.getName(), objectSummaries));
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

    private static void createBucketsOnTarget(List<Bucket> buckets, RadosGatewayS3Client targetClient) {
        System.out.println("create buckets on target rgw...");
        for (Bucket bucket : buckets) {
            if (targetClient.isBucketExist(bucket.getName())) continue;

            targetClient.createBucket(bucket.getName());
        }
    }

    private static void createUserOnTarget() {
        System.out.println("create user on target rgw with access and secret key...");
        if (ShellExecutor.checkUserExist()) {
            if (!ShellExecutor.createUser()) {
                throw new RuntimeException("create user on target rgw failed.");
            }
        }
    }

    private static List<Bucket> getBucketListFromSource(RadosGatewayS3Client sourceS3Client) throws IOException {
        System.out.println("list bucket from source rgw...");
        List<Bucket> buckets = sourceS3Client.listBuckets();
        if (buckets == null || buckets.isEmpty()) {
            throw new RuntimeException("source gateway hasn't any buckets.");
        }
        return buckets;
    }

}

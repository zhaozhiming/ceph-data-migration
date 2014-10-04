package com.github.zzm.migration.main;

import com.github.zzm.migration.model.Gateway;
import com.github.zzm.migration.model.User;
import com.github.zzm.migration.s3.RadosGatewayS3Client;
import com.github.zzm.migration.sch.SchExecutor;
import com.github.zzm.migration.yml.YamlParser;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> buckets = getBucketListFromSource();

        createUserOnTarget();

        User user = YamlParser.getUser();
        Gateway targetRgw = YamlParser.getTargetRgw();
        RadosGatewayS3Client targetClient =
                new RadosGatewayS3Client(user.getAccessKey(), user.getSecretKey(), targetRgw.getUrl());

        createBucketsOnTarget(buckets, targetClient);

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

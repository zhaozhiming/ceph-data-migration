package com.github.zzm.migration.main;

import com.github.zzm.migration.model.Gateway;
import com.github.zzm.migration.model.User;
import com.github.zzm.migration.s3.RadosGatewayS3Client;
import com.github.zzm.migration.util.SshUtil;
import com.github.zzm.migration.util.YamlUtil;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("list bucket from source rgw...");
        List<String> buckets = SshUtil.listBuckets();
        if (buckets == null || buckets.isEmpty()) {
            throw new RuntimeException("source gateway hasn't any buckets.");
        }

        System.out.println("create user on target rgw with access and secret key...");
        if (SshUtil.checkUserExist()) {
            if (!SshUtil.createUser()) {
                throw new RuntimeException("create user on target rgw failed.");
            }
        }

        User user = YamlUtil.parseUser();
        Gateway targetRgw = YamlUtil.parseTargetRgw();
        System.out.println("create buckets on target rgw...");
        RadosGatewayS3Client s3Client =
                new RadosGatewayS3Client(user.getAccessKey(), user.getSecretKey(), targetRgw.getUrl());
        for (String bucketName : buckets) {
            if (s3Client.isBucketExist(bucketName)) continue;

            s3Client.createBucket(bucketName);
        }

    }

}

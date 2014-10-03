package com.github.zzm.migration.main;

import com.github.zzm.migration.util.SshUtil;

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

        }

    }

}

package com.github.zzm.migration.model;

public class User {
    private String uid;
    private String displayName;
    private String accessKey;
    private String secretKey;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey.replace("\\", "");
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}

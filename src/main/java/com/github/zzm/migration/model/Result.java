package com.github.zzm.migration.model;

public class Result {
    private long success;
    private long failed;
    private long useTime;


    public void add1Success() {
        success++;
    }

    public void add1Failed() {
        failed++;
    }

    public void addUseTime(long useTime) {
        this.useTime += useTime;
    }

    public void addResult(Result result) {
        success += result.getSuccess();
        failed += result.getFailed();
        useTime += result.getUseTime();
    }

    public long getSuccess() {
        return success;
    }

    public long getFailed() {
        return failed;
    }

    public long getUseTime() {
        return useTime;
    }
}

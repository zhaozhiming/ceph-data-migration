package com.github.zzm.migration.main;

import com.github.zzm.migration.sch.ShellExecutor;
import org.junit.Test;

public class MainTest {


    @Test
    public void should_() throws Exception {
//        System.out.println(SshUtil.listBuckets());
//        System.out.println(SshUtil.checkUserExist());
        System.out.println(ShellExecutor.createUser());
    }
}
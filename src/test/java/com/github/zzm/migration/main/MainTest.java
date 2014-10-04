package com.github.zzm.migration.main;

import com.github.zzm.migration.util.SshUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {


    @Test
    public void should_() throws Exception {
//        System.out.println(SshUtil.listBuckets());
//        System.out.println(SshUtil.checkUserExist());
        System.out.println(SshUtil.createUser());
    }
}
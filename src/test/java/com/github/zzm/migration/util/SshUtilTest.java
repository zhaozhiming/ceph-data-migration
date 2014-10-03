package com.github.zzm.migration.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class SshUtilTest {

    @Test
    public void should_correct() throws Exception {
        System.out.println("result:" + SshUtil.exec("ls", YamlUtil.parseSourceRgw()));
    }

}
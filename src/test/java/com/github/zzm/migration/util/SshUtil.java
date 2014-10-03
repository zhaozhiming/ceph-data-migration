package com.github.zzm.migration.util;

import com.github.zzm.migration.model.Gateway;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.jcraft.jsch.*;

import java.io.InputStream;
import java.io.InputStreamReader;

public class SshUtil {

    public static String exec(String command, Gateway gateway) throws JSchException {
        Session session = null;
        Channel channel = null;
        try {
            JSch jSch = new JSch();
            session = jSch.getSession(gateway.getUser(), gateway.getHost());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(gateway.getPassword());
            session.connect();

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            ((ChannelExec) channel).setErrStream(System.err);
            channel.connect();

            InputStream inputStream = channel.getInputStream();

            return CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(String.format("exec command failed. host:%s, user:%s, command:%s",
                    gateway.getHost(), gateway.getUser(), command), e);
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }
}

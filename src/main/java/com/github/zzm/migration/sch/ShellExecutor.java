package com.github.zzm.migration.sch;

import com.github.zzm.migration.model.Gateway;
import com.github.zzm.migration.model.User;
import com.github.zzm.migration.yml.YamlParser;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ShellExecutor {

    public static boolean checkUserExist() {
        User user = YamlParser.getUser();
        String command = String.format("sudo /usr/bin/radosgw-admin user info --uid=%s " +
                "--name=client.radosgw.gateway", user.getUid());
        String result = exec(command, YamlParser.getTargetRgw());
        System.out.println(String.format("user info: %s", result));
        return result.contains(user.getUid());
    }

    public static boolean createUser() {
        User user = YamlParser.getUser();
        String command = String.format("sudo /usr/bin/radosgw-admin user create --uid=%s " +
                        "--display-name=%s --access-key=%s --secret=%s --name=client.radosgw.gateway",
                user.getUid(), user.getDisplayName(), user.getAccessKey(), user.getSecretKey());
        String result = exec(command, YamlParser.getTargetRgw());
        System.out.println(String.format("user info: %s", result));
        return result.contains(user.getUid());
    }

    private static String exec(String command, Gateway gateway) {
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

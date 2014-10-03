package com.github.zzm.migration.util;

import com.github.zzm.migration.model.Gateway;
import com.github.zzm.migration.model.User;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class SshUtil {

    public static List<String> listBuckets() throws IOException {
        User user = YamlUtil.parseUser();
        Gateway sourceRgw = YamlUtil.parseSourceRgw();
        String command = "sudo /usr/bin/radosgw-admin bucket list --uid=%s --name=client.radosgw.gateway";
        String result = exec(String.format(command, user.getUid()), sourceRgw);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, new TypeReference<List<String>>() {});
    }

    public static boolean checkUserExist() {
        User user = YamlUtil.parseUser();
        Gateway targetRgw = YamlUtil.parseTargetRgw();
        String command = "sudo /usr/bin/radosgw-admin user info --uid=%s --name=client.radosgw.gateway";
        String result = exec(String.format(command, user.getUid()), targetRgw);
        System.out.println(result);
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
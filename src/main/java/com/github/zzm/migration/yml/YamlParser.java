package com.github.zzm.migration.yml;

import com.github.zzm.migration.model.Gateway;
import com.github.zzm.migration.model.User;
import org.ho.yaml.Yaml;

import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;

public class YamlParser {
    private static User user = parse("user.yml", User.class);
    private static Gateway sourceRgw = parse("source_rgw.yml", Gateway.class);
    private static Gateway targetRgw = parse("target_rgw.yml", Gateway.class);

    public static User getUser() {
        return user;
    }

    public static Gateway getSourceRgw() {
        return sourceRgw;
    }

    public static Gateway getTargetRgw() {
        return targetRgw;
    }

    private static <T> T parse(String fileName, Class<T> iclass) {
        try {
            CodeSource src = YamlParser.class.getProtectionDomain().getCodeSource();
            URL url = new URL(src.getLocation(), fileName);
            InputStream inputStream = url.openStream();
            return Yaml.loadType(inputStream, iclass);
        } catch (Exception e) {
            throw new RuntimeException(String.format("parse yaml file failed. file: %s.", fileName));
        }
    }
}

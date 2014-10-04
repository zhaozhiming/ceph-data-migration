package com.github.zzm.migration.yml;

import com.github.zzm.migration.model.Gateway;
import com.github.zzm.migration.model.User;
import org.ho.yaml.Yaml;

import java.io.FileNotFoundException;
import java.io.InputStream;

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
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
            return Yaml.loadType(inputStream, iclass);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("parse yaml file failed. file: %s.", fileName));
        }
    }
}

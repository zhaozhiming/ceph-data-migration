package com.github.zzm.migration.util;

import com.github.zzm.migration.model.Gateway;
import com.github.zzm.migration.model.User;
import org.ho.yaml.Yaml;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class YamlUtil {
    public static User parseUser() throws FileNotFoundException {
        return parse("user.yml", User.class);
    }

    public static Gateway parseSourceRgw() throws FileNotFoundException {
        return parse("source_rgw.yml", Gateway.class);
    }

    public static Gateway parseTargetRgw() throws FileNotFoundException {
        return parse("target_rgw.yml", Gateway.class);
    }

    private static <T> T parse(String fileName, Class<T> iclass) throws FileNotFoundException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
        return Yaml.loadType(inputStream, iclass);
    }
}

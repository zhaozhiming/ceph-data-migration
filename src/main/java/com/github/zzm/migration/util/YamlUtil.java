package com.github.zzm.migration.util;

import com.github.zzm.migration.model.User;
import org.ho.yaml.Yaml;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class YamlUtil {
    public static User parseUser() throws FileNotFoundException {
        return parse("user.yml", User.class);
    }

    private static <T> T parse(String fileName, Class<T> iclass) throws FileNotFoundException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
        return Yaml.loadType(inputStream, iclass);
    }
}

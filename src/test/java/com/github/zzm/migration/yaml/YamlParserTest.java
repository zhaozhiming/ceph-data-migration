package com.github.zzm.migration.yaml;

import com.github.zzm.migration.model.Gateway;
import com.github.zzm.migration.model.User;
import com.github.zzm.migration.yml.YamlParser;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class YamlParserTest {

    @Test
    public void should_parse_user_correct() throws Exception {
        User user = YamlParser.getUser();
        assertThat(user.getUid(), is("zzm"));
        assertThat(user.getDisplayName(), is("zhaozhiming"));
        assertThat(user.getAccessKey(), is("A5N01NS46EPXLX67KIRF"));
        assertThat(user.getSecretKey(), is("T7xf/PPj6vr7r618Xxv/UoVODFJdtLybY/G4lAb8"));
    }

    @Test
    public void should_source_rgw_correct() throws Exception {
        Gateway sourceRgw = YamlParser.getSourceRgw();
        assertThat(sourceRgw.getUrl(), is("http://192.168.42.3:80"));
        assertThat(sourceRgw.getHost(), is("192.168.42.3"));
        assertThat(sourceRgw.getUser(), is("vagrant"));
        assertThat(sourceRgw.getPassword(), is("vagrant"));
    }

    @Test
    public void should_target_rgw_correct() throws Exception {
        Gateway targetRgw = YamlParser.getTargetRgw();
        assertThat(targetRgw.getUrl(), is("http://192.168.42.2:80"));
        assertThat(targetRgw.getHost(), is("192.168.42.2"));
        assertThat(targetRgw.getUser(), is("vagrant"));
        assertThat(targetRgw.getPassword(), is("vagrant"));
    }

}

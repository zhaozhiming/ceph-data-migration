package com.github.zzm.migration.util;

import com.github.zzm.migration.model.User;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class YamlUtilTest {

    @Test
    public void should_parse_user_correct() throws Exception {
        User user = YamlUtil.parseUser();
        assertThat(user.getUid(), is("zzm"));
        assertThat(user.getDisplayName(), is("zhaozhiming"));
        assertThat(user.getAccessKey(), is("A5N01NS46EPXLX67KIRF"));
        assertThat(user.getSecretKey(), is("T7xf/PPj6vr7r618Xxv/UoVODFJdtLybY/G4lAb8"));
    }

}
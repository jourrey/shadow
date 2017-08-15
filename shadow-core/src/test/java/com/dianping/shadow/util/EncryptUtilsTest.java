package com.dianping.shadow.util;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

/**
 * Created by jourrey on 16/11/24.
 */
public class EncryptUtilsTest {

    @Test
    public void testEncodeMD5String() {
        Assert.assertThat(EncryptUtils.encodeMD5String("oneadmin"), is("51E0948C5EDA9E308A85D64701CF74A3"));
    }

    @Test
    public void testEncodeSHAString() {
        Assert.assertThat(EncryptUtils.encodeSHAString("oneadmin"), is("7BC8559A8FE509E680562B85C337F170956FCB06"));
    }

}

package com.dianping.shadow.aspect;

import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.context.init.InitApplicationContext;
import com.dianping.shadow.declaration.AspectDefinition;
import com.dianping.shadow.util.ReflectionUtils;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;

import static org.hamcrest.core.Is.is;

/**
 * 容器初始化
 * Created by jourrey on 16/11/10.
 */
public class AspectConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(AspectConfigTest.class);

    @Before
    public void cleanerAspectContext() {
        Field aspectResolverCache = ReflectionUtils.findField(AspectContext.class, "aspectResolverCache");
        Field joinPointResolverCache = ReflectionUtils.findField(AspectContext.class, "joinPointResolverCache");
        ReflectionUtils.makeAccessible(aspectResolverCache);
        ReflectionUtils.makeAccessible(joinPointResolverCache);
        ReflectionUtils.setField(aspectResolverCache, AspectContext.getInstance(), Lists.newLinkedList());
        ReflectionUtils.setField(joinPointResolverCache, AspectContext.getInstance(), LinkedHashMultimap.create());
    }

    @Test
    public void testAspectConfig() {
        InitApplicationContext.scan("com.dianping.shadow.sample.declaration");
        Collection<AspectDefinition> aspectResolver = AspectContext.getInstance().getAspect();
        Assert.assertThat(aspectResolver.size(), is(8));
        AspectDefinition aspectDefinition = null;
        for (Iterator<AspectDefinition> iterator = aspectResolver.iterator(); iterator.hasNext(); ) {
            aspectDefinition = iterator.next();
            LOG.error("aspectDefinition:{}", aspectDefinition);
        }
        Assert.assertThat(aspectDefinition.getOrder(), is(1));
        Assert.assertThat(aspectDefinition.getExtendInfo(), is("Interface"));
    }

}

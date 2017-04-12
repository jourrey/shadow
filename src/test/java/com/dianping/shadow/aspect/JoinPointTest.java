package com.dianping.shadow.aspect;

import com.dianping.shadow.sample.annotation.AnnotationTest;
import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;
import com.dianping.shadow.context.AdviceBean;
import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.context.init.InitApplicationContext;
import com.dianping.shadow.util.ReflectionUtils;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;

import static org.hamcrest.core.Is.is;

/**
 * 容器初始化
 * Created by jourrey on 16/11/10.
 */
public class JoinPointTest {
    private static final Logger LOG = LogManager.getLogger(JoinPointTest.class);

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
    public void testJoinPoint() {
        InitApplicationContext.scan("com.dianping.shadow.sample.notinherited");
        Collection<JoinPointBean> joinPointBeans = AspectContext.getInstance().getJoinPoint();
        Assert.assertThat(joinPointBeans.size(), is(16));
        int annotationCount = 0;
        int inheritedCount = 0;
        int count = 0;
        for (Iterator<JoinPointBean> iterator = joinPointBeans.iterator(); iterator.hasNext(); ) {
            JoinPointBean joinPointBean = iterator.next();
            LOG.error("joinPointBean:{}#{}", joinPointBean.getMember().getDeclaringClass(), joinPointBean.getMember().getName());
            Assert.assertTrue(joinPointBean.getMember() instanceof Method);
            Assert.assertFalse(Modifier.isAbstract(joinPointBean.getMember().getModifiers()));
            Collection<AdviceBean> adviceResolver = AspectContext.getInstance().getAdvice(joinPointBean);
            Assert.assertTrue(adviceResolver.size() > 0);
            LOG.error("size:{}", adviceResolver.size());
            count += adviceResolver.size();
            for (AdviceBean adviceBean : adviceResolver) {
                LOG.error("adviceBean:{}#{}", adviceBean.getAdvice().getClass(), adviceBean.getExtendInfo());
                if (AnnotationTest.class.equals(adviceBean.getPointcutBean().getAnnotation().annotationType())) {
                    annotationCount++;
                }
                if (InheritedAnnotationTest.class.equals(adviceBean.getPointcutBean().getAnnotation().annotationType())) {
                    inheritedCount++;
                }
            }
        }
        Assert.assertThat(annotationCount, is(16));
        Assert.assertThat(inheritedCount, is(0));
        Assert.assertThat(count, is(annotationCount + inheritedCount));
    }

    @Test
    public void testJoinPoint2() {
        InitApplicationContext.scan("com.dianping.shadow.sample.inherited");
        Collection<JoinPointBean> joinPointBeans = AspectContext.getInstance().getJoinPoint();
        Assert.assertThat(joinPointBeans.size(), is(20));
        int annotationCount = 0;
        int inheritedCount = 0;
        int count = 0;
        for (Iterator<JoinPointBean> iterator = joinPointBeans.iterator(); iterator.hasNext(); ) {
            JoinPointBean joinPointBean = iterator.next();
            LOG.error("joinPointBean:{}#{}", joinPointBean.getMember().getDeclaringClass(), joinPointBean.getMember().getName());
            Assert.assertTrue(joinPointBean.getMember() instanceof Method);
            Assert.assertFalse(Modifier.isAbstract(joinPointBean.getMember().getModifiers()));
            Collection<AdviceBean> adviceResolver = AspectContext.getInstance().getAdvice(joinPointBean);
            Assert.assertTrue(adviceResolver.size() > 0);
            LOG.error("size:{}", adviceResolver.size());
            count += adviceResolver.size();
            for (AdviceBean adviceBean : adviceResolver) {
                LOG.error("adviceBean:{}#{}", adviceBean.getAdvice().getClass(), adviceBean.getExtendInfo());
                if (AnnotationTest.class.equals(adviceBean.getPointcutBean().getAnnotation().annotationType())) {
                    annotationCount++;
                }
                if (InheritedAnnotationTest.class.equals(adviceBean.getPointcutBean().getAnnotation().annotationType())) {
                    inheritedCount++;
                }
            }
        }
        Assert.assertThat(annotationCount, is(0));
        Assert.assertThat(inheritedCount, is(20));
        Assert.assertThat(count, is(annotationCount + inheritedCount));
    }
}

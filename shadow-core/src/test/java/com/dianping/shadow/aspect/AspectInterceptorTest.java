package com.dianping.shadow.aspect;

import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.context.JoinPointBeanFactory;
import com.dianping.shadow.context.PointcutBeanFactory;
import com.dianping.shadow.context.init.InitApplicationContext;
import com.dianping.shadow.sample.annotation.AnnotationTest;
import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;
import com.dianping.shadow.sample.inherited.MethodInheritedAspect;
import com.dianping.shadow.sample.notinherited.MethodAnnotationAspect;
import com.dianping.shadow.util.AnnotationUtils;
import com.dianping.shadow.util.ReflectionUtils;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

/**
 * AOP的切面
 *
 * @author jourrey
 */
public class AspectInterceptorTest {
    private static final Logger LOG = LoggerFactory.getLogger(AspectInterceptorTest.class);

    /**
     * 切面处理
     *
     * @param aspectParam
     * @return
     * @throws Exception
     */
    public String intercept(AspectParam aspectParam) throws Exception {
        LOG.debug("enter Struts2AspectInterceptor");
        Object result;
        try {
            result = AspectParamAspectInterceptor.INTERCEPTOR.interceptor.advice(aspectParam);
        } catch (Throwable th) {
            throw (Exception) th;
        }
        LOG.debug("exit Struts2AspectInterceptor");
        return (String) result;
    }

    public enum AspectParamAspectInterceptor {
        INTERCEPTOR(new AspectInterceptor<AspectParam>() {

            @Override
            protected JoinPointBean getJoinPointBean(AspectParam aspectParam) {
                return aspectParam.getJoinPointBean();
            }

            @Override
            protected Map<String, Object> getParameters(AspectParam aspectParam) {
                return aspectParam.getParameters();
            }

            @Override
            protected Object getResult(AspectParam aspectParam) throws Throwable {
                return aspectParam.getResult();
            }
        });

        private final AspectInterceptor<AspectParam> interceptor;

        AspectParamAspectInterceptor(AspectInterceptor<AspectParam> interceptor) {
            this.interceptor = interceptor;
        }
    }

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
    public void testAspectInterceptor() throws Exception {
        InitApplicationContext.scan("com.dianping.shadow.sample.notinherited");

        AspectInterceptorTest interceptor0 = new AspectInterceptorTest();
        AspectInterceptorTest interceptor1 = new AspectInterceptorTest();

        Method method12 = ReflectionUtils.findMethod(MethodAnnotationAspect.class, "getString12", String.class);
        Method methodInherited12 = ReflectionUtils.findMethod(MethodInheritedAspect.class, "getString12", String.class);

        String token = AspectContext.getInstance().getToken();
        Assert.assertNotNull(token);
        Assert.assertNull(AspectContext.getInstance().getInitHierarchy());
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(0));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(0));

        AspectParam aspectParam0 = AspectParam.create(PointcutBeanFactory.create(AnnotationUtils.parseMethod(method12, AnnotationTest.class))
                , JoinPointBeanFactory.create(method12));
        AspectParam aspectParam1 = AspectParam.create(PointcutBeanFactory.create(AnnotationUtils.parseMethod(methodInherited12, InheritedAnnotationTest.class))
                , JoinPointBeanFactory.create(method12));

        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertNull(AspectContext.getInstance().getInitHierarchy());
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(0));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(3));

        interceptor0.intercept(aspectParam0);
        Assert.assertThat(AspectContext.getInstance().getToken(), not(token));
        Assert.assertNull(AspectContext.getInstance().getInitHierarchy());
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(0));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(0));

        interceptor1.intercept(aspectParam1);
        Assert.assertThat(AspectContext.getInstance().getToken(), not(token));
        Assert.assertNull(AspectContext.getInstance().getInitHierarchy());
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(0));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(0));
    }
}
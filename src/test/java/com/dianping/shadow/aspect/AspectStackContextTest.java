package com.dianping.shadow.aspect;

import com.dianping.shadow.sample.notinherited.MethodAnnotationAspect;
import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.context.JoinPointBeanFactory;
import com.dianping.shadow.context.init.InitApplicationContext;
import com.dianping.shadow.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

/**
 * 容器初始化
 * Created by jourrey on 16/11/10.
 */
public class AspectStackContextTest {

    @Test
    public void testBuilder() {
        InitApplicationContext.scan("com.dianping.shadow.sample.notinherited");

        Method method12 = ReflectionUtils.findMethod(MethodAnnotationAspect.class, "getString12", String.class);

        AspectContext.getInstance().tryClean();

        String token = AspectContext.getInstance().getToken();
        Assert.assertNotNull(token);
        Assert.assertNull(AspectContext.getInstance().getInitHierarchy());
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(0));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(0));

        AspectStackContext context0 = AspectStackContext.builder(JoinPointBeanFactory.create(method12));
        AspectStackContext context1 = AspectStackContext.builder(JoinPointBeanFactory.create(method12));
        AspectStackContext context2 = AspectStackContext.builder(JoinPointBeanFactory.create(method12));
        AspectStackContext context3 = AspectStackContext.builder(JoinPointBeanFactory.create(method12));

        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertNull(AspectContext.getInstance().getInitHierarchy());
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(0));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(1));

        context0.doBefore(null);
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(3));

        context1.doBefore(null);
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(2));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(5));

        context2.doBefore(null);
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(3));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(7));

        context3.doBefore(null);
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(4));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(9));

        context3.doReturning(null);
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(4));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(11));

        context2.doReturning(null);
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(3));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(13));

        context1.doReturning(null);
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(2));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(15));

        context0.doReturning(null);
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(17));

        context3.doFinally();
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(4));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(18));

        context2.doFinally();
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(3));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(19));

        context1.doFinally();
        Assert.assertThat(AspectContext.getInstance().getToken(), is(token));
        Assert.assertThat(AspectContext.getInstance().getInitHierarchy(), is(1));
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(2));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(20));

        context0.doFinally();
        Assert.assertThat(AspectContext.getInstance().getToken(), not(token));
        Assert.assertNull(AspectContext.getInstance().getInitHierarchy());
        Assert.assertThat(AspectContext.getInstance().getCurrentHierarchy(), is(0));
        Assert.assertThat(AspectContext.getInstance().getAspectSequenceAndIncrement(), is(0));
    }

}

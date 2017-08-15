package com.dianping.shadow.context.strategy;

import com.dianping.shadow.advice.AdviceBean;
import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.context.init.InitApplicationContext;
import com.dianping.shadow.sample.annotation.AnnotationTest;
import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;
import com.dianping.shadow.util.ReflectionUtils;
import com.google.common.base.Optional;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;

import static org.hamcrest.core.Is.is;

/**
 * Created by jourrey on 16/11/10.
 */
public class AspectAnnotationStrategyTest {

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
    public void testStrategy0() {
        AspectAnnotationStrategy strategy0 = new AspectAnnotationStrategy() {
            @Override
            public Optional<Class<? extends Annotation>> choose(Class<? extends Annotation> annotationType) {
                return Optional.absent();
            }
        };
        AspectAnnotationStrategyLoader.registerStrategy(strategy0);

        InitApplicationContext.scan("com.dianping.shadow.sample.notinherited");
        Collection<JoinPointBean> joinPointBeans = AspectContext.getInstance().getJoinPoint();
        Assert.assertThat(joinPointBeans.size(), is(16));
        int annotationCount = 0;
        int inheritedCount = 0;
        int count = 0;
        for (Iterator<JoinPointBean> iterator = joinPointBeans.iterator(); iterator.hasNext(); ) {
            JoinPointBean joinPointBean = iterator.next();
            Assert.assertTrue(joinPointBean.getMember() instanceof Method);
            Assert.assertFalse(Modifier.isAbstract(joinPointBean.getMember().getModifiers()));
            Collection<AdviceBean> adviceResolver = AspectContext.getInstance().getAdvice(joinPointBean);
            Assert.assertTrue(adviceResolver.size() > 0);
            count += adviceResolver.size();
            for (AdviceBean adviceBean : adviceResolver) {
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

        AspectAnnotationStrategyLoader.unregisterStrategy(strategy0);
    }

    @Test
    public void testStrategy1() {
        AspectAnnotationStrategy strategy0 = new AspectAnnotationStrategy() {
            @Override
            public Optional<Class<? extends Annotation>> choose(Class<? extends Annotation> annotationType) {
                return Optional.absent();
            }
        };
        AspectAnnotationStrategy strategy1 = new AspectAnnotationStrategy() {
            @Override
            public Optional<? extends Class<? extends Annotation>> choose(Class<? extends Annotation> annotationType) {
                return Optional.of(annotationType);
            }
        };
        AspectAnnotationStrategyLoader.registerStrategy(strategy0);
        AspectAnnotationStrategyLoader.registerStrategy(strategy1);

        InitApplicationContext.scan("com.dianping.shadow.sample.notinherited");
        Collection<JoinPointBean> joinPointBeans = AspectContext.getInstance().getJoinPoint();
        Assert.assertThat(joinPointBeans.size(), is(16));
        int annotationCount = 0;
        int inheritedCount = 0;
        int count = 0;
        for (Iterator<JoinPointBean> iterator = joinPointBeans.iterator(); iterator.hasNext(); ) {
            JoinPointBean joinPointBean = iterator.next();
            Assert.assertTrue(joinPointBean.getMember() instanceof Method);
            Assert.assertFalse(Modifier.isAbstract(joinPointBean.getMember().getModifiers()));
            Collection<AdviceBean> adviceResolver = AspectContext.getInstance().getAdvice(joinPointBean);
            Assert.assertTrue(adviceResolver.size() > 0);
            count += adviceResolver.size();
            for (AdviceBean adviceBean : adviceResolver) {
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
        AspectAnnotationStrategyLoader.unregisterStrategy(strategy0);
        AspectAnnotationStrategyLoader.unregisterStrategy(strategy1);
    }

    @Test
    public void testStrategy2() {
        AspectAnnotationStrategy strategy0 = new AspectAnnotationStrategy() {
            @Override
            public Optional<Class<? extends Annotation>> choose(Class<? extends Annotation> annotationType) {
                return Optional.absent();
            }
        };
        AspectAnnotationStrategy strategy1 = new AspectAnnotationStrategy() {
            @Override
            public Optional<? extends Class<? extends Annotation>> choose(Class<? extends Annotation> annotationType) {
                return Optional.of(annotationType);
            }
        };
        AspectAnnotationStrategy strategy2 = new AspectAnnotationStrategy() {
            @Override
            public Optional<? extends Class<? extends Annotation>> choose(Class<? extends Annotation> annotationType) {
                return Optional.of(AnnotationTest.class.equals(annotationType) ? InheritedAnnotationTest.class : annotationType);
            }
        };
        AspectAnnotationStrategyLoader.registerStrategy(strategy0);
        AspectAnnotationStrategyLoader.registerStrategy(strategy1);
        AspectAnnotationStrategyLoader.registerStrategy(strategy2);

        InitApplicationContext.scan("com.dianping.shadow.sample.strategy");
        Collection<JoinPointBean> joinPointBeans = AspectContext.getInstance().getJoinPoint();
        Assert.assertThat(joinPointBeans.size(), is(20));
        int annotationCount = 0;
        int inheritedCount = 0;
        int count = 0;
        for (Iterator<JoinPointBean> iterator = joinPointBeans.iterator(); iterator.hasNext(); ) {
            JoinPointBean joinPointBean = iterator.next();
            Assert.assertTrue(joinPointBean.getMember() instanceof Method);
            Assert.assertFalse(Modifier.isAbstract(joinPointBean.getMember().getModifiers()));
            Collection<AdviceBean> adviceResolver = AspectContext.getInstance().getAdvice(joinPointBean);
            Assert.assertTrue(adviceResolver.size() > 0);
            count += adviceResolver.size();
            for (AdviceBean adviceBean : adviceResolver) {
                if (AnnotationTest.class.equals(adviceBean.getPointcutBean().getAnnotation().annotationType())) {
                    annotationCount++;
                }
                if (InheritedAnnotationTest.class.equals(adviceBean.getPointcutBean().getAnnotation().annotationType())) {
                    inheritedCount++;
                }
            }
        }
        Assert.assertThat(annotationCount, is(16));
        Assert.assertThat(inheritedCount, is(4));
        Assert.assertThat(count, is(annotationCount + inheritedCount));

        AspectAnnotationStrategyLoader.unregisterStrategy(strategy0);
        AspectAnnotationStrategyLoader.unregisterStrategy(strategy1);
        AspectAnnotationStrategyLoader.unregisterStrategy(strategy2);
    }

    @Test
    public void testStrategy3() {
        AspectAnnotationStrategy strategy0 = new AspectAnnotationStrategy() {
            @Override
            public Optional<Class<? extends Annotation>> choose(Class<? extends Annotation> annotationType) {
                return Optional.absent();
            }
        };
        AspectAnnotationStrategy strategy1 = new AspectAnnotationStrategy() {
            @Override
            public Optional<? extends Class<? extends Annotation>> choose(Class<? extends Annotation> annotationType) {
                return Optional.of(annotationType);
            }
        };
        AspectAnnotationStrategy strategy2 = new AspectAnnotationStrategy() {
            @Override
            public Optional<? extends Class<? extends Annotation>> choose(Class<? extends Annotation> annotationType) {
                return Optional.of(AnnotationTest.class.equals(annotationType) ? InheritedAnnotationTest.class : annotationType);
            }
        };
        AspectAnnotationStrategyLoader.registerStrategy(strategy0);
        AspectAnnotationStrategyLoader.registerStrategy(strategy1);
        AspectAnnotationStrategyLoader.registerStrategy(strategy2);

        InitApplicationContext.scan("com.dianping.shadow.sample.inherited");
        Collection<JoinPointBean> joinPointBeans = AspectContext.getInstance().getJoinPoint();
        Assert.assertThat(joinPointBeans.size(), is(20));
        int annotationCount = 0;
        int inheritedCount = 0;
        int count = 0;
        for (Iterator<JoinPointBean> iterator = joinPointBeans.iterator(); iterator.hasNext(); ) {
            JoinPointBean joinPointBean = iterator.next();
            Assert.assertTrue(joinPointBean.getMember() instanceof Method);
            Assert.assertFalse(Modifier.isAbstract(joinPointBean.getMember().getModifiers()));
            Collection<AdviceBean> adviceResolver = AspectContext.getInstance().getAdvice(joinPointBean);
            Assert.assertTrue(adviceResolver.size() > 0);
            count += adviceResolver.size();
            for (AdviceBean adviceBean : adviceResolver) {
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

        AspectAnnotationStrategyLoader.unregisterStrategy(strategy0);
        AspectAnnotationStrategyLoader.unregisterStrategy(strategy1);
        AspectAnnotationStrategyLoader.unregisterStrategy(strategy2);
    }

}

package com.dianping.shadow.context.scanner;

import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.util.ReflectionUtils;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.hamcrest.core.Is.is;

/**
 * Created by jourrey on 16/11/10.
 */
public class ClassScannerTest {

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
    public void testScanFile() {
        Set<Class<?>> classSet = new ClassScanner.ClassScannerBuilder()
                .basePackage("com.dianping.shadow")
                .recursive(false)
                .build()
                .doScan();
        Assert.assertThat(classSet.size(), is(0));

        classSet = new ClassScanner.ClassScannerBuilder()
                .basePackage("com.dianping.shadow.sample.notinherited")
                .recursive(false)
                .build()
                .doScan();
        Assert.assertThat(classSet.size(), is(6));

        classSet = new ClassScanner.ClassScannerBuilder()
                .basePackage("com.dianping.shadow.sample.notinherited")
                .recursive(true)
                .build()
                .doScan();
        Assert.assertThat(classSet.size(), is(6));

        classSet = new ClassScanner.ClassScannerBuilder()
                .basePackage("com.dianping.shadow")
                .recursive(true)
                .build()
                .doScan();
        Assert.assertThat(classSet.size(), is(168));
        for (Class cl : classSet) {
            Assert.assertTrue(cl.getName().startsWith("com.dianping.shadow"));
        }
    }

    @Test
    public void testScanJar() {
        Set<Class<?>> classSet = new ClassScanner.ClassScannerBuilder()
                .basePackage("org.aspectj.lang")
                .recursive(false)
                .build()
                .doScan();
        Assert.assertThat(classSet.size(), is(7));
        for (Class cl : classSet) {
            Assert.assertTrue(cl.getName().startsWith("org.aspectj.lang"));
        }

        classSet = new ClassScanner.ClassScannerBuilder()
                .basePackage("org.aspectj.lang")
                .recursive(true)
                .build()
                .doScan();
        Assert.assertThat(classSet.size(), is(62));
        for (Class cl : classSet) {
            Assert.assertTrue(cl.getName().startsWith("org.aspectj.lang"));
        }
    }

    @Test
    public void testScanFilter() {
        Set<Class<?>> classSet = new ClassScanner.ClassScannerBuilder()
                .basePackage("com.dianping.shadow")
                .recursive(false)
                .filter(new AbstractScanner.ScannerFilter() {
                    @Override
                    public boolean accept(Object o) {
                        return false;
                    }
                })
                .build()
                .doScan();
        Assert.assertThat(classSet.size(), is(0));

        classSet = new ClassScanner.ClassScannerBuilder()
                .basePackage("com.dianping.shadow")
                .recursive(true)
                .filter(new AbstractScanner.ScannerFilter() {
                    @Override
                    public boolean accept(Object o) {
                        return false;
                    }
                })
                .build()
                .doScan();
        Assert.assertThat(classSet.size(), is(0));

        classSet = new ClassScanner.ClassScannerBuilder()
                .basePackage("org.aspectj.lang")
                .recursive(false)
                .filter(new AbstractScanner.ScannerFilter() {
                    @Override
                    public boolean accept(Object o) {
                        return false;
                    }
                })
                .build()
                .doScan();
        Assert.assertThat(classSet.size(), is(0));

        classSet = new ClassScanner.ClassScannerBuilder()
                .basePackage("org.aspectj.lang")
                .recursive(true)
                .filter(new AbstractScanner.ScannerFilter() {
                    @Override
                    public boolean accept(Object o) {
                        return false;
                    }
                })
                .build()
                .doScan();
        Assert.assertThat(classSet.size(), is(0));
    }

}

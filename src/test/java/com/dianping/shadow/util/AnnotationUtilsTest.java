package com.dianping.shadow.util;

import com.dianping.shadow.sample.annotation.AnnotationAttrSuperTest;
import com.dianping.shadow.sample.annotation.AnnotationAttrTest;
import com.dianping.shadow.sample.annotation.AnnotationTest;
import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;
import com.dianping.shadow.sample.notinherited.Class2AnnotationAspect;
import com.dianping.shadow.sample.notinherited.ClassAnnotationAspect;
import com.dianping.shadow.sample.notinherited.Method2AnnotationAspect;
import com.dianping.shadow.sample.notinherited.MethodAnnotationAspect;
import com.dianping.shadow.sample.inherited.Class2InheritedAspect;
import com.dianping.shadow.sample.inherited.ClassInheritedAspect;
import com.dianping.shadow.sample.inherited.Method2InheritedAspect;
import com.dianping.shadow.sample.inherited.MethodInheritedAspect;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.core.Is.is;

/**
 * Created by jourrey on 16/11/24.
 */
public class AnnotationUtilsTest {

    @Test
    public void testParseClass() {
        Assert.assertNull(AnnotationUtils.parseClass(MethodAnnotationAspect.class, AnnotationTest.class));
        Assert.assertThat(AnnotationUtils.parseClass(ClassAnnotationAspect.class, AnnotationTest.class).name(), is("Class"));

        Assert.assertNotNull(AnnotationUtils.parseClass(MethodInheritedAspect.class, InheritedAnnotationTest.class));
        Assert.assertThat(AnnotationUtils.parseClass(ClassInheritedAspect.class, InheritedAnnotationTest.class).name(), is("Class"));
    }

    @Test
    public void testParseMethod() {
        Method method12 = ReflectionUtils.findMethod(MethodAnnotationAspect.class, "getString12", String.class);
        Method method00 = ReflectionUtils.findMethod(ClassAnnotationAspect.class, "getString00", String.class);
        Method method200 = ReflectionUtils.findMethod(Class2AnnotationAspect.class, "getString00", String.class);

        Assert.assertThat(AnnotationUtils.parseMethod(method12, AnnotationTest.class).name(), is("Method"));
        Assert.assertThat(AnnotationUtils.parseMethod(method00, AnnotationTest.class).name(), is("Class"));
        Assert.assertNull(AnnotationUtils.parseMethod(method200, AnnotationTest.class));

        Method methodInherited12 = ReflectionUtils.findMethod(MethodInheritedAspect.class, "getString12", String.class);
        Method methodInherited00 = ReflectionUtils.findMethod(ClassInheritedAspect.class, "getString00", String.class);
        Method methodInherited200 = ReflectionUtils.findMethod(Class2InheritedAspect.class, "getString00", String.class);

        Assert.assertThat(AnnotationUtils.parseMethod(methodInherited12, InheritedAnnotationTest.class).name(), is("Method"));
        Assert.assertThat(AnnotationUtils.parseMethod(methodInherited00, InheritedAnnotationTest.class).name(), is("Class"));
        Assert.assertThat(AnnotationUtils.parseMethod(methodInherited200, InheritedAnnotationTest.class).name(), is("Interface"));
    }

    @Test
    public void testFindAnnotationByAnnotation() {
        Method method12 = ReflectionUtils.findMethod(MethodAnnotationAspect.class, "getString12", String.class);

        Assert.assertThat(AnnotationUtils.getAnnotation(AnnotationUtils.parseMethod(method12, AnnotationTest.class)
                , AnnotationTest.class).name(), is("Method"));
        Assert.assertThat(AnnotationUtils.getAnnotation(AnnotationUtils.parseMethod(method12, AnnotationTest.class)
                , AnnotationAttrTest.class).name(), is("Attr"));
        Assert.assertNull(AnnotationUtils.getAnnotation(AnnotationUtils.parseMethod(method12, AnnotationTest.class), AnnotationAttrSuperTest.class));

        Assert.assertThat(AnnotationUtils.findAnnotation(AnnotationUtils.parseMethod(method12, AnnotationTest.class)
                , AnnotationTest.class).name(), is("Method"));
        Assert.assertThat(AnnotationUtils.findAnnotation(AnnotationUtils.parseMethod(method12, AnnotationTest.class)
                , AnnotationAttrTest.class).name(), is("Attr"));
        Assert.assertThat(AnnotationUtils.findAnnotation(AnnotationUtils.parseMethod(method12, AnnotationTest.class)
                , AnnotationAttrSuperTest.class).name(), is("AnnotationAttr"));
    }

    @Test
    public void testFindAnnotationMethod() {
        Method method12 = ReflectionUtils.findMethod(MethodAnnotationAspect.class, "getString12", String.class);
        Method method00 = ReflectionUtils.findMethod(ClassAnnotationAspect.class, "getString00", String.class);
        Method method01 = ReflectionUtils.findMethod(ClassAnnotationAspect.class, "getString01", String.class);

        Assert.assertThat(AnnotationUtils.getAnnotation(method12, AnnotationTest.class).name(), is("Method"));
        Assert.assertThat(AnnotationUtils.findAnnotation(method12, AnnotationTest.class).name(), is("Method"));
        Assert.assertNull(AnnotationUtils.getAnnotation(method00, AnnotationTest.class));
        Assert.assertNull(AnnotationUtils.findAnnotation(method00, AnnotationTest.class));
        Assert.assertNull(AnnotationUtils.getAnnotation(method01, AnnotationTest.class));
        Assert.assertThat(AnnotationUtils.findAnnotation(method01, AnnotationTest.class).name(), is("InterfaceMethod"));

        Method methodInherited12 = ReflectionUtils.findMethod(MethodInheritedAspect.class, "getString12", String.class);
        Method methodInherited00 = ReflectionUtils.findMethod(ClassInheritedAspect.class, "getString00", String.class);
        Method methodInherited01 = ReflectionUtils.findMethod(ClassInheritedAspect.class, "getString01", String.class);

        Assert.assertThat(AnnotationUtils.getAnnotation(methodInherited12, InheritedAnnotationTest.class).name(), is("Method"));
        Assert.assertThat(AnnotationUtils.findAnnotation(methodInherited12, InheritedAnnotationTest.class).name(), is("Method"));
        Assert.assertNull(AnnotationUtils.getAnnotation(methodInherited00, InheritedAnnotationTest.class));
        Assert.assertNull(AnnotationUtils.findAnnotation(methodInherited00, InheritedAnnotationTest.class));
        Assert.assertNull(AnnotationUtils.getAnnotation(methodInherited01, InheritedAnnotationTest.class));
        Assert.assertThat(AnnotationUtils.findAnnotation(methodInherited01, InheritedAnnotationTest.class).name(), is("InterfaceMethod"));
    }

    @Test
    public void testFindAnnotationClass() {
        Assert.assertThat(AnnotationUtils.getAnnotation(Method2AnnotationAspect.class, AnnotationTest.class).name()
                , is("Method2False"));
        Assert.assertThat(AnnotationUtils.findAnnotation(Method2AnnotationAspect.class, AnnotationTest.class).name()
                , is("Method2False"));

        Assert.assertNull(AnnotationUtils.getAnnotation(MethodAnnotationAspect.class, AnnotationTest.class));
        Assert.assertThat(AnnotationUtils.findAnnotation(MethodAnnotationAspect.class, AnnotationTest.class).name()
                , is("Class"));

        Assert.assertNull(AnnotationUtils.getAnnotation(Class2AnnotationAspect.class, AnnotationTest.class));
        Assert.assertThat(AnnotationUtils.findAnnotation(Class2AnnotationAspect.class, AnnotationTest.class).name()
                , is("Interface"));


        Assert.assertThat(AnnotationUtils.getAnnotation(Method2InheritedAspect.class, InheritedAnnotationTest.class).name()
                , is("Method2False"));
        Assert.assertThat(AnnotationUtils.findAnnotation(Method2InheritedAspect.class, InheritedAnnotationTest.class).name()
                , is("Method2False"));

        Assert.assertThat(AnnotationUtils.getAnnotation(MethodInheritedAspect.class, InheritedAnnotationTest.class).name()
                , is("Class"));
        Assert.assertThat(AnnotationUtils.findAnnotation(MethodInheritedAspect.class, InheritedAnnotationTest.class).name()
                , is("Class"));

        Assert.assertNull(AnnotationUtils.getAnnotation(Class2InheritedAspect.class, InheritedAnnotationTest.class));
        Assert.assertThat(AnnotationUtils.findAnnotation(Class2InheritedAspect.class, InheritedAnnotationTest.class).name()
                , is("Interface"));
    }
}

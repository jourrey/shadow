package com.dianping.shadow.sample.aspectconfig;

import com.dianping.shadow.sample.advice.AdviceInterceptTest;
import com.dianping.shadow.sample.annotation.AnnotationTest;
import com.dianping.shadow.annotation.AspectConfig;

/**
 * Created by jourrey on 16/8/20.
 */
@AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "Class")
public class ClassConfigAspect implements InterfaceConfigAspect {

    @Override
    public String getString00(String s) {
        return ClassConfigAspect.class.getName();
    }

    @Override
    public String getString01(String s) {
        return ClassConfigAspect.class.getName();
    }

    @Override
    @AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "ClassMethod")
    public String getString02(String s) {
        return ClassConfigAspect.class.getName();
    }

    public String getString10(String s) {
        return ClassConfigAspect.class.getName();
    }

    @AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "ClassMethod")
    public String getString11(String s) {
        return ClassConfigAspect.class.getName();
    }

    @AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "ClassMethod")
    public String getString12(String s) {
        return ClassConfigAspect.class.getName();
    }
}

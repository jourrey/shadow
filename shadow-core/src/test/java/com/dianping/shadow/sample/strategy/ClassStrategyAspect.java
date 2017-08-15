package com.dianping.shadow.sample.strategy;

import com.dianping.shadow.sample.annotation.AnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
@AnnotationTest(name = "Class")
public class ClassStrategyAspect implements InterfaceStrategyAspect {

    @Override
    public String getString00(String s) {
        return ClassStrategyAspect.class.getName();
    }

    @Override
    public String getString01(String s) {
        return ClassStrategyAspect.class.getName();
    }

    @Override
    @AnnotationTest(name = "ClassMethod")
    public String getString02(String s) {
        return ClassStrategyAspect.class.getName();
    }

    public String getString10(String s) {
        return ClassStrategyAspect.class.getName();
    }

    @AnnotationTest(name = "ClassMethod")
    public String getString11(String s) {
        return ClassStrategyAspect.class.getName();
    }

    @AnnotationTest(name = "ClassMethod")
    public String getString12(String s) {
        return ClassStrategyAspect.class.getName();
    }
}

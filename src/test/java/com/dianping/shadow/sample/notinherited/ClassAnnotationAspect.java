package com.dianping.shadow.sample.notinherited;

import com.dianping.shadow.sample.annotation.AnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
@AnnotationTest(name = "Class")
public class ClassAnnotationAspect implements InterfaceAnnotationAspect {

    @Override
    public String getString00(String s) {
        return ClassAnnotationAspect.class.getName();
    }

    @Override
    public String getString01(String s) {
        return ClassAnnotationAspect.class.getName();
    }

    @Override
    @AnnotationTest(name = "ClassMethod")
    public String getString02(String s) {
        return ClassAnnotationAspect.class.getName();
    }

    public String getString10(String s) {
        return ClassAnnotationAspect.class.getName();
    }

    @AnnotationTest(name = "ClassMethod")
    public String getString11(String s) {
        return ClassAnnotationAspect.class.getName();
    }

    @AnnotationTest(name = "ClassMethod")
    public String getString12(String s) {
        return ClassAnnotationAspect.class.getName();
    }
}

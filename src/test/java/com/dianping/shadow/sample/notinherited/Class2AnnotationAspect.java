package com.dianping.shadow.sample.notinherited;

import com.dianping.shadow.sample.annotation.AnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
public class Class2AnnotationAspect implements InterfaceAnnotationAspect {

    @Override
    public String getString00(String s) {
        return Class2AnnotationAspect.class.getName();
    }

    @Override
    public String getString01(String s) {
        return Class2AnnotationAspect.class.getName();
    }

    @Override
    @AnnotationTest(name = "Class2Method")
    public String getString02(String s) {
        return Class2AnnotationAspect.class.getName();
    }

    public String getString10(String s) {
        return Class2AnnotationAspect.class.getName();
    }

    @AnnotationTest(name = "Class2Method")
    public String getString11(String s) {
        return Class2AnnotationAspect.class.getName();
    }

    @AnnotationTest(name = "Class2Method")
    public String getString12(String s) {
        return Class2AnnotationAspect.class.getName();
    }
}

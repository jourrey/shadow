package com.dianping.shadow.sample.inherited;

import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
public class Class2InheritedAspect implements InterfaceInheritedAspect {

    @Override
    public String getString00(String s) {
        return Class2InheritedAspect.class.getName();
    }

    @Override
    public String getString01(String s) {
        return Class2InheritedAspect.class.getName();
    }

    @Override
    @InheritedAnnotationTest(name = "Class2Method")
    public String getString02(String s) {
        return Class2InheritedAspect.class.getName();
    }

    public String getString10(String s) {
        return Class2InheritedAspect.class.getName();
    }

    @InheritedAnnotationTest(name = "Class2Method")
    public String getString11(String s) {
        return Class2InheritedAspect.class.getName();
    }

    @InheritedAnnotationTest(name = "Class2Method")
    public String getString12(String s) {
        return Class2InheritedAspect.class.getName();
    }
}

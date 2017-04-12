package com.dianping.shadow.sample.inherited;

import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
@InheritedAnnotationTest(name = "Class")
public class ClassInheritedAspect implements InterfaceInheritedAspect {

    @Override
    public String getString00(String s) {
        return ClassInheritedAspect.class.getName();
    }

    @Override
    public String getString01(String s) {
        return ClassInheritedAspect.class.getName();
    }

    @Override
    @InheritedAnnotationTest(name = "ClassMethod")
    public String getString02(String s) {
        return ClassInheritedAspect.class.getName();
    }

    public String getString10(String s) {
        return ClassInheritedAspect.class.getName();
    }

    @InheritedAnnotationTest(name = "ClassMethod")
    public String getString11(String s) {
        return ClassInheritedAspect.class.getName();
    }

    @InheritedAnnotationTest(name = "ClassMethod")
    public String getString12(String s) {
        return ClassInheritedAspect.class.getName();
    }
}

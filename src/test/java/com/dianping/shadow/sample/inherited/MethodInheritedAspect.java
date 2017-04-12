package com.dianping.shadow.sample.inherited;

import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
public class MethodInheritedAspect extends ClassInheritedAspect {

    @InheritedAnnotationTest(name = "Method")
    public String getString12(String s) {
        return MethodInheritedAspect.class.getName();
    }

    public String getString20(String s) {
        return MethodInheritedAspect.class.getName();
    }

    @InheritedAnnotationTest(name = "Method")
    public String getString21(String s) {
        return MethodInheritedAspect.class.getName();
    }

    @InheritedAnnotationTest(name = "Method")
    public void getString22() {

    }
}

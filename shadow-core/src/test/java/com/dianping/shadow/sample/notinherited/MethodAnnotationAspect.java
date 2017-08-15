package com.dianping.shadow.sample.notinherited;

import com.dianping.shadow.sample.annotation.AnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
public class MethodAnnotationAspect extends ClassAnnotationAspect {

    @AnnotationTest(name = "Method")
    public String getString12(String s) {
        return MethodAnnotationAspect.class.getName();
    }

    public String getString20(String s) {
        return MethodAnnotationAspect.class.getName();
    }

    @AnnotationTest(name = "Method")
    public String getString21(String s) {
        return MethodAnnotationAspect.class.getName();
    }

    @AnnotationTest(name = "Method")
    public void getString22() {

    }
}

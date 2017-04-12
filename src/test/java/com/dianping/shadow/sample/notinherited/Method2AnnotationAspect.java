package com.dianping.shadow.sample.notinherited;

import com.dianping.shadow.sample.annotation.AnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
@AnnotationTest(value = false, name = "Method2False")
public class Method2AnnotationAspect extends Class2AnnotationAspect {

    @AnnotationTest(name = "Method2")
    public String getString12(String s) {
        return Method2AnnotationAspect.class.getName();
    }

    public String getString20(String s) {
        return Method2AnnotationAspect.class.getName();
    }


    @AnnotationTest(name = "Method2")
    public String getString21(String s) {
        return Method2AnnotationAspect.class.getName();
    }

    @AnnotationTest(name = "Method2")
    public void getString22() {

    }
}

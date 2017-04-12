package com.dianping.shadow.sample.inherited;

import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
@InheritedAnnotationTest(value = false, name = "Method2False")
public class Method2InheritedAspect extends Class2InheritedAspect {

    @InheritedAnnotationTest(name = "Method2")
    public String getString12(String s) {
        return Method2InheritedAspect.class.getName();
    }

    public String getString20(String s) {
        return Method2InheritedAspect.class.getName();
    }


    @InheritedAnnotationTest(name = "Method2")
    public String getString21(String s) {
        return Method2InheritedAspect.class.getName();
    }

    @InheritedAnnotationTest(name = "Method2")
    public void getString22() {

    }
}

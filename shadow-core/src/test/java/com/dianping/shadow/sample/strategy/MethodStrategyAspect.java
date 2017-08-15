package com.dianping.shadow.sample.strategy;

import com.dianping.shadow.sample.annotation.AnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
public class MethodStrategyAspect extends ClassStrategyAspect {

    @AnnotationTest(name = "Method")
    public String getString12(String s) {
        return MethodStrategyAspect.class.getName();
    }

    public String getString20(String s) {
        return MethodStrategyAspect.class.getName();
    }

    @AnnotationTest(name = "Method")
    public String getString21(String s) {
        return MethodStrategyAspect.class.getName();
    }

    @AnnotationTest(name = "Method")
    public void getString22() {

    }
}

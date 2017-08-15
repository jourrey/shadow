package com.dianping.shadow.sample.strategy;

import com.dianping.shadow.sample.annotation.AnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
public class Class2StrategyAspect implements InterfaceStrategyAspect {

    @Override
    public String getString00(String s) {
        return Class2StrategyAspect.class.getName();
    }

    @Override
    public String getString01(String s) {
        return Class2StrategyAspect.class.getName();
    }

    @Override
    @AnnotationTest(name = "Class2Method")
    public String getString02(String s) {
        return Class2StrategyAspect.class.getName();
    }

    public String getString10(String s) {
        return Class2StrategyAspect.class.getName();
    }

    @AnnotationTest(name = "Class2Method")
    public String getString11(String s) {
        return Class2StrategyAspect.class.getName();
    }

    @AnnotationTest(name = "Class2Method")
    public String getString12(String s) {
        return Class2StrategyAspect.class.getName();
    }
}

package com.dianping.shadow.sample.strategy;

import com.dianping.shadow.sample.annotation.AnnotationTest;
import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
@AnnotationTest(name = "Interface")
@InheritedAnnotationTest(name = "Interface")
public interface InterfaceStrategyAspect {

    String getString00(String s);

    @AnnotationTest(name = "InterfaceMethod")
    String getString01(String s);

    @AnnotationTest(name = "InterfaceMethod")
    String getString02(String s);
}

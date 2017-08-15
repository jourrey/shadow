package com.dianping.shadow.sample.inherited;

import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
@InheritedAnnotationTest(name = "Interface")
public interface InterfaceInheritedAspect {

    String getString00(String s);

    @InheritedAnnotationTest(name = "InterfaceMethod")
    String getString01(String s);

    @InheritedAnnotationTest(name = "InterfaceMethod")
    String getString02(String s);
}

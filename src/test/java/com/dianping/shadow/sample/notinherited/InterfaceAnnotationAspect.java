package com.dianping.shadow.sample.notinherited;

import com.dianping.shadow.sample.annotation.AnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
@AnnotationTest(name = "Interface")
public interface InterfaceAnnotationAspect {

    String getString00(String s);

    @AnnotationTest(name = "InterfaceMethod")
    String getString01(String s);

    @AnnotationTest(name = "InterfaceMethod")
    String getString02(String s);
}

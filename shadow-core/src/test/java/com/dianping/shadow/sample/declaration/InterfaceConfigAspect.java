package com.dianping.shadow.sample.declaration;

import com.dianping.shadow.sample.advice.AdviceInterceptTest;
import com.dianping.shadow.sample.annotation.AnnotationTest;
import com.dianping.shadow.declaration.statement.AspectConfig;

/**
 * Created by jourrey on 16/8/20.
 */
@AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "Interface", order = 1)
public interface InterfaceConfigAspect {

    String getString00(String s);

    @AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "InterfaceMethod")
    String getString01(String s);

    @AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "InterfaceMethod")
    String getString02(String s);
}

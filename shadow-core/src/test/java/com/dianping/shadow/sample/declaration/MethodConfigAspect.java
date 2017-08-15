package com.dianping.shadow.sample.declaration;

import com.dianping.shadow.sample.advice.AdviceInterceptBuilderTest;
import com.dianping.shadow.sample.advice.AdviceInterceptTest;
import com.dianping.shadow.sample.annotation.AnnotationTest;
import com.dianping.shadow.declaration.statement.AspectConfig;
import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
public class MethodConfigAspect extends ClassConfigAspect {

    @AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "Method")
    public String getString12(String s) {
        return MethodConfigAspect.class.getName();
    }

    public String getString20(String s) {
        return MethodConfigAspect.class.getName();
    }

    @AspectConfig(annotation = InheritedAnnotationTest.class, advice = AdviceInterceptBuilderTest.class, extendInfo = "Method")
    public String getString21(String s) {
        return MethodConfigAspect.class.getName();
    }

    @AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "Method")
    public void getString22() {

    }
}

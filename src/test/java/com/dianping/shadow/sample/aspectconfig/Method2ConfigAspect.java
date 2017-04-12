package com.dianping.shadow.sample.aspectconfig;

import com.dianping.shadow.sample.advice.AdviceInterceptBuilderTest;
import com.dianping.shadow.sample.advice.AdviceInterceptTest;
import com.dianping.shadow.sample.annotation.AnnotationTest;
import com.dianping.shadow.annotation.AspectConfig;
import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;

/**
 * Created by jourrey on 16/8/20.
 */
@AspectConfig(value = false, extendInfo = "Method2False")
public class Method2ConfigAspect extends Class2ConfigAspect {

    @AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "Method2")
    public String getString12(String s) {
        return Method2ConfigAspect.class.getName();
    }

    public String getString20(String s) {
        return Method2ConfigAspect.class.getName();
    }

    @AspectConfig(annotation = InheritedAnnotationTest.class, advice = AdviceInterceptBuilderTest.class
            , extendInfo = "Method2", value = false)
    public String getString21(String s) {
        return Method2ConfigAspect.class.getName();
    }

    @AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class, extendInfo = "Method2")
    public void getString22() {

    }
}

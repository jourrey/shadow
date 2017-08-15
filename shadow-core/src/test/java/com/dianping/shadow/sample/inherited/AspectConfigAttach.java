package com.dianping.shadow.sample.inherited;

import com.dianping.shadow.sample.advice.AdviceInterceptBuilderTest;
import com.dianping.shadow.declaration.statement.AspectConfig;
import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;

/**
 * Created by jourrey on 17/2/6.
 */
public interface AspectConfigAttach {

    @AspectConfig(annotation = InheritedAnnotationTest.class, advice = AdviceInterceptBuilderTest.class)
    void attach();

}

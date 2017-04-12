package com.dianping.shadow.sample.strategy;

import com.dianping.shadow.sample.advice.AdviceInterceptTest;
import com.dianping.shadow.sample.annotation.AnnotationTest;
import com.dianping.shadow.annotation.AspectConfig;

/**
 * Created by jourrey on 17/2/6.
 */
public interface AspectConfigAttach {

    @AspectConfig(annotation = AnnotationTest.class, advice = AdviceInterceptTest.class)
    void attach();

}

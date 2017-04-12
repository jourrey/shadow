package com.dianping.shadow.sample.advice;

import com.dianping.shadow.advice.AdviceInterceptBuilder;
import com.dianping.shadow.advice.AspectParam;
import com.dianping.shadow.context.AspectDefinition;
import com.dianping.shadow.sample.annotation.InheritedAnnotationTest;

/**
 * Created by jourrey on 16/11/24.
 */
public class AdviceInterceptBuilderTest extends AdviceInterceptTest implements AdviceInterceptBuilder<InheritedAnnotationTest> {

    private String name = "builder";

    @Override
    public String initAndReturnUniqueKey(AspectDefinition aspectDefinition, InheritedAnnotationTest annotation) {
        name = annotation.name();
        return name;
    }

    @Override
    public void before(AspectParam param) {
        param.setResult(name);
        super.before(param);
    }

    @Override
    public void afterReturning(AspectParam param) {
        param.setResult(name);
        super.afterReturning(param);
    }

    @Override
    public void afterThrowing(AspectParam param) {
        param.setResult(name);
        super.afterThrowing(param);
    }

    @Override
    public void afterFinally(AspectParam param) {
        param.setResult(name);
        super.afterFinally(param);
    }

}

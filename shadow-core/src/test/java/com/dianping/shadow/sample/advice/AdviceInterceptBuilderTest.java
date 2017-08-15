package com.dianping.shadow.sample.advice;

import com.dianping.shadow.advice.AdviceInterceptBuilder;
import com.dianping.shadow.aspect.AspectParam;
import com.dianping.shadow.context.PointcutBean;
import com.dianping.shadow.declaration.AspectDefinition;
import com.dianping.shadow.util.AnnotationUtils;

/**
 * Created by jourrey on 16/11/24.
 */
public class AdviceInterceptBuilderTest extends AdviceInterceptTest implements AdviceInterceptBuilder {

    private String name = "builder";

    @Override
    public String initAndReturnUniqueKey(AspectDefinition aspectDefinition, PointcutBean pointcutBean) {
        name = (String) AnnotationUtils.getValue(pointcutBean.getAnnotation(), "name");
        return name;
    }

    @Override
    public void before(AspectParam aspectParam) {
        aspectParam.setResult(name);
        super.before(aspectParam);
    }

    @Override
    public void afterReturning(AspectParam aspectParam) {
        aspectParam.setResult(name);
        super.afterReturning(aspectParam);
    }

    @Override
    public void afterThrowing(AspectParam aspectParam) {
        aspectParam.setResult(name);
        super.afterThrowing(aspectParam);
    }

    @Override
    public void afterFinally(AspectParam aspectParam) {
        aspectParam.setResult(name);
        super.afterFinally(aspectParam);
    }

}

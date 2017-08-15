package com.dianping.shadow.sample.advice;

import com.dianping.shadow.advice.AdviceIntercept;
import com.dianping.shadow.aspect.AspectParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jourrey on 16/11/24.
 */
public class AdviceInterceptTest implements AdviceIntercept {
    private static final Logger LOG = LoggerFactory.getLogger(AdviceInterceptTest.class);

    @Override
    public void before(AspectParam aspectParam) {
        LOG.error("before {}", paramToString(aspectParam));
    }

    @Override
    public void afterReturning(AspectParam aspectParam) {
        LOG.error("afterReturning {}", paramToString(aspectParam));
    }

    @Override
    public void afterThrowing(AspectParam aspectParam) {
        LOG.error("afterThrowing {}", paramToString(aspectParam));
    }

    @Override
    public void afterFinally(AspectParam aspectParam) {
        LOG.error("afterFinally {}", paramToString(aspectParam));
    }

    private String paramToString(AspectParam param) {
        return new StringBuilder().append("_" + param.getToken())
                .append("_" + param.getHierarchy())
                .append("_" + param.getSequence())
                .append("_" + param.getJoinPointBean().getDeclaringClass().getSimpleName())
                .append("_" + param.getJoinPointBean().getMember().getName())
                .append("_" + param.getPointcutBean().getAnnotation().annotationType().getSimpleName())
                .append("_" + param.getParameters())
                .append("_" + param.getResult())
                .append("_" + param.getThrowable())
                .append("_" + param.getExtendInfo())
                .toString();
    }
}

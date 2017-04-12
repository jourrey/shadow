package com.dianping.shadow.sample.advice;

import com.dianping.shadow.advice.AdviceIntercept;
import com.dianping.shadow.advice.AspectParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by jourrey on 16/11/24.
 */
public class AdviceInterceptTest implements AdviceIntercept {
    private static final Logger LOG = LogManager.getLogger(AdviceInterceptTest.class);

    @Override
    public void before(AspectParam param) {
        LOG.error("before {}", paramToString(param));
    }

    @Override
    public void afterReturning(AspectParam param) {
        LOG.error("afterReturning {}", paramToString(param));
    }

    @Override
    public void afterThrowing(AspectParam param) {
        LOG.error("afterThrowing {}", paramToString(param));
    }

    @Override
    public void afterFinally(AspectParam param) {
        LOG.error("afterFinally {}", paramToString(param));
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

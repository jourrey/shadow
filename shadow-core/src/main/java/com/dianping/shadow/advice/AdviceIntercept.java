package com.dianping.shadow.advice;

import com.dianping.shadow.aspect.AspectParam;

/**
 * 切面通知
 * Created by jourrey on 16/9/24.
 */
public interface AdviceIntercept {

    /**
     * Advice before
     *
     * @param aspectParam Aspect参数
     * @returnType: void
     */
    void before(AspectParam aspectParam);

    /**
     * Advice after returning
     *
     * @param aspectParam Aspect参数
     * @returnType: void
     */
    void afterReturning(AspectParam aspectParam);

    /**
     * Advice after throwing
     *
     * @param aspectParam Aspect参数
     * @returnType: void
     */
    void afterThrowing(AspectParam aspectParam);

    /**
     * Advice after finally
     *
     * @param aspectParam Aspect参数
     * @returnType: void
     */
    void afterFinally(AspectParam aspectParam);

}

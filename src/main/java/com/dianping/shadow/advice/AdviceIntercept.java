package com.dianping.shadow.advice;

/**
 * 切面通知
 * Created by jourrey on 16/9/24.
 */
public interface AdviceIntercept {

    /**
     * Advice before
     *
     * @param param Aspect参数
     * @returnType: void
     */
    void before(AspectParam param);

    /**
     * Advice after returning
     *
     * @param param Aspect参数
     * @returnType: void
     */
    void afterReturning(AspectParam param);

    /**
     * Advice after throwing
     *
     * @param param Aspect参数
     * @returnType: void
     */
    void afterThrowing(AspectParam param);

    /**
     * Advice after finally
     *
     * @param param Aspect参数
     * @returnType: void
     */
    void afterFinally(AspectParam param);

}

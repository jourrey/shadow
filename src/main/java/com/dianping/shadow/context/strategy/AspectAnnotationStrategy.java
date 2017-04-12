package com.dianping.shadow.context.strategy;

import com.google.common.base.Optional;

import java.lang.annotation.Annotation;

/**
 * 切面注解策略选择器
 * Created by jourrey on 17/2/6.
 */
public interface AspectAnnotationStrategy {

    /**
     * 根据当前注解类型,选择一个替换的注解类型
     * 返回Optional.absent()表示放弃当前Strategy,跳至下一个Strategy
     *
     * @param annotationType
     * @return
     */
    Optional<? extends Class<? extends Annotation>> choose(Class<? extends Annotation> annotationType);
}

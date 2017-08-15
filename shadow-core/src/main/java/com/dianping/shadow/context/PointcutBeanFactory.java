package com.dianping.shadow.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 切点的定义类
 *
 * @author jourrey
 */
public class PointcutBeanFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PointcutBeanFactory.class);

    /**
     * 创建切点
     *
     * @param annotation
     * @returnType: PointcutBean
     */
    public static PointcutBean create(Annotation annotation) {
        LOG.debug("annotation:{}", annotation);
        checkNotNull(annotation, "annotation can not null");
        return new PointcutBean(annotation);
    }

}

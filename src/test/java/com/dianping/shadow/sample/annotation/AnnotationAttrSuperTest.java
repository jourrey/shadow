package com.dianping.shadow.sample.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jourrey on 16/11/24.
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AnnotationAttrTest(name = "AnnotationAttrSuper")
public @interface AnnotationAttrSuperTest {

    /**
     * 名称
     */
    String name() default "AnnotationAttrSuper";

    /**
     * 是否启用注解, 默认是true启用注解
     *
     * @return boolean 返回类型
     * @throws
     */
    boolean value() default true;

}

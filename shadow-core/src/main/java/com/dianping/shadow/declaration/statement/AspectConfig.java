package com.dianping.shadow.declaration.statement;

import com.dianping.shadow.advice.AdviceIntercept;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 切面注解，被标注的类或者方法将执行切面调用
 *
 * @author jourrey
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AspectConfig {

    /**
     * 连接点,切面监听的条件为该注解
     *
     * @returnType: Class<? extends Annotation>
     */
    Class<? extends Annotation> annotation() default Annotation.class;

    /**
     * 织入,切面执行逻辑
     *
     * @returnType: Class<? extends AdviceIntercept>
     */
    Class<? extends AdviceIntercept> advice() default AdviceIntercept.class;

    /**
     * 织入类型,切面执行的类型
     *
     * @return
     */
    AdviceType[] adviceType() default AdviceType.AROUND;

    /**
     * 顺序,切面之行的顺序
     *
     * @return
     */
    int order() default 0;

    /**
     * 扩展信息，默认是无扩展信息
     *
     * @return String 返回类型
     * @throws
     */
    String extendInfo() default "";

    /**
     * 是否启用注解, 默认是true启用注解
     *
     * @return boolean 返回类型
     * @throws
     */
    boolean value() default true;

}

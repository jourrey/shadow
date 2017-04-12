package com.dianping.shadow.aspect.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 这里根据各业务自己扩展,定义规范,不属于框架范畴,仅示例而已
 * 基于注解的Spring AOP的切面
 * 这里并没有使用@Component标记为Spring的Bean,是因为不希望被自动加载,允许可配
 * 特别需要注意的是这里只能使用@Around
 * 示例:
 * <aop:aspectj-autoproxy/>
 * <bean class="com.dianping.shadow.aspect.spring.AnnotationAspectSpringInterceptor" />
 *
 * @author jourrey
 */
@Aspect
public class AnnotationAspectSpringInterceptor extends AspectSpringInterceptor {

    /**
     * 定义Pointcut，该方法就是一个标识，不进行调用
     * 知道这里为什么不写成execution(* *..*.*(..))吗? {@See AspectSpringInterceptor类的注释}
     *
     * @returnType: void
     */
    @Pointcut("execution(public * com.dianping..service..*.*(..))")
    public void aspectjMethod() {
    }

    /**
     * 定义Advice，该方法就是一个标识，不进行调用
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("aspectjMethod()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        return advice(pjp);
    }

}
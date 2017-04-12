package com.dianping.shadow.aspect.aspectj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * AspectJ AOP的切面
 * 需要AspectJ类型项目编译
 * 可自行查阅
 * http://www.eclipse.org/aspectj/
 * http://blog.csdn.net/zl3450341/article/details/7673938
 * 具体使用示例:
 * public aspect DemoAspect {
 *     pointcut DemoPointCut() : execution(* *..*.*(..));
 *     around() : DemoPointCut(){
 *         new AspectAspectJInterceptor().advice(thisJoinPoint);
 *     }
 * }
 *
 * @author jourrey
 */
@Aspect
public class AspectAspectJInterceptor {
    private static final Logger LOG = LogManager.getLogger(AspectAspectJInterceptor.class);

    /**
     * 定义Pointcut，该方法就是一个标识，不进行调用
     *
     * @returnType: void
     */
    @Pointcut("execution(* *..*.*(..))")
    public void aspectjMethod() {
    }

    /**
     * 定义Advice类型为Around，切面处理
     *
     * @param pjp
     * @throws Throwable
     * @returnType: Object
     */
    @Around("aspectjMethod()")
    public Object advice(ProceedingJoinPoint pjp) throws Throwable {
        LOG.debug("enter AspectAspectJInterceptor");
        Object result;
        try {
            result = ProceedingJoinPointAspectInterceptor.INTERCEPTOR.getInterceptor().advice(pjp);
        } catch (Throwable th) {
            throw th;
        }
        LOG.debug("exit AspectAspectJInterceptor");
        return result;
    }

}
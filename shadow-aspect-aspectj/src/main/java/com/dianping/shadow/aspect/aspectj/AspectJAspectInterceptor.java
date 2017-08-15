package com.dianping.shadow.aspect.aspectj;

import com.dianping.shadow.aspect.AspectInterceptor;
import com.dianping.shadow.common.exception.CreateJoinPointBeanException;
import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.context.JoinPointBeanFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * AspectJ AOP的切面
 * 需要AspectJ类型项目编译
 * 可自行查阅
 * http://www.eclipse.org/aspectj/
 * http://blog.csdn.net/zl3450341/article/details/7673938
 * 具体使用示例:
 * public aspect DemoAspect {
 * pointcut DemoPointCut() : execution(* *..*.*(..));
 * around() : DemoPointCut(){
 * new AspectJAspectInterceptor().advice(thisJoinPoint);
 * }
 * }
 *
 * @author jourrey
 */
@Aspect
public class AspectJAspectInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(AspectJAspectInterceptor.class);

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
        LOG.debug("enter AspectJAspectInterceptor");
        Object result;
        try {
            result = ProceedingJoinPointAspectInterceptor.INTERCEPTOR.interceptor.advice(pjp);
        } catch (Throwable th) {
            throw th;
        }
        LOG.debug("exit AspectJAspectInterceptor");
        return result;
    }

    private enum ProceedingJoinPointAspectInterceptor {

        INTERCEPTOR(new AspectInterceptor<ProceedingJoinPoint>() {

            @Override
            protected JoinPointBean getJoinPointBean(ProceedingJoinPoint proceedingJoinPoint) {
                JoinPointBean joinPointBean = null;
                try {
                    MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
                    Object target = proceedingJoinPoint.getTarget();
                    try {
                        joinPointBean = JoinPointBeanFactory.create(target.getClass()
                                .getMethod(signature.getName(), signature.getParameterTypes()));
                    } catch (NoSuchMethodException e) {
                        joinPointBean = JoinPointBeanFactory.create(signature.getMethod());
                    }
                } catch (Throwable th) {
                    new CreateJoinPointBeanException("ProceedingJoinPointAspectInterceptor error", th);
                }
                return joinPointBean;
            }

            @Override
            protected Map<String, Object> getParameters(ProceedingJoinPoint proceedingJoinPoint) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < proceedingJoinPoint.getArgs().length; i++) {
                    map.put(String.valueOf(i), proceedingJoinPoint.getArgs()[i]);
                }
                return map;
            }

            @Override
            protected Object getResult(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
                return proceedingJoinPoint.proceed();
            }
        });

        private final AspectInterceptor<ProceedingJoinPoint> interceptor;

        ProceedingJoinPointAspectInterceptor(AspectInterceptor<ProceedingJoinPoint> interceptor) {
            this.interceptor = interceptor;
        }

    }

}
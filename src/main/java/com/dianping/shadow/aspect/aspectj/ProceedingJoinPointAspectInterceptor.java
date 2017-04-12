package com.dianping.shadow.aspect.aspectj;

import com.dianping.shadow.common.exception.CreateJoinPointBeanException;
import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.context.JoinPointBeanFactory;
import com.dianping.shadow.aspect.AspectInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jourrey on 16/11/6.
 */
public enum ProceedingJoinPointAspectInterceptor {

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

    public AspectInterceptor<ProceedingJoinPoint> getInterceptor() {
        return interceptor;
    }

}

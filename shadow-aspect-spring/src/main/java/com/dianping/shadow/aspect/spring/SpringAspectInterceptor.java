package com.dianping.shadow.aspect.spring;

import com.dianping.shadow.aspect.AspectInterceptor;
import com.dianping.shadow.common.exception.CreateJoinPointBeanException;
import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.context.JoinPointBeanFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring AOP的切面
 * 这个切面需要手动设置,否则会有一些问题.例如设置 @Pointcut("execution(* *..*.*(..))") 时,被覆盖的类,Spring会为其构建一个Bean.
 * 此时,如若这个类没有实现任意 接口 且又是 final 的,那么Bean会构建失败,导致应用无法启动,异常如下
 * org.springframework.aop.framework.AopConfigException: Could not generate CGLIB subclass of class [class xxx.xxx.xxx]
 * <p/>
 * 这里提供两个模版
 * 1.基于注解,参照{@link AnnotationSpringAspectInterceptor}
 * 2.基于XML
 * <!-- 日志拦截声明bean ，此bean作为切面类使用  -->
 * <bean id="logInterceptor" class="com.dianping.shadow.aspect.spring.SpringAspectInterceptor"/>
 * <aop:config>
 * <!-- 设置切面名，及切面类 -->
 * <aop:aspect id="logAspect" ref="logInterceptor">
 * <!-- 先设置切入点，待使用  -->
 * <aop:pointcut id="logPointcut" expression="execution(public * com.dianping..service..*.*(..))"/>
 * <!-- 运行前后方法配置，选择要执行的方法，参考预先设置好的切入点  -->
 * <aop:around method="advice" pointcut-ref="logPointcut"/>
 * </aop:aspect>
 * </aop:config>
 * <p/>
 * 特别需要注意的是这里只能使用<aop:around></aop:around>
 *
 * @author jourrey
 */
public class SpringAspectInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(SpringAspectInterceptor.class);

    /**
     * 切面处理
     *
     * @param pjp
     * @throws Throwable
     * @returnType: Object
     */
    public Object advice(ProceedingJoinPoint pjp) throws Throwable {
        LOG.debug("enter SpringAspectInterceptor");
        Object result;
        try {
            result = ProceedingJoinPointAspectInterceptor.INTERCEPTOR.interceptor.advice(pjp);
        } catch (Throwable th) {
            throw th;
        }
        LOG.debug("exit SpringAspectInterceptor");
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
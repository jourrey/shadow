package com.dianping.shadow.aspect.spring;

import com.dianping.shadow.aspect.aspectj.ProceedingJoinPointAspectInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Spring AOP的切面
 * 这个切面需要手动设置,否则会有一些问题.例如设置 @Pointcut("execution(* *..*.*(..))") 时,被覆盖的类,Spring会为其构建一个Bean.
 * 此时,如若这个类没有实现任意 接口 且又是 final 的,那么Bean会构建失败,导致应用无法启动,异常如下
 * org.springframework.aop.framework.AopConfigException: Could not generate CGLIB subclass of class [class xxx.xxx.xxx]
 * <p/>
 * 这里提供两个模版
 * 1.基于注解,参照{@link AnnotationAspectSpringInterceptor}
 * 2.基于XML
 * <!-- 日志拦截声明bean ，此bean作为切面类使用  -->
 * <bean id="logInterceptor" class="com.dianping.shadow.aspect.spring.AspectSpringInterceptor"/>
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
public class AspectSpringInterceptor {
    private static final Logger LOG = LogManager.getLogger(AspectSpringInterceptor.class);

    /**
     * 切面处理
     *
     * @param pjp
     * @throws Throwable
     * @returnType: Object
     */
    public Object advice(ProceedingJoinPoint pjp) throws Throwable {
        LOG.debug("enter AspectSpringInterceptor");
        Object result;
        try {
            result = ProceedingJoinPointAspectInterceptor.INTERCEPTOR.getInterceptor().advice(pjp);
        } catch (Throwable th) {
            throw th;
        }
        LOG.debug("exit AspectSpringInterceptor");
        return result;
    }

}
package com.dianping.shadow.aspect.instrument;

import com.dianping.shadow.aspect.AspectInterceptor;
import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.util.ReflectionUtils;
import javassist.CtMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Instrument AOP的切面
 *
 * @author jourrey
 */
public class AspectInstrumentInterceptor {
    private static final Logger LOG = LogManager.getLogger(AspectInstrumentInterceptor.class);

    /**
     * 切面处理
     *
     * @param ctMethod
     * @return
     * @throws Exception
     */
    public Object intercept(CtMethod ctMethod) throws Exception {
        LOG.debug("enter AspectInstrumentInterceptor");
        Object result;
        try {
            result = CtMethodAspectInterceptor.INTERCEPTOR.interceptor.advice(ctMethod);
        } catch (Throwable th) {
            throw (Exception) th;
        }
        LOG.debug("exit AspectInstrumentInterceptor");
        return result;
    }

    public enum CtMethodAspectInterceptor {
        INTERCEPTOR(new AspectInterceptor<CtMethod>() {

            @Override
            protected JoinPointBean getJoinPointBean(CtMethod ctMethod) {
//                return JoinPointBeanFactory.create(getMethod(actionInvocation.getClass()
//                        , actionInvocation.getProxy().getMethod()));
                return null;
            }

            private Method getMethod(CtMethod ctMethod) {
//                return ReflectionUtils.findMethod(clazz, methodName);
                return null;
            }

            @Override
            protected Map<String, Object> getParameters(CtMethod actionInvocation) {
//                return actionInvocation.getInvocationContext().getParameters();
                return null;
            }

            @Override
            protected Object getResult(CtMethod actionInvocation) throws Throwable {
//                return actionInvocation.invoke();
                return null;
            }
        });

        private final AspectInterceptor<CtMethod> interceptor;

        CtMethodAspectInterceptor(AspectInterceptor<CtMethod> interceptor) {
            this.interceptor = interceptor;
        }
    }
}

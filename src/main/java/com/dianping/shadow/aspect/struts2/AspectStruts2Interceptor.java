package com.dianping.shadow.aspect.struts2;

import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.context.JoinPointBeanFactory;
import com.dianping.shadow.aspect.AspectInterceptor;
import com.dianping.shadow.util.ReflectionUtils;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Struts2 AOP的切面
 *
 * @author jourrey
 */
public class AspectStruts2Interceptor extends AbstractInterceptor {
    private static final long serialVersionUID = 2351418455263140339L;
    private static final Logger LOG = LogManager.getLogger(AspectStruts2Interceptor.class);

    /**
     * 切面处理
     *
     * @param invocation
     * @return
     * @throws Exception
     */
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.debug("enter AspectStruts2Interceptor");
        Object result;
        try {
            result = ActionInvocationAspectInterceptor.INTERCEPTOR.interceptor.advice(invocation);
        } catch (Throwable th) {
            throw (Exception) th;
        }
        LOG.debug("exit AspectStruts2Interceptor");
        return (String) result;
    }

    public enum ActionInvocationAspectInterceptor {
        INTERCEPTOR(new AspectInterceptor<ActionInvocation>() {

            @Override
            protected JoinPointBean getJoinPointBean(ActionInvocation actionInvocation) {
                return JoinPointBeanFactory.create(getMethod(actionInvocation.getAction().getClass()
                        , actionInvocation.getProxy().getMethod()));
            }

            /**
             * Struts2的Action类入口,截至目前只有两种形式:
             * 1.实现com.opensymphony.xwork2.Action接口，并实现该接口中的execute()方法。
             * 该方法如下：
             * public String execute() throws Exception
             * 2.直接编写一个普通的Java类作为Action，只要实现一个返回类型为String的无参的public方法即可.
             * 该方法如下：
             * public String  xxx()
             * 所以直接读取无参数方法
             *
             * @param clazz
             * @param methodName
             * @return
             * @throws NoSuchMethodException
             */
            private Method getMethod(Class clazz, String methodName) {
                return ReflectionUtils.findMethod(clazz, methodName);
            }

            @Override
            protected Map<String, Object> getParameters(ActionInvocation actionInvocation) {
                return actionInvocation.getInvocationContext().getParameters();
            }

            @Override
            protected Object getResult(ActionInvocation actionInvocation) throws Throwable {
                return actionInvocation.invoke();
            }
        });

        private final AspectInterceptor<ActionInvocation> interceptor;

        ActionInvocationAspectInterceptor(AspectInterceptor<ActionInvocation> interceptor) {
            this.interceptor = interceptor;
        }
    }
}

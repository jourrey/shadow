package com.dianping.shadow.aspect.instrument;

import com.dianping.shadow.aspect.AspectInterceptor;
import com.dianping.shadow.aspect.instrument.transtormer.JavassistTransformer;
import com.dianping.shadow.common.exception.CreateJoinPointBeanException;
import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.context.JoinPointBeanFactory;
import com.dianping.shadow.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * instrument AOP的切面
 *
 * @author jourrey
 */
public class InstrumentAspectInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(InstrumentAspectInterceptor.class);

    /**
     * 切面处理
     * {@link JavassistTransformer#transform}在使用
     *
     * @param invocation
     * @return
     * @throws Exception
     */
    public static Object intercept(AgentInvocation invocation) throws Throwable {
        LOG.debug("enter InstrumentAspectInterceptor");
        Object result;
        try {
//            System.out.println(JSON.toJSONString(invocation));
            result = AgentInvocationAspectInterceptor.INTERCEPTOR.interceptor.advice(invocation);
        } catch (Throwable th) {
            throw th;
        }
        LOG.debug("exit InstrumentAspectInterceptor");
        return result;
    }

    private enum AgentInvocationAspectInterceptor {

        INTERCEPTOR(new AspectInterceptor<AgentInvocation>() {

            @Override
            protected JoinPointBean getJoinPointBean(AgentInvocation agentInvocation) {
                JoinPointBean joinPointBean = null;
                try {
                    joinPointBean = JoinPointBeanFactory.create(ReflectionUtils.findMethod(agentInvocation.getTargetClass()
                            , agentInvocation.getTargetMethodName(), agentInvocation.getParamTypes()));
                } catch (Throwable th) {
                    new CreateJoinPointBeanException("AgentInvocationAspectInterceptor error", th);
                }
                return joinPointBean;
            }

            @Override
            protected Map<String, Object> getParameters(AgentInvocation agentInvocation) {
                if (agentInvocation.getParams() == null) {
                    return Collections.emptyMap();
                }
                Map<String, Object> map = new HashMap<String, Object>();
                if (agentInvocation.getParamNames() != null
                        && agentInvocation.getParamNames().length == agentInvocation.getParams().length) {
                    for (int i = 0; i < agentInvocation.getParams().length; i++) {
                        map.put(agentInvocation.getParamNames()[i], agentInvocation.getParams()[i]);
                    }
                } else {
                    for (int i = 0; i < agentInvocation.getParams().length; i++) {
                        map.put(String.valueOf(i), agentInvocation.getParams()[i]);
                    }
                }
                return map;
            }

            @Override
            protected Object getResult(AgentInvocation agentInvocation) throws Throwable {
                Method method = ReflectionUtils.findMethod(agentInvocation.getTargetClass()
                        , agentInvocation.getTargetMethodName(), agentInvocation.getParamTypes());
                method.setAccessible(true);
                return ReflectionUtils.invokeMethod(method, agentInvocation.getTarget(), agentInvocation.getParams());
            }
        });

        private final AspectInterceptor<AgentInvocation> interceptor;

        AgentInvocationAspectInterceptor(AspectInterceptor<AgentInvocation> interceptor) {
            this.interceptor = interceptor;
        }

    }

}

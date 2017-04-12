package com.dianping.shadow.aspect;

import com.dianping.shadow.context.JoinPointBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * AOP的切面
 *
 * @author jourrey
 */
public abstract class AspectInterceptor<T> {
    private static final Logger LOG = LogManager.getLogger(AspectInterceptor.class);

    /**
     * 切面处理
     *
     * @param t
     * @throws Throwable
     * @returnType: Object
     */
    public Object advice(T t) throws Throwable {
        LOG.debug("enter {} advice", this.getClass());
        Object result = null;
        Map<String, Object> map = null;
        AspectStackContext context = null;
        JoinPointBean joinPointBean;
        try {
            joinPointBean = getJoinPointBean(t);
            context = AspectStackContext.builder(joinPointBean);
            map = getParameters(t);
        } catch (Throwable th) {
            LOG.error("{} advice init error:{}", this.getClass(), th);
        }
        try {
            if (null != context) {
                context.doBefore(map);
            }
        } catch (Throwable th) {
            LOG.error("{} advice before error:{}", this.getClass(), th);
            throw th;
        }
        try {
            /* 执行代理方法 */
            result = getResult(t);
            try {
                if (null != context) {
                    context.doReturning(result);
                }
            } catch (Throwable th) {
                LOG.error("{} advice after error:{}", this.getClass(), th);
                throw th;
            }
        } catch (Throwable th) {
            try {
                if (null != context) {
                    context.doThrowing(th);
                }
            } catch (Throwable th1) {
                LOG.error("{} advice throwable error:{}", this.getClass(), th1);
                throw th1;
            }
            throw th;
        } finally {
            try {
                if (null != context) {
                    context.doFinally();
                }
            } catch (Throwable th) {
                LOG.error("{} advice finally error:{}", th);
                throw th;
            }
        }
        LOG.debug("exit {} advice", this.getClass());
        return result;
    }

    protected abstract JoinPointBean getJoinPointBean(T t);

    protected abstract Map<String, Object> getParameters(T t);

    protected abstract Object getResult(T t) throws Throwable;
}
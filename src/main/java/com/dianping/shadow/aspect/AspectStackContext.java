package com.dianping.shadow.aspect;

import com.dianping.shadow.advice.AspectParam;
import com.dianping.shadow.annotation.AdviceType;
import com.dianping.shadow.common.exception.UnsupportedModifyAspectStackException;
import com.dianping.shadow.context.AdviceBean;
import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.util.structure.FunctionLinkedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Map;

/**
 * Around栈上下文
 *
 * @author jourrey
 */
public class AspectStackContext {
    private static final Logger LOG = LogManager.getLogger(AspectStackContext.class);

    /* 织入操作 */
    private FunctionLinkedList<AspectStackFrame> advice;
    /* 前置操作压栈 */
    private FunctionLinkedList<AspectStackFrame> stack;
    /* 初始切面层级，用来标识调用入口 */
    private Integer initHierarchy;
    /* 当前切面层级，用来标识调用层级 */
    private Integer currentHierarchy;
    /* 连接点 */
    private JoinPointBean joinPointBean;
    /* 连接点拦截的参数 */
    private Map<String, Object> parameters;
    /* 连接点拦截的结果 */
    private Object result;
    /* 连接点拦截的异常 */
    private Throwable throwable;

    private AspectStackContext(JoinPointBean joinPointBean) {
        this.advice = new FunctionLinkedList<AspectStackFrame>();
        this.stack = new FunctionLinkedList<AspectStackFrame>();
        this.currentHierarchy = AspectContext.getInstance().getAspectHierarchyAndIncrement();
        this.joinPointBean = joinPointBean;
    }

    public Integer getInitHierarchy() {
        return initHierarchy;
    }

    public Integer getCurrentHierarchy() {
        return currentHierarchy;
    }

    /**
     * 是否初始切面
     *
     * @return
     */
    public boolean isInitAspect() {
        return initHierarchy == null || initHierarchy == currentHierarchy;
    }

    /**
     * 设置切面上下文
     */
    private void flush() {
        if (AspectContext.getInstance().getInitHierarchy() == null) {
            this.initHierarchy = currentHierarchy;
        } else {
            this.initHierarchy = AspectContext.getInstance().getInitHierarchy();
        }
        try {
            AspectContext.getInstance().setAspectStack(this);
        } catch (UnsupportedModifyAspectStackException e) {
            LOG.error("{} does not have permission", getClass(), e);
        }
    }

    /**
     * 构建切面处理器
     *
     * @param joinPointBean
     * @return
     */
    public static AspectStackContext builder(JoinPointBean joinPointBean) {
        LOG.debug("joinPointBean:{}", joinPointBean);
        AspectStackContext handler = new AspectStackContext(joinPointBean);
        if (joinPointBean == null) {
            return handler;
        }
        Collection<AdviceBean> adviceBeans = AspectContext.getInstance().getAdvice(joinPointBean);
        if (adviceBeans == null) {
            return handler;
        }
        for (AdviceBean adviceBean : adviceBeans) {
            // 尾部插入,保证入栈顺序与容器顺序一致
            handler.advice.addLast(new AspectStackFrame(handler, adviceBean));
        }
        return handler;
    }

    /**
     * 构建切面参数
     *
     * @param adviceBean
     * @return
     */
    private AspectParam buildAspectParam(AdviceBean adviceBean) {
        LOG.debug("adviceBean:{}", adviceBean);
        AspectParam aspectParam = AspectParam.create(adviceBean.getPointcutBean(), joinPointBean);
        aspectParam.setParameters(parameters);
        aspectParam.setResult(result);
        aspectParam.setThrowable(throwable);
        aspectParam.setExtendInfo(adviceBean.getExtendInfo());
        return aspectParam;
    }

    /**
     * 前置织入
     *
     * @param parameters
     */
    public void doBefore(Map<String, Object> parameters) {
        if (null == advice) {
            return;
        }
        flush();
        this.parameters = parameters;
        advice.traversalFromHead(AdviceFunction.BEFORE.function);
    }

    /**
     * 后置织入
     *
     * @param result
     */
    public void doReturning(Object result) {
        if (null == stack) {
            return;
        }
        flush();
        this.result = result;
        stack.traversalFromHead(AdviceFunction.AFTER_RETURNING.function);
    }

    /**
     * 异常织入
     *
     * @param throwable
     */
    public void doThrowing(Throwable throwable) {
        if (null == stack) {
            return;
        }
        flush();
        this.throwable = throwable;
        stack.traversalFromHead(AdviceFunction.AFTER_THROWING.function);
    }

    /**
     * 完成织入
     */
    public void doFinally() {
        if (null == stack) {
            return;
        }
        flush();
        stack.traversalFromHead(AdviceFunction.FINALLY.function);
        // 回到栈顶,清空上下文,必须在出栈之后
        AspectContext.getInstance().tryClean();
    }

    public enum AdviceFunction {
        /**
         * Before adviceType
         */
        BEFORE(new FunctionLinkedList.Function<AspectStackFrame>() {
            @Override
            public void call(AspectStackFrame aspectStackFrame) {
                // 后置操作压栈
                aspectStackFrame.getAspectStackContext().stack.addFirst(aspectStackFrame);

                if (aspectStackFrame.getAdviceBean().hasAdvice(AdviceType.AROUND)
                        || aspectStackFrame.getAdviceBean().hasAdvice(AdviceType.BEFORE)) {
                    aspectStackFrame.getAdviceBean().getAdvice().before(
                            aspectStackFrame.getAspectStackContext().buildAspectParam(aspectStackFrame.getAdviceBean()));
                }
            }
        }),

        /**
         * After returning adviceType
         */
        AFTER_RETURNING(new FunctionLinkedList.Function<AspectStackFrame>() {
            @Override
            public void call(AspectStackFrame aspectStackFrame) {
                if (aspectStackFrame.getAdviceBean().hasAdvice(AdviceType.AROUND)
                        || aspectStackFrame.getAdviceBean().hasAdvice(AdviceType.AFTER)
                        || aspectStackFrame.getAdviceBean().hasAdvice(AdviceType.AFTER_RETURNING)) {
                    aspectStackFrame.getAdviceBean().getAdvice().afterReturning(
                            aspectStackFrame.getAspectStackContext().buildAspectParam(aspectStackFrame.getAdviceBean()));
                }
            }
        }),

        /**
         * After throwing adviceType
         */
        AFTER_THROWING(new FunctionLinkedList.Function<AspectStackFrame>() {
            @Override
            public void call(AspectStackFrame aspectStackFrame) {
                if (aspectStackFrame.getAdviceBean().hasAdvice(AdviceType.AROUND)
                        || aspectStackFrame.getAdviceBean().hasAdvice(AdviceType.AFTER)
                        || aspectStackFrame.getAdviceBean().hasAdvice(AdviceType.AFTER_THROWING)) {
                    aspectStackFrame.getAdviceBean().getAdvice().afterThrowing(
                            aspectStackFrame.getAspectStackContext().buildAspectParam(aspectStackFrame.getAdviceBean()));
                }
            }
        }),

        /**
         * After (finally) adviceType
         */
        FINALLY(new FunctionLinkedList.Function<AspectStackFrame>() {
            @Override
            public void call(AspectStackFrame aspectStackFrame) {
                aspectStackFrame.getAdviceBean().getAdvice().afterFinally(
                        aspectStackFrame.getAspectStackContext().buildAspectParam(aspectStackFrame.getAdviceBean()));
                // 后置操作出栈
                aspectStackFrame.getAspectStackContext().stack.remove(aspectStackFrame);
            }
        });

        private final FunctionLinkedList.Function<AspectStackFrame> function;

        AdviceFunction(FunctionLinkedList.Function<AspectStackFrame> function) {
            this.function = function;
        }
    }
}
package com.dianping.shadow.context;

import com.dianping.shadow.advice.AdviceBean;
import com.dianping.shadow.aspect.AspectStackContext;
import com.dianping.shadow.common.exception.UnsupportedModifyAspectStackException;
import com.dianping.shadow.declaration.AspectDefinition;
import com.dianping.shadow.util.ClassUtils;
import com.dianping.shadow.util.ReflectionUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Aspect上下文
 *
 * @author jourrey
 */
public class AspectContext {
    private static final Logger LOG = LoggerFactory.getLogger(AspectContext.class);

    private static volatile AspectContext instance = null;

    private final List<AspectDefinition> aspectResolverCache;
    private final Multimap<JoinPointBean, AdviceBean> joinPointResolverCache;
    /* 切面层级/序列, 初始值 */
    private static final Integer AUTO_INCREMENT = 0;
    /* 切面层级/序列, 步长 */
    private static final Integer AUTO_INCREMENT_INCREMENT = 1;
    /* 切面分组, 用来标识一组切面, 父子线程共享 */
    private static final ThreadLocal<String> token = new InheritableThreadLocal<String>() {
        public String initialValue() {
            return UUID.randomUUID().toString();
        }
    };
    /* 切面序列, 用来标识调用顺序, 父子线程共享 */
    private static final ThreadLocal<Integer> aspectSequence = new InheritableThreadLocal<Integer>() {
        public Integer initialValue() {
            return AUTO_INCREMENT;
        }
    };
    /**
     * 切面层级, 用来标识调用层级
     * 不可用InheritableThreadLocal, tryClean()根据此判断是否清空上下文
     */
    private static final ThreadLocal<Integer> aspectHierarchy = new ThreadLocal<Integer>() {
        public Integer initialValue() {
            return AUTO_INCREMENT;
        }
    };
    /**
     * 切面栈, 当前程序执行的上下文
     * 不可用InheritableThreadLocal, tryClean()根据此判断是否清空上下文
     */
    private static final ThreadLocal<AspectStackContext> aspectStack = new ThreadLocal<AspectStackContext>() {
        public AspectStackContext initialValue() {
            return AspectStackContext.builder(null);
        }
    };

    /**
     * 获取当前切面分组令牌
     *
     * @return
     */
    public String getToken() {
        return token.get();
    }

    /**
     * 获取当前切面调用序列,并自增
     *
     * @return
     */
    public Integer getAspectSequenceAndIncrement() {
        return getAndIncrement(aspectSequence);
    }

    /**
     * 获取当前切面层级,并自增
     *
     * @return
     */
    public Integer getAspectHierarchyAndIncrement() {
        return getAndIncrement(aspectHierarchy);
    }

    /**
     * 获取当前值,并自增
     *
     * @param threadLocal
     * @return
     */
    private Integer getAndIncrement(ThreadLocal<Integer> threadLocal) {
        Integer current = threadLocal.get();
        threadLocal.set(current + AUTO_INCREMENT_INCREMENT);
        return current;
    }

    /**
     * 设置当前切面
     * 对调用对象进行限制,不是一个好的Case,但是随意设置将造成无可估量的后果
     * 严禁任何方式修改,这里抛出检查异常,希望能引起你的注意
     *
     * @param context
     */
    public void setAspectStack(AspectStackContext context) throws UnsupportedModifyAspectStackException {
        //不让使用Reflection.getCallerClass()
        Class callerClass;
        try {
            callerClass = ClassUtils.loadClass(ReflectionUtils.getStackTraceElement(1).getClassName());
        } catch (ClassNotFoundException e) {
            LOG.error("setAspectStack Exception", e);
            throw new UnsupportedModifyAspectStackException();
        }
        if (AspectStackContext.class.equals(callerClass)) {
            aspectStack.set(context);
        } else {
            throw new UnsupportedModifyAspectStackException();
        }
    }

    /**
     * 获取初始切面层级
     *
     * @return
     */
    public Integer getInitHierarchy() {
        return aspectStack.get().getInitHierarchy();
    }

    /**
     * 获取当前切面层级
     *
     * @return
     */
    public Integer getCurrentHierarchy() {
        return aspectStack.get().getCurrentHierarchy();
    }

    /**
     * 清除切面上下文
     */
    public void tryClean() {
        if (aspectStack.get().isInitAspect()) {
            token.remove();
            aspectHierarchy.remove();
            aspectSequence.remove();
            aspectStack.remove();
        }
    }

    /**
     * 设置切面
     *
     * @param aspectDefinition
     */
    public void addAspect(AspectDefinition aspectDefinition) {
        LOG.debug("aspectDefinition:{}", aspectDefinition);
        checkNotNull(aspectDefinition, "aspectDefinition can not null");
        if (aspectResolverCache.contains(aspectDefinition)) {
            return;
        }
        aspectResolverCache.add(aspectDefinition);
        Collections.sort(aspectResolverCache);
    }

    /**
     * 获取切面
     *
     * @return
     */
    public Collection<AspectDefinition> getAspect() {
        return aspectResolverCache;
    }

    /**
     * 添加连接点
     *
     * @param joinPointBean
     * @param adviceBean
     */
    public void addJoinPoint(JoinPointBean joinPointBean, AdviceBean adviceBean) {
        LOG.debug("joinPointBean:{} adviceBean:{}", joinPointBean, adviceBean);
        checkNotNull(joinPointBean, "joinPointBean can not null");
        checkNotNull(adviceBean, "adviceBean can not null");
        joinPointResolverCache.put(joinPointBean, adviceBean);
    }

    /**
     * 获取连接点
     *
     * @return
     */
    public Collection<JoinPointBean> getJoinPoint() {
        return ImmutableSet.copyOf(joinPointResolverCache.keySet());
    }

    /**
     * 获取通知
     *
     * @param joinPointBean
     * @return
     */
    public Collection<AdviceBean> getAdvice(JoinPointBean joinPointBean) {
        return ImmutableSet.copyOf(joinPointResolverCache.get(joinPointBean));
    }

    private AspectContext() {
        // 因为需要排序.所以不使用LinkedHashSet
        aspectResolverCache = Lists.newLinkedList();
        joinPointResolverCache = LinkedHashMultimap.create();
    }

    public static AspectContext getInstance() {
        if (instance == null) {
            synchronized (AspectContext.class) {
                if (instance == null) {
                    instance = new AspectContext();
                }
            }
        }
        return instance;
    }

    /**
     * 如果该对象被用于序列化，可以保证对象在序列化前后保持一致
     *
     * @returnType: Object
     */
    public Object readResolve() {
        return getInstance();
    }
}

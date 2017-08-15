package com.dianping.shadow.advice;

import com.dianping.shadow.common.exception.InstantiationInterceptException;
import com.dianping.shadow.context.PointcutBean;
import com.dianping.shadow.declaration.AspectDefinition;
import com.dianping.shadow.declaration.statement.AdviceType;
import com.dianping.shadow.util.ClassUtils;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 切面的定义类
 *
 * @author jourrey
 */
public class AdviceBeanFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AdviceBeanFactory.class);
    private static final ConcurrentHashMap<String, AdviceIntercept> adviceCache = new ConcurrentHashMap<String, AdviceIntercept>();

    /**
     * 根据切面定义生成切面Bean
     *
     * @param aspectDefinition
     * @param pointcutBean
     * @returnType: ActionLog
     */
    public static AdviceBean create(AspectDefinition aspectDefinition, PointcutBean pointcutBean) {
        LOG.debug("aspectDefinition:{} pointcutBean:{}", aspectDefinition, pointcutBean);
        checkNotNull(aspectDefinition, "aspectDefinition can not null");
        checkNotNull(pointcutBean, "pointcutBean can not null");
        checkNotNull(pointcutBean.getAnnotation(), "pointcutBean.annotation can not null");
        AdviceIntercept intercept = getAspectIntercept(aspectDefinition, pointcutBean);
        if (intercept == null) {
            return null;
        }
        AdviceBean adviceBean = AdviceBean.AdviceBeanBuilder.create(pointcutBean, intercept
                , initAdviceTypes(aspectDefinition.getAdviceTypes())
                , aspectDefinition.getOrder(), aspectDefinition.getExtendInfo());
        return adviceBean;
    }

    /**
     * 初始化AdviceType,如果没有默认AROUND
     *
     * @param adviceTypes
     * @return
     */
    private static Set<AdviceType> initAdviceTypes(Set<AdviceType> adviceTypes) {
        if (CollectionUtils.isEmpty(adviceTypes)) {
            adviceTypes = Sets.newHashSetWithExpectedSize(1);
            adviceTypes.add(AdviceType.AROUND);
        }
        return adviceTypes;
    }

    /**
     * 根据切面定义和注解获取切面的拦截器
     *
     * @param aspectDefinition
     * @param pointcutBean
     * @return
     */
    private static AdviceIntercept getAspectIntercept(AspectDefinition aspectDefinition, PointcutBean pointcutBean) {
        AdviceIntercept adviceIntercept;
        try {
            String builderAdviceKey = aspectDefinition.getAdvice().getName();
            if (adviceCache.containsKey(builderAdviceKey)) {
                return adviceCache.get(builderAdviceKey);
            }

            adviceIntercept = ClassUtils.newInstanceOf(aspectDefinition.getAdvice());
            // 对于AdviceInterceptBuilder的处理
            if (AdviceInterceptBuilder.class.isAssignableFrom(aspectDefinition.getAdvice())) {
                builderAdviceKey = ((AdviceInterceptBuilder) adviceIntercept).initAndReturnUniqueKey(aspectDefinition
                        , pointcutBean);
                if (adviceCache.containsKey(builderAdviceKey)) {
                    return adviceCache.get(builderAdviceKey);
                }
            }
            
            AdviceIntercept cache = adviceCache.putIfAbsent(builderAdviceKey, adviceIntercept);
            if (cache != null) {
                adviceIntercept = cache;
            }
        } catch (Exception e) {
            throw new InstantiationInterceptException(aspectDefinition.getAdvice(), e);
        }
        return adviceIntercept;
    }

}

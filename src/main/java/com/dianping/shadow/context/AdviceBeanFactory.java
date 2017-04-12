package com.dianping.shadow.context;

import com.dianping.shadow.advice.AdviceIntercept;
import com.dianping.shadow.advice.AdviceInterceptBuilder;
import com.dianping.shadow.annotation.AdviceType;
import com.dianping.shadow.common.exception.InstantiationInterceptException;
import com.dianping.shadow.util.ClassUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 切面的定义类
 *
 * @author jourrey
 */
public class AdviceBeanFactory {
    private static final Logger LOG = LogManager.getLogger(AdviceBeanFactory.class);
    private static final Map<String, AdviceIntercept> adviceCache = Maps.newHashMap();

    /**
     * 根据切面定义生成切面Bean
     *
     * @param pointcutBean
     * @param aspectDefinition
     * @returnType: ActionLog
     */
    public static AdviceBean create(PointcutBean pointcutBean, AspectDefinition aspectDefinition) {
        LOG.debug("pointcutBean:{} aspectDefinition:{}", pointcutBean, aspectDefinition);
        checkNotNull(pointcutBean, "pointcutBean can not null");
        checkNotNull(pointcutBean.getAnnotation(), "pointcutBean.annotation can not null");
        checkNotNull(aspectDefinition, "aspectDefinition can not null");
        AdviceIntercept intercept = getAspectIntercept(aspectDefinition, pointcutBean.getAnnotation());
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
     * @param annotation
     * @return
     */
    private static AdviceIntercept getAspectIntercept(AspectDefinition aspectDefinition, Annotation annotation) {
        AdviceIntercept adviceIntercept;
        try {
            String builderAdviceKey;
            if (AdviceInterceptBuilder.class.isAssignableFrom(aspectDefinition.getAdvice())) {
                adviceIntercept = ClassUtils.newInstanceOf(aspectDefinition.getAdvice());
                builderAdviceKey = ((AdviceInterceptBuilder) adviceIntercept).initAndReturnUniqueKey(aspectDefinition
                        , annotation);
                if (!adviceCache.containsKey(builderAdviceKey)) {
                    adviceCache.put(builderAdviceKey, adviceIntercept);
                }
            } else {
                builderAdviceKey = aspectDefinition.getAdvice().getName();
                if (!adviceCache.containsKey(builderAdviceKey)) {
                    adviceIntercept = ClassUtils.newInstanceOf(aspectDefinition.getAdvice());
                    adviceCache.put(builderAdviceKey, adviceIntercept);
                }
            }
            adviceIntercept = adviceCache.get(builderAdviceKey);
        } catch (Exception e) {
            throw new InstantiationInterceptException(aspectDefinition.getAdvice(), e);
        }
        return adviceIntercept;
    }

}

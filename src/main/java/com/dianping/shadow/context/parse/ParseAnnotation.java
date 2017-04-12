package com.dianping.shadow.context.parse;

import com.dianping.shadow.context.AdviceBeanFactory;
import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.context.AspectDefinition;
import com.dianping.shadow.context.JoinPointBeanFactory;
import com.dianping.shadow.context.PointcutBeanFactory;
import com.dianping.shadow.context.scanner.AbstractScanner;
import com.dianping.shadow.context.scanner.MemberScanner;
import com.dianping.shadow.context.strategy.AspectAnnotationStrategy;
import com.dianping.shadow.context.strategy.AspectAnnotationStrategyLoader;
import com.dianping.shadow.util.AnnotationUtils;
import com.google.common.base.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 解析注解
 * 根据配置的切面,解析类中方法对应的监听事件
 */
public class ParseAnnotation {
    private static final Logger LOG = LogManager.getLogger(ParseAnnotation.class);

    /**
     * 根据配置的切面,解析类中方法对应的监听事件
     *
     * @param clazz
     */
    public static Set<Member> doParse(Class<?> clazz) {
        Set<Member> members =
                new MemberScanner.MemberScannerBuilder()
                        .clazz(clazz)
                        .filter(new MemberParseFilter())
                        .build()
                        .doScan();
        return members;
    }

    /**
     * 注册方法的监听事件
     */
    private static class MemberParseFilter implements AbstractScanner.ScannerFilter<Member> {
        /**
         * {@link AspectDefinition#compareTo(AspectDefinition)}
         */
        @Override
        public boolean accept(Member member) {
            Collection<AspectDefinition> aspectResolver = AspectContext.getInstance().getAspect();
            for (Iterator<AspectDefinition> iterator = aspectResolver.iterator(); iterator.hasNext(); ) {
                AspectDefinition aspectDefinition = iterator.next();
                if (!aspectDefinition.isValue()) {
                    break; //直接break是因为按照isValue正排序
                }
                if (!(member instanceof Method)) {
                    LOG.debug("Skip {}, current only support method", member.getName());
                    continue;
                }
                if (Modifier.isAbstract(member.getModifiers())) {
                    LOG.debug("Skip {}, current method is abstract", member.getName());
                    continue;
                }
                // 获取注解对象
                Annotation annotation = AnnotationUtils.parseMethod((Method) member, aspectDefinition.getAnnotation());
                if (annotation == null) {
                    List<AspectAnnotationStrategy> strategies = AspectAnnotationStrategyLoader.getStrategies();
                    Optional<? extends Class<? extends Annotation>> classOptional;
                    for (AspectAnnotationStrategy strategy : strategies) {
                        try {
                            classOptional = strategy.choose(aspectDefinition.getAnnotation());
                            if (classOptional.isPresent()) {
                                annotation = AnnotationUtils.parseMethod((Method) member, classOptional.get());
                                if (annotation != null) {
                                    break;
                                }
                            }
                        } catch (Throwable th) {
                            LOG.error("strategy {} exception", strategy.getClass().getName(), th);
                            break;
                        }
                    }
                }
                // 存在注解,则加入切面
                if (annotation != null) {
                    LOG.debug("add JoinPoint {}", member.getName());
                    AspectContext.getInstance().addJoinPoint(JoinPointBeanFactory.create(member)
                            , AdviceBeanFactory.create(PointcutBeanFactory.create(annotation), aspectDefinition));
                }
            }
            // 无需缓存, 不需要收录Method
            return false;
        }
    }

}
package com.dianping.shadow.context.parse;

import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.context.scanner.AbstractScanner;
import com.dianping.shadow.context.scanner.ClassScanner;
import com.dianping.shadow.declaration.AspectDefinition;
import com.dianping.shadow.declaration.statement.AspectConfig;
import com.dianping.shadow.util.AnnotationUtils;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 解析出{@link AspectConfig}注解配置
 */
public class ParseAspectConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ParseAspectConfig.class);

    /**
     * 根据包名获取包内所有类路径
     *
     * @param packageName
     * @return
     */
    public static Set<Class<?>> doParse(String packageName) {
        Set<Class<?>> classes =
                new ClassScanner.ClassScannerBuilder()
                        .basePackage(packageName)
                        .recursive(true)
                        .filter(new AspectConfigParseFilter())
                        .build()
                        .doScan();
        return classes;
    }

    /**
     * 将{@link AspectConfig}配置,注入上下文
     */
    private static AspectDefinition aspectConfig2AspectDefinition(AspectConfig aspectConfig) {
        checkAspectConfig(aspectConfig);
        return AspectDefinition.AspectDefinitionBuilder.create(aspectConfig.annotation()
                , aspectConfig.advice(), Sets.newHashSet(aspectConfig.adviceType())
                , aspectConfig.order(), aspectConfig.extendInfo(), aspectConfig.value());
    }

    /**
     * 将{@link AspectConfig}配置,校验
     */
    private static void checkAspectConfig(AspectConfig aspectConfig) {
        checkNotNull(aspectConfig, "aspectConfig can not null");
        checkNotNull(aspectConfig.advice(), "aspectConfig.advice can not null");
        checkNotNull(aspectConfig.adviceType(), "aspectConfig.adviceType can not null");
        checkNotNull(aspectConfig.annotation(), "aspectConfig.annotation can not null");
    }

    /**
     * 将{@link AspectConfig}配置,注入上下文
     */
    private static class AspectConfigParseFilter implements AbstractScanner.ScannerFilter<Class<?>> {
        @Override
        public boolean accept(Class<?> classRef) {
            Set<AspectConfig> aspectConfigs = findAspectConfig(classRef);
            AspectConfig aspectConfig;
            for (Iterator<AspectConfig> iterator = aspectConfigs.iterator(); iterator.hasNext(); ) {
                aspectConfig = iterator.next();
                //生效配置才载入
                if (aspectConfig != null && aspectConfig.value()) {
                    LOG.debug("add Aspect {}", aspectConfig);
                    AspectContext.getInstance().addAspect(aspectConfig2AspectDefinition(aspectConfig));
                }
            }
            // 缓存, 收录所有class, 后续遍历
            return true;
        }
    }

    /**
     * 解析类上的注解
     *
     * @param clazz
     * @return
     */
    private static Set<AspectConfig> findAspectConfig(Class<?> clazz) {
        checkNotNull(clazz, "clazz can not null");
        Set<AspectConfig> aSet = Sets.newHashSet();
        AspectConfig config = AnnotationUtils.parseClass(clazz, AspectConfig.class);
        if (config != null) {
            aSet.add(config);
        }
        if (ArrayUtils.isEmpty(clazz.getDeclaredMethods())) {
            return aSet;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            config = AnnotationUtils.parseMethod(method, AspectConfig.class);
            if (config != null) {
                aSet.add(config);
            }
        }
        return aSet;
    }

}
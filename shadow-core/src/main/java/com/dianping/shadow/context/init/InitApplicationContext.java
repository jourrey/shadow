package com.dianping.shadow.context.init;

import com.dianping.shadow.common.exception.IllegalPackageNameException;
import com.dianping.shadow.context.parse.ParseAspectConfig;
import com.dianping.shadow.context.parse.ParsePointcutAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Set;

/**
 * 容器初始化
 * Created by jourrey on 16/11/10.
 */
public class InitApplicationContext {
    private static final Logger LOG = LoggerFactory.getLogger(ContextLoaderListener.class);

    public static void scan(String packageName) {
        // 跳过自身Class,扫描优化
        if (InitApplicationContext.class.getCanonicalName().startsWith(packageName)) {
            LOG.warn("packageName can not contain {}", InitApplicationContext.class);
            throw new IllegalPackageNameException(MessageFormat.format("packageName can not contain {0}", InitApplicationContext.class));
        }
        Set<Class<?>> classes = ParseAspectConfig.doParse(packageName);
        if (null == classes) {
            return;
        }
        for (Class<?> aClass : classes) {
            ParsePointcutAnnotation.doParse(aClass);
        }
        return;
    }
}

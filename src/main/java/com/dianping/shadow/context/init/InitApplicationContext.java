package com.dianping.shadow.context.init;

import com.dianping.shadow.context.parse.ParseAnnotation;
import com.dianping.shadow.context.parse.ParseAspectConfig;

import java.util.Set;

/**
 * 容器初始化
 * Created by jourrey on 16/11/10.
 */
public class InitApplicationContext {

    public static void scan(String packageName) {
        Set<Class<?>> classes = ParseAspectConfig.doParse(packageName);
        if (null == classes) {
            return;
        }
        for (Class<?> aClass : classes) {
            ParseAnnotation.doParse(aClass);
        }
        return;
    }
}

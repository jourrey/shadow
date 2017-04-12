package com.dianping.shadow.context.strategy;

import com.dianping.shadow.util.ExtensionLoader;

import java.util.List;

/**
 * Created by jourrey on 17/2/6.
 */
public class AspectAnnotationStrategyLoader {

    private static List<AspectAnnotationStrategy> strategies = ExtensionLoader.getList(AspectAnnotationStrategy.class);

    public static boolean registerStrategy(AspectAnnotationStrategy strategy) {
        if (strategies.contains(strategy)) {
            return false;
        }
        return strategies.add(strategy);
    }

    public static boolean unregisterStrategy(AspectAnnotationStrategy strategy) {
        return strategies.remove(strategy);
    }

    public static List<AspectAnnotationStrategy> getStrategies() {
        return strategies;
    }
}

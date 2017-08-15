package com.dianping.shadow.advice;

import com.dianping.shadow.context.PointcutBean;
import com.dianping.shadow.declaration.AspectDefinition;

/**
 * 切面通知,根据上下文,初始化实例
 * Created by jourrey on 16/9/24.
 */
public interface AdviceInterceptBuilder extends AdviceIntercept {

    /**
     * 初始化通知对象,并返回一个可标识的唯一值
     *
     * @param aspectDefinition
     * @param pointcutBean
     * @return
     */
    String initAndReturnUniqueKey(AspectDefinition aspectDefinition, PointcutBean pointcutBean);

}

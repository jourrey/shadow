package com.dianping.shadow.aspect;

import com.dianping.shadow.advice.AdviceBean;

/**
 * 切面栈帧
 *
 * @author jourrey
 */
public class AspectStackFrame {

    private AspectStackContext aspectStackContext;
    private AdviceBean adviceBean;

    public AspectStackFrame(AspectStackContext aspectStackContext, AdviceBean adviceBean) {
        this.aspectStackContext = aspectStackContext;
        this.adviceBean = adviceBean;
    }

    public AspectStackContext getAspectStackContext() {
        return aspectStackContext;
    }

    public AdviceBean getAdviceBean() {
        return adviceBean;
    }

}
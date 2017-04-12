package com.dianping.shadow.common.exception;

import java.text.MessageFormat;

/**
 * Created by jourrey on 16/10/25.
 */
public class InstantiationInterceptException extends RuntimeException {
    private static final long serialVersionUID = 9096703785405848834L;

    private Class interceptClass;

    public InstantiationInterceptException(Class interceptClass, Throwable cause) {
        super(MessageFormat.format("Could not instantiate {0}", interceptClass.getName()), cause);
        this.interceptClass = interceptClass;
    }

    public Class getInterceptClass() {
        return this.interceptClass;
    }
}

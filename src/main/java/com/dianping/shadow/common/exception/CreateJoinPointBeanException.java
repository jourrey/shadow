package com.dianping.shadow.common.exception;

/**
 * Created by jourrey on 16/10/25.
 */
public class CreateJoinPointBeanException extends RuntimeException {
    private static final long serialVersionUID = 9096703785405848834L;

    public CreateJoinPointBeanException(String message) {
        super(message);
    }

    public CreateJoinPointBeanException(String message, Throwable cause) {
        super(message, cause);
    }

}

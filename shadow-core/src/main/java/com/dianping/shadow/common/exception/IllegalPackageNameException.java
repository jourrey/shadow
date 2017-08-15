package com.dianping.shadow.common.exception;

/**
 * Created by jourrey on 16/10/25.
 */
public class IllegalPackageNameException extends RuntimeException {
    private static final long serialVersionUID = 9096703785405848834L;

    public IllegalPackageNameException(String message) {
        super(message);
    }

    public IllegalPackageNameException(String message, Throwable cause) {
        super(message, cause);
    }

}

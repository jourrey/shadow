package com.dianping.shadow.declaration.statement;

/**
 * 织入类型
 *
 * @author jourrey
 */
public enum AdviceType {

    /**
     * Before adviceType
     */
    BEFORE,

    /**
     * After returning adviceType
     */
    AFTER_RETURNING,

    /**
     * After throwing adviceType
     */
    AFTER_THROWING,

    /**
     * After adviceType (After returning & After throwing)
     */
    AFTER,

    /**
     * Around adviceType
     */
    AROUND
}

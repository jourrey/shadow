package com.dianping.shadow.aspect.instrument;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by jourrey on 17/5/16.
 */
public class AgentInvocation {
    public static final String PARAM_NAME_SEPARATOR = ",";

    private Object target;
    private Class targetClass;
    private String targetMethodName;
    private Class[] paramTypes;
    private Object[] params;
    private String[] paramNames;

    public void parseParamNames(String paramNameStr) {
        if (StringUtils.isBlank(paramNameStr)) {
            this.paramNames = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            this.paramNames = paramNameStr.split(PARAM_NAME_SEPARATOR);
        }
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }

    public void setTargetMethodName(String targetMethodName) {
        this.targetMethodName = targetMethodName;
    }

    public Class[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public void setParamNames(String[] paramNames) {
        this.paramNames = paramNames;
    }
}

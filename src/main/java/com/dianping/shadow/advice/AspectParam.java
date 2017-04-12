package com.dianping.shadow.advice;

import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.context.JoinPointBean;
import com.dianping.shadow.context.PointcutBean;

import java.io.Serializable;
import java.util.Map;

/**
 * 收集者参数
 *
 * @author jourrey
 */
public class AspectParam implements Serializable {
    /* 序列化唯一ID号 */
    private static final long serialVersionUID = 5155733192627422874L;

    /* 切面分组，用来标识一组切面 */
    private String token;

    /* 切面层级，用来标识调用层级 */
    private Integer hierarchy;

    /* 切面序列，用来切面顺序 */
    private Integer sequence;

    /* 切面的切点 */
    private PointcutBean pointcutBean;

    /* 切面的连接点 */
    private JoinPointBean joinPointBean;

    /* 切面的连接点参数 */
    private Map<String, Object> parameters;

    /* 切面的连接点结果 */
    private Object result;

    /* 切面的连接点异常 */
    private Throwable throwable;

    /* 切面扩展信息 */
    private String extendInfo;

    public static AspectParam create(PointcutBean pointcutBean, JoinPointBean joinPointBean) {
        AspectParam param = new AspectParam();
        param.pointcutBean = pointcutBean;
        param.joinPointBean = joinPointBean;
        return param;
    }

    private AspectParam() {
        this.token = AspectContext.getInstance().getToken();
        this.hierarchy = AspectContext.getInstance().getCurrentHierarchy();
        this.sequence = AspectContext.getInstance().getAspectSequenceAndIncrement();
    }

    public String getToken() {
        return token;
    }

    public Integer getHierarchy() {
        return hierarchy;
    }

    public Integer getSequence() {
        return sequence;
    }

    public PointcutBean getPointcutBean() {
        return pointcutBean;
    }

    public JoinPointBean getJoinPointBean() {
        return joinPointBean;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }
}

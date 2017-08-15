package com.dianping.shadow.advice;

import com.dianping.shadow.declaration.statement.AdviceType;
import com.dianping.shadow.context.PointcutBean;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 切面的定义类
 *
 * @author jourrey
 */
public class AdviceBean {

    private final PointcutBean pointcutBean;
    private final AdviceIntercept advice;
    private final Set<AdviceType> adviceTypes;
    private final int order;
    private final String extendInfo;

    private AdviceBean(PointcutBean pointcutBean, AdviceIntercept advice, Set<AdviceType> adviceTypes
            , int order, String extendInfo) {
        this.pointcutBean = pointcutBean;
        this.advice = advice;
        this.adviceTypes = adviceTypes;
        this.order = order;
        this.extendInfo = extendInfo;
    }

    public PointcutBean getPointcutBean() {
        return pointcutBean;
    }

    public AdviceIntercept getAdvice() {
        return advice;
    }

    public boolean hasAdvice(AdviceType adviceType) {
        return adviceTypes.contains(adviceType);
    }

    public int getOrder() {
        return order;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AdviceBean that = (AdviceBean) o;

        return new EqualsBuilder()
                .append(order, that.order)
                .append(pointcutBean, that.pointcutBean)
                .append(advice, that.advice)
                .append(adviceTypes, that.adviceTypes)
                .append(extendInfo, that.extendInfo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pointcutBean)
                .append(advice)
                .append(adviceTypes)
                .append(order)
                .append(extendInfo)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pointcutBean", pointcutBean)
                .append("advice", advice)
                .append("adviceTypes", adviceTypes)
                .append("order", order)
                .append("extendInfo", extendInfo)
                .toString();
    }

    public static class AdviceBeanBuilder {

        public static AdviceBean create(PointcutBean pointcutBean, AdviceIntercept advice, Set<AdviceType> adviceTypes
                , int order, String extendInfo) {
            checkNotNull(pointcutBean, "pointcutBean can not null");
            checkNotNull(advice, "advice can not null");
            adviceTypes = CollectionUtils.isEmpty(adviceTypes) ? Sets.newHashSet(AdviceType.AROUND) : adviceTypes;
            return new AdviceBean(pointcutBean, advice, adviceTypes, order, extendInfo);
        }

    }

}

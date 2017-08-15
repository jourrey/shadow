package com.dianping.shadow.declaration;

import com.dianping.shadow.advice.AdviceIntercept;
import com.dianping.shadow.context.parse.ParsePointcutAnnotation;
import com.dianping.shadow.declaration.statement.AdviceType;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 切面的定义类
 *
 * @author jourrey
 */
public class AspectDefinition implements Comparable<AspectDefinition> {
    private final Class<? extends Annotation> annotation;
    private final Class<? extends AdviceIntercept> advice;
    private final Set<AdviceType> adviceTypes;
    private final int order;
    private final String extendInfo;
    private final boolean value;

    private AspectDefinition(Class<? extends Annotation> annotation
            , Class<? extends AdviceIntercept> advice, Set<AdviceType> adviceTypes
            , int order, String extendInfo, boolean value) {
        this.annotation = annotation;
        this.advice = advice;
        this.adviceTypes = adviceTypes;
        this.order = order;
        this.extendInfo = extendInfo;
        this.value = value;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public Class<? extends AdviceIntercept> getAdvice() {
        return advice;
    }

    public Set<AdviceType> getAdviceTypes() {
        return adviceTypes;
    }

    public int getOrder() {
        return order;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AspectDefinition that = (AspectDefinition) o;

        return new EqualsBuilder()
                .append(annotation, that.annotation)
                .append(advice, that.advice)
                .append(adviceTypes, that.adviceTypes)
                .append(order, that.order)
                .append(extendInfo, that.extendInfo)
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(annotation)
                .append(advice)
                .append(adviceTypes)
                .append(order)
                .append(extendInfo)
                .append(value)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("annotation", annotation)
                .append("advice", advice)
                .append("adviceTypes", adviceTypes)
                .append("order", order)
                .append("extendInfo", extendInfo)
                .append("value", value)
                .toString();
    }

    /**
     * value必须第一位比较,遍历优化,第一个出现false,之后全抛弃
     * 除了value 和 order 其它参与排序的属性,只是为了保证上下文执行顺序尽量一致,先后不影响功能
     * {@link ParsePointcutAnnotation.MemberParseFilter#accept(Member)}
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(AspectDefinition o) {
        return ComparisonChain.start()
                .compareTrueFirst(this.isValue(), o.isValue())
                .compare(this.getOrder(), o.getOrder())
                .compare(this.getAnnotation().getName(), o.getAnnotation().getName())
                .compare(this.getAdvice().getName(), o.getAdvice().getName())
                .compare(this.getExtendInfo(), o.getExtendInfo())
                .compare(this.getAdviceTypes().size(), o.getAdviceTypes().size())
                .result();
    }

    public static class AspectDefinitionBuilder {

        public static AspectDefinition create(Class<? extends Annotation> annotation
                , Class<? extends AdviceIntercept> advice, Set<AdviceType> adviceTypes
                , int order, String extendInfo, boolean value) {
            checkNotNull(annotation, "annotation can not null");
            checkNotNull(advice, "advice can not null");
            adviceTypes = CollectionUtils.isEmpty(adviceTypes) ? Sets.newHashSet(AdviceType.AROUND) : adviceTypes;
            return new AspectDefinition(annotation, advice, adviceTypes, order, extendInfo, value);
        }

    }

}

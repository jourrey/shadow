package com.dianping.shadow.context;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.annotation.Annotation;

/**
 * 切点的定义类
 *
 * @author jourrey
 */
public class PointcutBean {

    private final Annotation annotation;

    public PointcutBean(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PointcutBean that = (PointcutBean) o;

        return new EqualsBuilder()
                .append(annotation, that.annotation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(annotation)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("annotation", annotation)
                .toString();
    }
}

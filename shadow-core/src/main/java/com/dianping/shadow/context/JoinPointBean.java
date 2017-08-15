package com.dianping.shadow.context;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Member;

/**
 * 连接点的定义类
 *
 * @author jourrey
 */
public class JoinPointBean {

    private final Member member;

    public JoinPointBean(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    public Class getDeclaringClass() {
        return member.getDeclaringClass();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        JoinPointBean that = (JoinPointBean) o;

        return new EqualsBuilder()
                .append(member, that.member)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(member)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("member", member)
                .toString();
    }

}

package com.dianping.shadow.context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 连接点的定义类
 *
 * @author jourrey
 */
public class JoinPointBeanFactory {
    private static final Logger LOG = LogManager.getLogger(JoinPointBeanFactory.class);

    /**
     * 创建连接点
     *
     * @param member
     * @returnType: JoinPointBean
     */
    public static JoinPointBean create(Member member) {
        LOG.debug("member:{}", member);
        checkNotNull(member, "member can not null");
        if (!(member instanceof Method)) {
            throw new IllegalArgumentException("Current only support method");
        }
        return new JoinPointBean(member);
    }

}

package com.dianping.shadow.context.strategy;

import com.alibaba.fastjson.JSON;
import com.dianping.shadow.util.EncryptUtils;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 切点的定义类
 *
 * @author jourrey
 */
public class AnnotationExpression {
    private final List<List<Class<? extends Annotation>>> candidate = Lists.newLinkedList();
    private final List<Class<? extends Annotation>> inauguration = Lists.newLinkedList();
    private Expression pointcut;

    /**
     * 创建一个表达式
     *
     * @return
     */
    public void setPointcut(List<List<Class<? extends Annotation>>> pointcut) {
        candidate.addAll(pointcut);
    }

    /**
     * 创建一个表达式
     *
     * @return
     */
    public Expression create() {
        Expression expression = new Expression(null);
        expression.parent = expression;
        this.pointcut = expression;
        return expression;
    }

    /**
     * 获取切点组合
     *
     * @return
     */
    public List<List<Class<? extends Annotation>>> getCandidateTuple() {
        if (CollectionUtils.isNotEmpty(candidate)) {
            return candidate;
        }
        Multimap<Connective, Class<? extends Annotation>> connectiveResolverCache = LinkedListMultimap.create();
        parsePointcutTuple(pointcut, pointcut, connectiveResolverCache);
        for (Iterator<Connective> iterator = connectiveResolverCache.keySet().iterator(); iterator.hasNext(); ) {
            Connective connective = iterator.next();
//            System.out.println(JSON.toJSONString(connectiveResolverCache.get(connective)));
            candidate.add((List<Class<? extends Annotation>>) connectiveResolverCache.get(connective));
        }
        return candidate;
    }

    public void print() {
        for (List<Class<? extends Annotation>> classes : getCandidateTuple()) {
//            System.out.println(JSON.toJSONString(classes));
        }
    }

    public static void main(String[] args) {
        AnnotationExpression bean = new AnnotationExpression();
        Expression expression = bean.create();
        //无复合表达式
//        expression.and(Documented.class).or(Target.class).and(Target.class).or(Retention.class);

//        expression.or(Documented.class).and(Target.class).or(Target.class).and(Retention.class);

//        expression.and(Documented.class).or(Target.class).and(Target.class).or(Retention.class)
//                .or(Documented.class).and(Target.class).or(Target.class).and(Retention.class);

//        expression.or(Documented.class).and(Target.class).or(Target.class).and(Retention.class)
//                .and(Documented.class).or(Target.class).and(Target.class).or(Retention.class);

        // 复合表达式
//        expression.and().and(Documented.class).or(Target.class).and(Target.class).or(Retention.class).lift()
//                .and().and(Override.class).or(SafeVarargs.class).and(SafeVarargs.class).or(SuppressWarnings.class);

//        expression.and().and(Documented.class).or(Target.class).and(Target.class).or(Retention.class).lift()
//                .or().and(Override.class).or(SafeVarargs.class).and(SafeVarargs.class).or(SuppressWarnings.class);

//        expression.or().and(Documented.class).or(Target.class).and(Target.class).or(Retention.class).lift()
//                .or().and(Override.class).or(SafeVarargs.class).and(SafeVarargs.class).or(SuppressWarnings.class);

        // 嵌套复合表达式
        expression.and().and(Documented.class).or(Target.class).and(Target.class).or(Retention.class)
                .and().and(Override.class).or(SafeVarargs.class).and(SafeVarargs.class).or(SuppressWarnings.class);

//        expression.and().and(Documented.class).or(Target.class).and(Target.class).or(Retention.class).lift()
//                .or().and(Override.class).or(SafeVarargs.class).and(SafeVarargs.class).or(SuppressWarnings.class);

//        expression.or().and(Documented.class).or(Target.class).and(Target.class).or(Retention.class).lift()
//                .or().and(Override.class).or(SafeVarargs.class).and(SafeVarargs.class).or(SuppressWarnings.class);

//        expression.and().and(Documented.class).or(Target.class).and(Target.class).or(Retention.class)
//        .and().and(Override.class).or(SafeVarargs.class).and(SafeVarargs.class).or(SuppressWarnings.class)
//        .and().and(Inherited.class).or(Transient.class).and(Transient.class).or(ConstructorProperties.class);
        bean.print();
    }

    private void parsePointcutTuple(Connective connective, Connective origin
            , Multimap<Connective, Class<? extends Annotation>> expressionListMultimap) {
//        System.out.println(JSON.toJSONString(connective));
//        System.out.println(JSON.toJSONString(origin));

        if (Type.EXPRESSION.equals(connective.type)) {
            List<Connective> connectiveList = ((Expression) connective).connectives;
            Multimap<Connective, Class<? extends Annotation>> currentConnectiveMap;
            for (ListIterator<Connective> iterator = connectiveList.listIterator(); iterator.hasNext(); ) {
                Connective children = iterator.next();
                if (Operator.OR.equals(children.operator)) {
                    origin = children;
                }
                currentConnectiveMap = LinkedListMultimap.create();
                parsePointcutTuple(children, origin, currentConnectiveMap);
                for (Iterator<Connective> currents = expressionListMultimap.keySet().iterator(); iterator.hasNext(); ) {
                    Connective current = currents.next();

                }
                for (Iterator<Connective> currents = currentConnectiveMap.keySet().iterator(); iterator.hasNext(); ) {
                    Connective current = currents.next();

                }
                if (Operator.AND.equals(children.operator) && Type.EXPRESSION.equals(children.type)) {

//                    expressionListMultimap.get(previous).addAll(expressionListMultimap.get(origin));
//                    expressionListMultimap.removeAll(origin);
                }
//                Connective previous = iterator.hasPrevious() ? iterator.previous() : children;
//                if (Operator.OR.equals(children.operator)) {
//                    expressionListMultimap.get(children).addAll(expressionListMultimap.get(origin));
//                    origin = children;
//                }
            }
        } else {
            expressionListMultimap.get(origin).add(((Proposition) connective).annotation);
//            if (Operator.AND.equals(connective.operator)) {
//                expressionListMultimap.get(origin).add(((Proposition) connective).statement);
//            }
//            if (Operator.OR.equals(connective.operator)) {
//                expressionListMultimap.get(connective).add(((Proposition) connective).statement);
//            }
        }
    }

    /**
     * 注解集合签名
     *
     * @param o
     * @return
     */
    private String signature(Object o) {
        return EncryptUtils.encodeMD5String(JSON.toJSONString(o));
    }

    /**
     * 切点表达式操作符
     */
    private enum Operator {
        AND,
        OR
    }

    /**
     * 切点表达式元素类型
     */
    private enum Type {
        EXPRESSION,
        PROPOSITION
    }

    /**
     * 切点表达式联结,暨逻辑联结词
     */
    private static class Connective {

        protected Operator operator;
        protected Type type;

        public Operator getOperator() {
            return operator;
        }

        public Type getType() {
            return type;
        }
    }

    /**
     * 逻辑表达式,暨逻辑复合命题  Compound
     */
    public static class Expression extends Connective {

        private Expression parent;
        private List<Connective> connectives;

        public Expression(Expression parent) {
            this.type = Type.EXPRESSION;
            this.parent = parent;
            this.connectives = Lists.newLinkedList();
        }

        public Expression getParent() {
            return parent;
        }

        public List<Connective> getConnectives() {
            return connectives;
        }

        /**
         * 添加一个复合命题,表达式下沉一级,与前一个是 and 逻辑运算,可以理解为"("操作符
         *
         * @return
         */
        public Expression and() {
            Expression children = new Expression(this);
            children.operator = Operator.AND;
            this.connectives.add(children);
            return children;
        }

        /**
         * 添加一个复合命题,表达式下沉一级,与前一个是 or 逻辑运算,可以理解为"("操作符
         *
         * @return
         */
        public Expression or() {
            Expression children = new Expression(this);
            children.operator = Operator.OR;
            this.connectives.add(children);
            return children;
        }

        /**
         * 结束一个复合命题,表达式提升一级,,可以理解为")"操作符
         * lift命名参考自RxJava源码
         *
         * @return
         */
        public Expression lift() {
            return this.parent;
        }

        /**
         * 添加一个简单命题,与前一个是 and 逻辑运算
         *
         * @return
         */
        public Expression and(Class<? extends Annotation> annotation) {
            Proposition proposition = new Proposition(annotation).and();
            this.connectives.add(proposition);
            return this;
        }

        /**
         * 添加一个简单命题,与前一个是 or 逻辑运算
         *
         * @return
         */
        public Expression or(Class<? extends Annotation> annotation) {
            Proposition proposition = new Proposition(annotation).or();
            this.connectives.add(proposition);
            return this;
        }

    }

    /**
     * 逻辑表达式元素,暨逻辑简单命题
     */
    private static class Proposition extends Connective {

        private Class<? extends Annotation> annotation;

        public Proposition(Class<? extends Annotation> annotation) {
            this.type = Type.PROPOSITION;
            this.annotation = annotation;
        }

        public Class<? extends Annotation> getAnnotation() {
            return annotation;
        }

        public Proposition and() {
            this.operator = Operator.AND;
            return this;
        }

        public Proposition or() {
            this.operator = Operator.OR;
            return this;
        }
    }

}

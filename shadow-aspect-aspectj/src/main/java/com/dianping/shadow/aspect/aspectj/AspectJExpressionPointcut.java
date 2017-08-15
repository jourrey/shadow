package com.dianping.shadow.aspect.aspectj;

import com.dianping.shadow.util.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.tools.ContextBasedMatcher;
import org.aspectj.weaver.tools.FuzzyBoolean;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spring里面的实现入口,就是为了看看,没啥用,阅读可忽略
 */
public class AspectJExpressionPointcut {
    private static final Logger LOG = LoggerFactory.getLogger(AspectJExpressionPointcut.class);

    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<PointcutPrimitive>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
    }

    private String expression;
    private transient PointcutExpression pointcutExpression;
    private transient Map<Method, ShadowMatch> shadowMatchCache = new ConcurrentHashMap<Method, ShadowMatch>(32);

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Build the underlying AspectJ pointcut expression.
     */
    private PointcutExpression buildPointcutExpression() {
        return buildPointcutExpression(ClassUtils.getDefaultClassLoader());
    }

    /**
     * Build the underlying AspectJ pointcut expression.
     */
    private PointcutExpression buildPointcutExpression(ClassLoader classLoader) {
        PointcutParser parser = initializePointcutParser(classLoader);
        PointcutParameter[] pointcutParameters = new PointcutParameter[0];
        return parser.parsePointcutExpression(
                replaceBooleanOperators(getExpression()),
                null, pointcutParameters);
    }

    /**
     * Initialize the underlying AspectJ pointcut parser.
     */
    private PointcutParser initializePointcutParser(ClassLoader cl) {
        PointcutParser parser = PointcutParser
                .getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                        SUPPORTED_PRIMITIVES, cl);
        parser.registerPointcutDesignatorHandler(new BeanNamePointcutDesignatorHandler());
        return parser;
    }

    /**
     * If a pointcut expression has been specified in XML, the user cannot
     * write {@code and} as "&&" (though &amp;&amp; will work).
     * We also allow {@code and} between two pointcut sub-expressions.
     * <p>This method converts back to {@code &&} for the AspectJ pointcut parser.
     */
    private String replaceBooleanOperators(String pcExpr) {
        String result = StringUtils.replace(pcExpr, " and ", " && ");
        result = StringUtils.replace(result, " or ", " || ");
        result = StringUtils.replace(result, " not ", " ! ");
        return result;
    }

    /**
     * Handler for the Spring-specific {@code bean()} pointcut designator
     * filter to AspectJ.
     * <p>This handler must be added to each pointcut object that needs to
     * handle the {@code bean()} PCD. Matching context is obtained
     * automatically by examining a thread local variable and therefore a matching
     * context need not be set on the pointcut.
     */
    private class BeanNamePointcutDesignatorHandler implements PointcutDesignatorHandler {

        private static final String BEAN_DESIGNATOR_NAME = "";

        public String getDesignatorName() {
            return BEAN_DESIGNATOR_NAME;
        }

        public ContextBasedMatcher parse(String expression) {
            return new BeanNameContextMatcher(expression);
        }
    }


    /**
     * Matcher class for the BeanNamePointcutDesignatorHandler.
     * <p>Dynamic match tests for this matcher always return true,
     * since the matching decision is made at the proxy creation time.
     * For static match tests, this matcher abstains to allow the overall
     * pointcut to match even when negation is used with the bean() pointcut.
     */
    private class BeanNameContextMatcher implements ContextBasedMatcher {

        private final NamePattern expressionPattern;

        public BeanNameContextMatcher(String expression) {
            this.expressionPattern = new NamePattern(expression);
        }

        public boolean couldMatchJoinPointsInType(Class someClass) {
            return (contextMatch(someClass) == FuzzyBoolean.YES);
        }

        public boolean couldMatchJoinPointsInType(Class someClass, MatchingContext context) {
            return (contextMatch(someClass) == FuzzyBoolean.YES);
        }

        public boolean matchesDynamically(MatchingContext context) {
            return true;
        }

        public FuzzyBoolean matchesStatically(MatchingContext context) {
            return contextMatch(null);
        }

        public boolean mayNeedDynamicTest() {
            return false;
        }

        private FuzzyBoolean contextMatch(Class targetType) {
            return targetType == null ? FuzzyBoolean.MAYBE : FuzzyBoolean.fromBoolean(matchesBeanName(targetType.getName()));
        }

        private boolean matchesBeanName(String advisedBeanName) {
            return this.expressionPattern.matches(advisedBeanName);
        }
    }
}

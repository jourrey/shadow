package com.dianping.shadow.util;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * General utility methods for working with annotations, handling bridge methods
 * (which the compiler generates for generic declarations) as well as super methods
 * (for optional &quot;annotation inheritance&quot;). Note that none of this is
 * provided by the JDK's introspection facilities themselves.
 * <p/>
 * <p>As a general rule for runtime-retained annotations (e.g. for transaction
 * control, authorization, or service exposure), always use the lookup methods
 * on this class (e.g., {@link #findAnnotation(Method, Class)},
 * {@link #findAnnotation(Method, Class)}, and {@link #getAnnotation(AnnotatedElement, Class)})
 * instead of the plain annotation lookup methods in the JDK. You can still
 * explicitly choose between a <em>get</em> lookup on the given class level only
 * ({@link #findAnnotation(Method, Class)}) and a <em>find</em> lookup in the entire
 * inheritance hierarchy of the given method ({@link #findAnnotation(Method, Class)}).
 * 本类是基于spring AnnotationUtils修改而来,由于不满足功能和代码不够好,决定重写核心功能
 *
 * @author jourrey
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Mark Fisher
 * @author Chris Beams
 * @author Phillip Webb
 * @see java.lang.reflect.Method#getDeclaredAnnotations()
 * @since 2.0
 */
public class AnnotationUtils {
    private static final Logger LOG = LogManager.getLogger(AnnotationUtils.class);
    /*
   *         Who's your daddy ? (Thanks Kevin Bourrillion)
   *
   *           N777777777NO
   *         N7777777777777N
   *        M777777777777777N
   *        $N877777777D77777M
   *       N M77777777ONND777M
   *       MN777777777NN  D777
   *     N7ZN777777777NN ~M7778
   *    N777777777777MMNN88777N
   *    N777777777777MNZZZ7777O
   *    DZN7777O77777777777777
   *     N7OONND7777777D77777N
   *      8$M++++?N???$77777$
   *       M7++++N+M77777777N
   *        N77O777777777777$                              M
   *          DNNM$$$$777777N                              D
   *         N$N:=N$777N7777M                             NZ
   *        77Z::::N777777777                          ODZZZ
   *       77N::::::N77777777M                         NNZZZ$
   *     $777:::::::77777777MN                        ZM8ZZZZZ
   *     777M::::::Z7777777Z77                        N++ZZZZNN
   *    7777M:::::M7777777$777M                       $++IZZZZM
   *   M777$:::::N777777$M7777M                       +++++ZZZDN
   *     NN$::::::7777$$M777777N                      N+++ZZZZNZ
   *       N::::::N:7$O:77777777                      N++++ZZZZN
   *       M::::::::::::N77777777+                   +?+++++ZZZM
   *       8::::::::::::D77777777M                    O+++++ZZ
   *        ::::::::::::M777777777N                      O+?D
   *        M:::::::::::M77777777778                     77=
   *        D=::::::::::N7777777777N                    777
   *       INN===::::::=77777777777N                  I777N
   *      ?777N========N7777777777787M               N7777
   *      77777$D======N77777777777N777N?         N777777
   *     I77777$$$N7===M$$77777777$77777777$MMZ77777777N
   *      $$$$$$$$$$$NIZN$$$$$$$$$M$$7777777777777777ON
   *       M$$$$$$$$M    M$$$$$$$$N=N$$$$7777777$$$ND
   *      O77Z$$$$$$$     M$$$$$$$$MNI==$DNNNNM=~N
   *   7 :N MNN$$$$M$      $$$777$8      8D8I
   *     NMM.:7O           777777778
   *                       7777777MN
   *                       M NO .7:
   *                       M   :   M
   *                            8
   */

    /**
     * The attribute name for annotations with a single element
     */
    public static final String VALUE = "value";
    private static final Cache<AnnotationCacheKey, Optional<Annotation>> annotatedElementAnnotationCache
            = CacheBuilder.newBuilder()
            .weakKeys()
            .weakValues()
            .build();

    /**
     * 解析类上的注解,根据注解是否是Inherited决定递归
     *
     * @param clazz
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A parseClass(Class<?> clazz, Class<A> annotationType) {
        checkNotNull(clazz, "clazz can not null");
        checkNotNull(annotationType, "annotationType can not null");
        A annotation;
        if (null == annotationType.getAnnotation(Inherited.class)) {
            annotation = getAnnotation(clazz, annotationType);
        } else {
            annotation = findAnnotation(clazz, annotationType);
        }
        return annotation;
    }

    /**
     * 解析某个方法上的注解,方法没有则获取类上的,根据注解是否是Inherited决定递归
     *
     * @param method
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A parseMethod(Method method, Class<A> annotationType) {
        checkNotNull(method, "method can not null");
        checkNotNull(annotationType, "annotationType can not null");
        A annotation;
        if (null == annotationType.getAnnotation(Inherited.class)) {
            annotation = getAnnotation(method, annotationType);
        } else {
            annotation = findAnnotation(method, annotationType);
        }
        return annotation == null ? parseClass(method.getDeclaringClass(), annotationType) : annotation;
    }

    /**
     * Get a single {@link Annotation} of {@code annotationType} from the supplied
     * annotation: either the given annotation itself or a meta-annotation thereof.
     *
     * @param ann            the Annotation to check
     * @param annotationType the annotation type to look for, both locally and as a meta-annotation
     * @return the matching annotation, or {@code null} if none found
     * @since 4.0
     */
    public static <T extends Annotation> T getAnnotation(Annotation ann, Class<T> annotationType) {
        if (annotationType.isInstance(ann)) {
            return (T) ann;
        }
        return getAnnotation(ann.annotationType(), annotationType);
    }


    /**
     * 通过注解递归查找注解,包括注解本身和本身的注解
     * java.lang.annotation下,不支持递归{@link #isInJavaLangAnnotationPackage(Annotation)})
     *
     * @param ann            the Annotation to check
     * @param annotationType the annotation type to look for, both locally and as a meta-annotation
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A findAnnotation(final Annotation ann, final Class<A> annotationType) {
        return findAnnotation(ann, annotationType, new HashSet<Annotation>());
    }

    /**
     * 通过注解递归查找注解,包括注解本身和本身的注解
     *
     * @param ann            the Annotation to check
     * @param annotationType the annotation type to look for, both locally and as a meta-annotation
     * @param visited        the set of annotations that have already been visited
     * @param <A>
     * @return
     */
    private static <A extends Annotation> A findAnnotation(final Annotation ann, final Class<A> annotationType
            , final Set<Annotation> visited) {
        if (ann == null) {
            return null;
        }
        if (annotationType.isInstance(ann)) {
            return (A) ann;
        }
        A annotation = getAnnotation(ann.annotationType(), annotationType);
        if (annotation != null) {
            return annotation;
        }
        Annotation[] annotations = ann.annotationType().getDeclaredAnnotations();
        for (Annotation an : annotations) {
            if (!isInJavaLangAnnotationPackage(an) && visited.add(an)) {
                annotation = findAnnotation(an, annotationType, visited);
                if (annotation != null) {
                    return annotation;
                }
            }
        }
        return null;
    }

    /**
     * Get a single {@link Annotation} of {@code annotationType} from the supplied {@link Method}.
     * <p>Correctly handles bridge {@link Method Methods} generated by the compiler.
     *
     * @param method         the method to look for annotations on
     * @param annotationType the annotation type to look for
     * @return the annotations found
     * @see BridgeMethodResolver#findBridgedMethod(Method)
     */
    public static <A extends Annotation> A findAnnotationByBridgeMethod(Method method, Class<A> annotationType) {
        Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
        return findAnnotation(resolvedMethod, annotationType);
    }

    /**
     * 查找方法上注解
     *
     * @param method
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
        return findAnnotation(method, annotationType, new AnnotatedElementRelation<Method>() {
            @Override
            public Class<?>[] getInterfaces(Method method) {
                return method.getDeclaringClass().getInterfaces();
            }

            @Override
            public Class<?> getSuperclass(Method method) {
                return method.getDeclaringClass().getSuperclass();
            }

            @Override
            public AnnotatedElement next(Class<?> declaringClass, Method method) {
                try {
                    return declaringClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                } catch (NoSuchMethodException ex) {
                    // No equivalent method found
                    return null;
                }
            }
        });
    }

    /**
     * 查找类上注解
     *
     * @param clazz
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
        return findAnnotation(clazz, annotationType, new AnnotatedElementRelation<Class<?>>() {
            @Override
            public Class<?>[] getInterfaces(Class<?> aClass) {
                return aClass.getInterfaces();
            }

            @Override
            public Class<?> getSuperclass(Class<?> aClass) {
                return aClass.getSuperclass();
            }

            @Override
            public AnnotatedElement next(Class<?> declaringClass, Class<?> aClass) {
                return declaringClass;
            }
        });
    }

    /**
     * AnnotatedElement递归关系
     */
    public interface AnnotatedElementRelation<T extends AnnotatedElement> {
        /**
         * AnnotatedElement实现接口
         *
         * @param t
         * @return
         */
        Class<?>[] getInterfaces(T t);

        /**
         * AnnotatedElement继承类
         *
         * @param t
         * @return
         */
        Class<?> getSuperclass(T t);

        /**
         * 迭代AnnotatedElement
         *
         * @param declaringClass
         * @param t
         * @return
         */
        AnnotatedElement next(Class<?> declaringClass, T t);
    }

    /**
     * 递归查找注解
     * 深度优先遍历,优先级:自己(包含JDK Inherited策略),接口,注解,父类
     *
     * @param element
     * @param annotationType
     * @param relation
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A findAnnotation(AnnotatedElement element, Class<A> annotationType
            , AnnotatedElementRelation relation) {
        if (element == null) {
            return null;
        }
        A annotation = getAnnotation(element, annotationType);
        if (annotation != null) {
            return annotation;
        }
        Class<?>[] interfaces = relation.getInterfaces(element);
        for (Class<?> ifc : interfaces) {
            annotation = findAnnotation(relation.next(ifc, element), annotationType, relation);
            if (annotation != null) {
                return annotation;
            }
        }
        Annotation[] annotations = element.getDeclaredAnnotations();
        for (Annotation ann : annotations) {
            annotation = getAnnotation(ann, annotationType);
            if (annotation != null) {
                return annotation;
            }
        }
        Class<?> superclass = relation.getSuperclass(element);
        if (superclass == null || superclass.equals(Object.class)) {
            return null;
        }
        return findAnnotation(relation.next(superclass, element), annotationType, relation);
    }

    /**
     * 查找AnnotatedElement上声明的注解
     * {@link AnnotatedElement#getAnnotations()}必然包含{@link AnnotatedElement#getDeclaredAnnotations()}
     * 所以这里进行优化直接使用的是{@link AnnotatedElement#getAnnotation(Class)}
     *
     * @param element        the Class, Method, Constructor or Field from which to get the annotation
     * @param annotationType the annotation type to look for, both locally and as a meta-annotation
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A getAnnotation(AnnotatedElement element, Class<A> annotationType) {
        if (element == null) {
            return null;
        }
        final AnnotationCacheKey cacheKey = new AnnotationCacheKey(element, annotationType);
        Optional<Annotation> annotation = Optional.absent();
        try {
            annotation = annotatedElementAnnotationCache.get(cacheKey, new Callable<Optional<Annotation>>() {
                @Override
                public Optional<Annotation> call() throws Exception {
                    return Optional.fromNullable(cacheKey.element.getAnnotation(cacheKey.annotationType));
                }
            });
        } catch (ExecutionException e) {
            // Assuming nested Class values not resolvable within annotation attributes...
            logIntrospectionFailure(cacheKey.element, e);
        }
        return annotation.isPresent() ? (A) annotation.get() : null;
    }

    /**
     * Determine if the supplied {@link Annotation} is defined in the core JDK
     * {@code java.lang.annotation} package.
     *
     * @param annotation the annotation to check (never {@code null})
     * @return {@code true} if the annotation is in the {@code java.lang.annotation} package
     */
    public static boolean isInJavaLangAnnotationPackage(Annotation annotation) {
        checkNotNull(annotation, "Annotation must not be null");
        return annotation.annotationType().getName().startsWith("java.lang.annotation");
    }

    /**
     * 注解是否在此element声明,用来去除Inherited属性注解影响
     * {@linkplain java.lang.annotation.Inherited inherited}.
     * {@link #isAnnotationInherited(Class, Class)} instead.
     *
     * @param annotationType the Class object corresponding to the annotation type
     * @param element        the Class object corresponding to the class on which to check for the annotation
     * @return {@code true} if an annotation for the specified {@code annotationType}
     * is declared locally on the supplied {@code element}
     * @see Class#getDeclaredAnnotations()
     * @see #isAnnotationInherited(Class, Class)
     */
    public static boolean isAnnotationDeclaredLocally(Class<? extends Annotation> annotationType, AnnotatedElement element) {
        checkNotNull(annotationType, "Annotation type must not be null");
        checkNotNull(element, "AnnotatedElement must not be null");
        for (Annotation ann : element.getDeclaredAnnotations()) {
            if (ann.annotationType().equals(annotationType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether an annotation for the specified {@code annotationType} is present
     * on the supplied {@code clazz} and is {@linkplain java.lang.annotation.Inherited inherited}
     * (i.e., not declared locally for the class).
     * <p>If the supplied {@code clazz} is an interface, only the interface itself will be checked.
     * In accordance with standard meta-annotation semantics, the inheritance hierarchy for interfaces
     * will not be traversed. See the {@linkplain java.lang.annotation.Inherited Javadoc} for the
     * {@code @Inherited} meta-annotation for further details regarding annotation inheritance.
     *
     * @param annotationType the Class object corresponding to the annotation type
     * @param clazz          the Class object corresponding to the class on which to check for the annotation
     * @return {@code true} if an annotation for the specified {@code annotationType} is present
     * on the supplied {@code clazz} and is <em>inherited</em>
     * @see Class#isAnnotationPresent(Class)
     * @see #isAnnotationDeclaredLocally(Class, AnnotatedElement)
     */
    public static boolean isAnnotationInherited(Class<? extends Annotation> annotationType, Class<?> clazz) {
        checkNotNull(annotationType, "Annotation type must not be null");
        checkNotNull(clazz, "Class must not be null");
        return (clazz.isAnnotationPresent(annotationType) && !isAnnotationDeclaredLocally(annotationType, clazz));
    }

    /**
     * Retrieve the <em>value</em> of the {@code &quot;value&quot;} attribute of a
     * single-element Annotation, given an annotation instance.
     *
     * @param annotation the annotation instance from which to retrieve the value
     * @return the attribute value, or {@code null} if not found
     * @see #getValue(Annotation, String)
     */
    public static Object getValue(Annotation annotation) {
        return getValue(annotation, VALUE);
    }

    /**
     * Retrieve the <em>value</em> of a named attribute, given an annotation instance.
     *
     * @param annotation    the annotation instance from which to retrieve the value
     * @param attributeName the name of the attribute value to retrieve
     * @return the attribute value, or {@code null} if not found
     * @see #getValue(Annotation)
     */
    public static Object getValue(Annotation annotation, String attributeName) {
        if (annotation == null || StringUtils.isBlank(attributeName)) {
            return null;
        }
        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName);
            ReflectionUtils.makeAccessible(method);
            return method.invoke(annotation);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Retrieve the <em>default value</em> of the {@code &quot;value&quot;} attribute
     * of a single-element Annotation, given an annotation instance.
     *
     * @param annotation the annotation instance from which to retrieve the default value
     * @return the default value, or {@code null} if not found
     * @see #getDefaultValue(Annotation, String)
     */
    public static Object getDefaultValue(Annotation annotation) {
        return getDefaultValue(annotation, VALUE);
    }

    /**
     * Retrieve the <em>default value</em> of a named attribute, given an annotation instance.
     *
     * @param annotation    the annotation instance from which to retrieve the default value
     * @param attributeName the name of the attribute value to retrieve
     * @return the default value of the named attribute, or {@code null} if not found
     * @see #getDefaultValue(Class, String)
     */
    public static Object getDefaultValue(Annotation annotation, String attributeName) {
        if (annotation == null) {
            return null;
        }
        return getDefaultValue(annotation.annotationType(), attributeName);
    }

    /**
     * Retrieve the <em>default value</em> of the {@code &quot;value&quot;} attribute
     * of a single-element Annotation, given the {@link Class annotation type}.
     *
     * @param annotationType the <em>annotation type</em> for which the default value should be retrieved
     * @return the default value, or {@code null} if not found
     * @see #getDefaultValue(Class, String)
     */
    public static Object getDefaultValue(Class<? extends Annotation> annotationType) {
        return getDefaultValue(annotationType, VALUE);
    }

    /**
     * Retrieve the <em>default value</em> of a named attribute, given the
     * {@link Class annotation type}.
     *
     * @param annotationType the <em>annotation type</em> for which the default value should be retrieved
     * @param attributeName  the name of the attribute value to retrieve.
     * @return the default value of the named attribute, or {@code null} if not found
     * @see #getDefaultValue(Annotation, String)
     */
    public static Object getDefaultValue(Class<? extends Annotation> annotationType, String attributeName) {
        if (annotationType == null || StringUtils.isBlank(attributeName)) {
            return null;
        }
        try {
            return annotationType.getDeclaredMethod(attributeName).getDefaultValue();
        } catch (Exception ex) {
            return null;
        }
    }

    private static void logIntrospectionFailure(AnnotatedElement annotatedElement, Exception ex) {
        LOG.info("Failed to introspect annotations on [{}]: {}", annotatedElement, ex);
    }

    /**
     * Cache key for the AnnotatedElement cache.
     */
    private static class AnnotationCacheKey {

        private final AnnotatedElement element;
        private final Class<? extends Annotation> annotationType;

        public AnnotationCacheKey(AnnotatedElement element, Class<? extends Annotation> annotationType) {
            this.element = element;
            this.annotationType = annotationType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            AnnotationCacheKey that = (AnnotationCacheKey) o;

            return new EqualsBuilder()
                    .append(element, that.element)
                    .append(annotationType, that.annotationType)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(element)
                    .append(annotationType)
                    .toHashCode();
        }
    }
}
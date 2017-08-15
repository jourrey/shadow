package com.dianping.shadow.aspect.instrument.transtormer;

import com.dianping.shadow.aspect.instrument.AgentInvocation;
import com.dianping.shadow.aspect.instrument.InstrumentAspectInterceptor;
import com.dianping.shadow.context.AspectContext;
import com.dianping.shadow.context.JoinPointBean;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import javassist.ByteArrayClassPath;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;

/**
 * Created by jourrey on 17/2/23.
 */
public class JavassistTransformer implements ClassFileTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(JavassistTransformer.class);
    private static final String FILE_SEPARATOR = File.separator;
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String TARGET_METHOD_NAME_SUFFIX = "$SHADOW";

    private static final ThreadLocal<Collection<String>> classNameList = new ThreadLocal<Collection<String>>() {
        public Collection<String> initialValue() {
            return Collections2.transform(AspectContext.getInstance().getJoinPoint()
                    , new Function<JoinPointBean, String>() {
                @Override
                public String apply(JoinPointBean input) {
                    return input.getDeclaringClass().getCanonicalName();
                }
            });
        }
    };

    private static final ThreadLocal<Collection<String>> memberNameList = new ThreadLocal<Collection<String>>() {
        public Collection<String> initialValue() {
            return Collections2.transform(AspectContext.getInstance().getJoinPoint()
                    , new Function<JoinPointBean, String>() {
                @Override
                public String apply(JoinPointBean input) {
                    return input.getMember().getName();
                }
            });
        }
    };

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined
            , ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace(FILE_SEPARATOR, PACKAGE_SEPARATOR);
        // 没有JoinPoint的类直接跳过
        if (!classNameList.get().contains(className) || this.getClass().getCanonicalName().equals(className)) {
            return null;
        }
        LOG.info("ClassFileTransformer className {}", className);
        try {
            ClassPool.doPruning = true;//减少javassist的内存消耗
            ClassPool pool = ClassPool.getDefault();//只是搜索JVM的同路径下的class
            //运行在JBoss或者Tomcat下，ClassPool Object 可能找不到用户的classes。需要添加classpath
            pool.insertClassPath(new ClassClassPath(this.getClass()));
            pool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
            CtClass targetClass = pool.get(className);// 使用全称,用于取得字节码类<使用javassist>
//            targetClass.stopPruning(true);
            for (CtMethod targetMethod : targetClass.getDeclaredMethods()) {
                // 抽象方法不织入
                if (Modifier.isAbstract(targetMethod.getModifiers())) {
                    continue;
                }
                String targetMethodName = targetMethod.getName();
                // 非JoinPoint的方法直接跳过,这里只要同名就好
                if (!memberNameList.get().contains(targetMethodName)) {
                    continue;
                }
                // 复制目标的方法,创建代理方法,名字为目标方法的名字
                CtMethod proxyMethod = CtNewMethod.copy(targetMethod, targetMethodName, targetClass, null);
                // 将目标方法名字修改为:methodName$SHADOW
                targetMethodName = targetMethodName + TARGET_METHOD_NAME_SUFFIX;
                targetMethod.setName(targetMethodName);

                // 构建代理方法体
                StringBuilder bodyStr = new StringBuilder();
                bodyStr.append("{\n");
                // 获取参数名称
                MethodInfo methodInfo = targetMethod.getMethodInfo();
                CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                LocalVariableAttribute attr = null;
                if (codeAttribute != null) {
                    attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                }
                String[] paramNames;
                if (attr == null || attr.tableLength() == 0) {
                    paramNames = ArrayUtils.EMPTY_STRING_ARRAY;
                } else {
                    paramNames = new String[targetMethod.getParameterTypes().length];
                    int pos = Modifier.isStatic(targetMethod.getModifiers()) ? 0 : 1;
                    for (int i = 0; i < paramNames.length; i++) {
                        paramNames[i] = attr.variableName(i + pos);
                    }
                }
                String paramNameStr = StringUtils.join(paramNames, AgentInvocation.PARAM_NAME_SEPARATOR);

                // 开始织入拦截器
                bodyStr.append(AgentInvocation.class.getName() + " agentInvocation = new " + AgentInvocation.class.getName() + "();\n");
                if (!Modifier.isStatic(targetMethod.getModifiers())) {
                    bodyStr.append("agentInvocation.setTarget($0);\n");
                }
                bodyStr.append("agentInvocation.setTargetClass($class);\n");
                bodyStr.append("agentInvocation.setTargetMethodName(\"" + targetMethod.getName() + "\");\n");
                bodyStr.append("agentInvocation.setParamTypes($sig);\n");
                bodyStr.append("agentInvocation.setParams($args);\n");
                bodyStr.append("agentInvocation.parseParamNames(\"" + paramNameStr + "\");\n");

                // 返回类型处理
                String returnType = targetMethod.getReturnType().getName();
                if (!"void".equals(returnType)) {
                    bodyStr.append(returnType + " result = (" + returnType + ")");
                }
                bodyStr.append(InstrumentAspectInterceptor.class.getName() + ".intercept(agentInvocation);\n");// 调用原有代码，类似于method();($$)表示所有的参数
                if (!"void".equals(returnType)) {
                    bodyStr.append("return result;\n");
                }
                bodyStr.append("}");

                // 设置代理方法体
                proxyMethod.setBody(bodyStr.toString());

                // 向targetClass添加代理方法
                targetClass.addMethod(proxyMethod);
            }
            return targetClass.toBytecode();
        } catch (Exception e) {
            LOG.error("ClassFileTransformer Exception", e);
            //忽略异常处理
        }
        return null;
    }

//    @Override
//    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined
//            , ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//        className = className.replace(FILE_SEPARATOR, PACKAGE_SEPARATOR);
//        if (!className.startsWith(basePackage)) {
//            return null;
//        }
//        try {
//            CtClass targetClass = ClassPool.getDefault().get(className);// 使用全称,用于取得字节码类<使用javassist>
//            for (CtMethod targetMethod : targetClass.getDeclaredMethods()) {
//                if (ArrayUtils.isNotEmpty(targetMethod.getParameterTypes())) {
//                    System.out.println(targetMethod.getParameterTypes()[0].getName());
//                } else {
//                    System.out.println("No Parameter");
//                }
//                // 狸猫换太子
//                String targetMethodName = targetMethod.getName();
//                // 创建代理方法,复制原来的方法,名字为原来的名字
//                CtMethod proxyMethod = CtNewMethod.copy(targetMethod, targetMethodName, targetClass, null);
//                // 新定义一个方法叫做比如sayHello$old,将原来的方法名字修改
//                targetMethodName = targetMethodName + TARGET_METHOD_NAME_SUFFIX;
//                targetMethod.setName(targetMethodName);
//
//                // 构建代理方法体
//                String returnType = targetMethod.getReturnType().getName();
//                System.out.println("className:" + targetClass.getName());
//                System.out.println("targetMethodName:" + targetMethodName);
//                System.out.println("returnType:" + returnType);
//                System.out.println("==============================");
//                StringBuilder bodyStr = new StringBuilder();
//                bodyStr.append("{\n");
//                bodyStr.append("System.out.println(\"this class " + targetClass.getName() + "\");\n");
//                bodyStr.append("Object[] param = $args;\n");
//                bodyStr.append("System.out.println(\"this param \" + com.alibaba.fastjson.JSON.toJSONString(param));\n");
//                if (!"void".equals(returnType)) {
//                    bodyStr.append(returnType + " result = ");
//                }
//                bodyStr.append(targetMethodName + "($$);\n");// 调用原有代码，类似于method();($$)表示所有的参数
//                if (!"void".equals(returnType)) {
//                    bodyStr.append("System.out.println(\"this result \" + result);\n");
//                    bodyStr.append("return result;\n");
//                }
//                bodyStr.append("}");
//
//                // 设置代理方法体
//                proxyMethod.setBody(bodyStr.toString());
//                // 向targetClass增加代理方法
//                targetClass.addMethod(proxyMethod);
//            }
//            return targetClass.toBytecode();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//        return null;
//    }

}

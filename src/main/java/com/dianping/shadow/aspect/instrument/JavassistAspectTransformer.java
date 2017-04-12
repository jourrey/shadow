package com.dianping.shadow.aspect.instrument;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by jourrey on 17/2/23.
 */
public class JavassistAspectTransformer implements ClassFileTransformer {
    private static final String FILE_SEPARATOR = File.separator;
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String TARGET_METHOD_NAME_SUFFIX = "$SHADOW";

    // 被处理的方法列表
    private String basePackage;

    public JavassistAspectTransformer(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined
            , ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace(FILE_SEPARATOR, PACKAGE_SEPARATOR);
        if (!className.startsWith(basePackage)) {
            return null;
        }
        try {
            CtClass targetClass = ClassPool.getDefault().get(className);// 使用全称,用于取得字节码类<使用javassist>
            for (CtMethod targetMethod : targetClass.getDeclaredMethods()) {
                if (ArrayUtils.isNotEmpty(targetMethod.getParameterTypes())) {
                    System.out.println(targetMethod.getParameterTypes()[0].getName());
                } else {
                    System.out.println("No Parameter");
                }
                // 狸猫换太子
                String targetMethodName = targetMethod.getName();
                // 创建代理方法,复制原来的方法,名字为原来的名字
                CtMethod proxyMethod = CtNewMethod.copy(targetMethod, targetMethodName, targetClass, null);
                // 新定义一个方法叫做比如sayHello$old,将原来的方法名字修改
                targetMethodName = targetMethodName + TARGET_METHOD_NAME_SUFFIX;
                targetMethod.setName(targetMethodName);

                // 构建代理方法体
                String returnType = targetMethod.getReturnType().getName();
                System.out.println("className:" + targetClass.getName());
                System.out.println("targetMethodName:" + targetMethodName);
                System.out.println("returnType:" + returnType);
                System.out.println("==============================");
                StringBuilder bodyStr = new StringBuilder();
                bodyStr.append("{\n");
                bodyStr.append("System.out.println(\"this class " + targetClass.getName() + "\");\n");
                bodyStr.append("Object[] param = $args;\n");
                bodyStr.append("System.out.println(\"this param \" + com.alibaba.fastjson.JSON.toJSONString(param));\n");
                if (!"void".equals(returnType)) {
                    bodyStr.append(returnType + " result = ");
                }
                bodyStr.append(targetMethodName + "($$);\n");// 调用原有代码，类似于method();($$)表示所有的参数
                if (!"void".equals(returnType)) {
                    bodyStr.append("System.out.println(\"this result \" + result);\n");
                    bodyStr.append("return result;\n");
                }
                bodyStr.append("}");

                // 设置代理方法体
                proxyMethod.setBody(bodyStr.toString());
                // 向targetClass增加代理方法
                targetClass.addMethod(proxyMethod);
            }
            return targetClass.toBytecode();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined
//            , ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//        className = className.replace("/", ".");
//        if (!className.startsWith("com.dian")) {
//            return null;
//        }
//        try {
//            CtClass ctclass = ClassPool.getDefault().get(className);// 使用全称,用于取得字节码类<使用javassist>
//            for (CtMethod ctmethod : ctclass.getDeclaredMethods()) {
//                String methodName = ctmethod.getName();
//                String returnType = ctmethod.getReturnType().getName();
//
//                String newMethodName = methodName + "$old";// 新定义一个方法叫做比如sayHello$old
//                ctmethod.setName(newMethodName);// 将原来的方法名字修改
//                // 创建新的方法，复制原来的方法，名字为原来的名字
//                CtMethod newMethod = CtNewMethod.copy(ctmethod, methodName, ctclass, null);
//                System.out.println("className:" + ctclass.getName());
//                System.out.println("methodName:" + methodName);
//                System.out.println("returnType:" + returnType);
//                System.out.println("==============================");
//                // 构建新的方法体
//                StringBuilder bodyStr = new StringBuilder();
//                bodyStr.append("{\n");
//                bodyStr.append("System.out.println(\"this class " + ctclass.getName() + "\");\n");
//                if (!"void".equals(returnType)) {
//                    bodyStr.append(returnType + " result = ");
//                }
//                bodyStr.append(newMethodName + "($$);\n");// 调用原有代码，类似于method();($$)表示所有的参数
//                bodyStr.append("Object[] param = $args;\n");
//                bodyStr.append("System.out.println(\"this param \" + com.alibaba.fastjson.JSON.toJSONString(param));\n");
//                if (!"void".equals(returnType)) {
//                    bodyStr.append("System.out.println(\"this result \" + result);\n");
//                }
//                if (!"void".equals(returnType)) {
//                    bodyStr.append("return result;\n");
//                }
//                bodyStr.append("}");
//
//                newMethod.setBody(bodyStr.toString());// 替换新方法
//                ctclass.addMethod(newMethod);// 增加新方法
//            }
//            return ctclass.toBytecode();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//        return null;
//    }

}

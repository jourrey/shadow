package com.dianping.shadow.aspect.instrument;

import org.apache.commons.lang3.StringUtils;

import java.lang.instrument.Instrumentation;
import java.text.MessageFormat;
import java.util.regex.Pattern;

/**
 * Created by jourrey on 17/2/23.
 * 在启动时，使用-javaagent方式加入代理
 * 执行命令:
 * <code>
 * java -javaagent:/Users/Documents/myagent.jar="Here, your can input agent arguments"
 * java -javaagent:/Users/../shadow-1.1.0-SNAPSHOT.jar -cp /Users/../shadow-1.1.0-SNAPSHOT.jar com.dianping.shadow.silhouette.SilhouetteDefinition
 * </code>
 */
public class LoadAgent {
    private static final String PACKAGE_NAME_RULE = "[.a-zA-Z0-9_]+";
    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile(PACKAGE_NAME_RULE);

    /**
     * 如果Agent是通过JVM选项的方式捆绑到程序中，则在JVM初化完毕后，会执行premain方法，premain执行之后才是程序的main方法。
     * 清单文件中需要指定Premain-Class
     * <p>
     * premain有两种形式，默认会执行1), 如果没有1)则会执行2), 1)和2)只会执行一个<br>
     * <code>
     * 1) public static void premain(String agentArgs, Instrumentation instrumentation)<br/>
     * 2) public static void premain(String agentArgs)
     * </code></p>
     *
     * @param args
     * @param inst
     */
    public static void premain(String args, Instrumentation inst) {
        System.out.println(MessageFormat.format("basePackage:{0}", args)); // 不用Log框架是因为命令行执行,上下文需要直接打印
        if (StringUtils.isBlank(args)) {
            System.out.println("Parameter must be a valid package name");
            return;
        }
        if (!PACKAGE_NAME_PATTERN.matcher(args).matches()) {
            System.out.println(MessageFormat.format("{0} package name format error" +
                    ", A scope name can contain only {1} characters.", args, PACKAGE_NAME_RULE));
            return;
        }
        inst.addTransformer(new JavassistAspectTransformer(args));
    }

    /**
     * 如果Agent是在程序运行过程中，动态的捆绑到程序中，则是执行agentmain方法。
     * 清单文件中要指定 Agent-Class
     * <p>
     * agentmain有两种形式，默认会执行1), 如果没有1)则会执行2), 1)和2)只会执行一个<br>
     * <code>
     * 1) public static void agentmain(String agentArgs, Instrumentation instrumentation)<br/>
     * 2) public static void agentmain(String agentArgs)
     * </code></p>
     * <p/>
     *
     * @param args
     * @param inst
     */
    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("Hi, I'm agentmain agent!");
        inst.addTransformer(new AspectTransformer());
    }

}

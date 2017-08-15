package com.dianping.shadow.context.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * 类扫描器
 * Created by jourrey on 16/10/26.
 */
public class ClassScanner extends AbstractScanner<Class<?>> {
    private static final Logger LOG = LoggerFactory.getLogger(ClassScanner.class);

    private static final String FILE_SEPARATOR = File.separator;
    private static final String FILE_SUFFIX = ".class";
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String BASE_PACKAGE_RULE = "[.a-zA-Z0-9_]+";
    private static final Pattern BASE_PACKAGE_PATTERN = Pattern.compile(BASE_PACKAGE_RULE);
    private static final String CURRENT_PACKAGE_RULE = "[a-zA-Z0-9_]+" + FILE_SUFFIX;
    private static final Pattern CURRENT_PACKAGE_PATTERN = Pattern.compile(CURRENT_PACKAGE_RULE);

    private String basePackage;
    private boolean recursive;
    private String fileEncode;
    private String basePackageDir;

    ClassScanner(String basePackage, boolean recursive, String fileEncode, ScannerFilter<Class<?>> filter) {
        this.basePackage = basePackage;
        this.basePackageDir = basePackage.replace(PACKAGE_SEPARATOR, FILE_SEPARATOR);
        this.recursive = recursive;
        this.fileEncode = fileEncode;
        this.filter = filter;
    }

    @Override
    public Set<Class<?>> doScan() {
        LOG.debug("package:{}", basePackage);
        if (!BASE_PACKAGE_PATTERN.matcher(basePackage).matches()) {
            throw new IllegalArgumentException(MessageFormat.format("{0} package name format error" +
                    ", A scope name can contain only {1} characters.", basePackage, BASE_PACKAGE_RULE));
        }
        init();
        Enumeration<URL> dirs;
        try {
            dirs = getClassLoader().getResources(basePackageDir);
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                if (isFileProtocol(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), fileEncode);
                    String rootPath = filePath.substring(0, filePath.length() - basePackageDir.length() - 1);
                    findAndAddClassesInPackageByFile(filePath, rootPath);
                } else if (isJarProtocol(protocol)) {
                    findAndAddClassesInPackageByJarFile(url);
                }
            }
        } catch (IOException e) {
            LOG.error("doScan Exception", e);
        }
        return contains;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packagePath
     * @param rootPath
     */
    private void findAndAddClassesInPackageByFile(String packagePath, String rootPath) {
        //获取此包的目录 建立一个File
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirFiles = dir.listFiles();
        if (dirFiles == null) {
            return;
        }
        //循环所有文件
        for (File file : dirFiles) {
            //如果是目录 则继续扫描
            if (file.isDirectory()) {
                if (recursive) {
                    findAndAddClassesInPackageByFile(file.getAbsolutePath(), rootPath);
                } else {
                    LOG.debug("recursive is {} abandon directory {}", recursive, file.getName());
                }
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名
                loadClass(file.getAbsolutePath().substring(rootPath.length()));
            }
        }
    }

    /**
     * 以Jar的形式来获取包下的所有Class
     *
     * @param baseUrl
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void findAndAddClassesInPackageByJarFile(URL baseUrl) throws IOException {
        // 获取jar
        JarFile jar = ((JarURLConnection) baseUrl.openConnection()).getJarFile();
        // 从此jar包 得到一个枚举类
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            //获取jar里的一个实体 可以是目录和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            String classFullName = entry.getName();
            //目录 不是basePackageDir开头 不是FILE_SUFFIX结尾 直接跳过
            if (entry.isDirectory() || !classFullName.startsWith(basePackageDir)) {
                continue;
            }
            //不是递归查找,校验是否当前目录,不是则跳过
            if (!recursive) {
                String classNameNotSuffix = classFullName.substring(basePackageDir.length() + 1);
                if (!CURRENT_PACKAGE_PATTERN.matcher(classNameNotSuffix).matches()) {
                    continue;
                }
            }
            loadClass(classFullName);
        }
    }

    /**
     * 通过ClassLoader加载该类
     *
     * @param classFullName
     */
    private void loadClass(String classFullName) {
        try {
            if (!classFullName.endsWith(FILE_SUFFIX)) {
                return;
            }
            // 去除开头的分隔符
            if (FILE_SEPARATOR.equals(String.valueOf(classFullName.charAt(0)))) {
                classFullName = classFullName.substring(1);
            }
            classFullName = classFullName.substring(0, classFullName.length() - FILE_SUFFIX.length())
                    .replaceAll(FILE_SEPARATOR, PACKAGE_SEPARATOR);

            Class singleClass = getClassLoader().loadClass(classFullName);
            if (filter != null && filter.accept(singleClass)) {
                contains.add(singleClass);
            }
        } catch (Throwable th) {
            LOG.error("loadClass {}", th);
        }
    }

    private boolean isFileProtocol(String protocol) {
        return ProtocolType.FILE.value.equalsIgnoreCase(protocol);
    }

    private boolean isJarProtocol(String protocol) {
        return ProtocolType.JAR.value.equalsIgnoreCase(protocol);
    }

    public enum ProtocolType {
        FILE("file"), JAR("jar");

        private final String value;

        ProtocolType(String value) {
            this.value = value;
        }
    }

    public static class ClassScannerBuilder {
        private static final String DEFAULT_PACKAGE = "";
        private static final String FILE_ENCODE = "UTF-8";

        private String basePackage;
        private boolean recursive;
        private String fileEncode;
        private ScannerFilter filter;

        public ClassScannerBuilder() {
            this.basePackage = DEFAULT_PACKAGE;
            this.recursive = true;
            this.fileEncode = FILE_ENCODE;
            this.filter = getDefaultFilter();
        }

        public ClassScannerBuilder basePackage(String basePackage) {
            this.basePackage = basePackage;
            return this;
        }

        public ClassScannerBuilder recursive(boolean recursive) {
            this.recursive = recursive;
            return this;
        }

        public ClassScannerBuilder filter(ScannerFilter filter) {
            this.filter = filter;
            return this;
        }

        public ClassScanner build() {
            return new ClassScanner(this.basePackage, this.recursive, this.fileEncode, this.filter);
        }
    }

}

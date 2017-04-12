package com.dianping.shadow.util;

import java.io.File;

/**
 * Created by jourrey on 17/2/23.
 */
public class RootPathUtils {
    private static final String CHARSET_NAME = "UTF-8";
    public static final String JAR_SUFFIX = ".jar";

    /**
     * 获取环境根目录绝对路径
     *
     * @return
     */
    public static String getRootAbsolutePath() {
        File file = getRootPath();
        if (file == null)
            return null;
        return file.getAbsolutePath();
    }

    /**
     * 获取环境根目录父目录
     *
     * @return
     */
    public static String getRootParentPath() {
        File file = getRootPath();
        if (file == null)
            return null;
        return file.getParent();
    }

    /**
     * 获取环境根目录名称
     *
     * @return
     */
    public static String getRootName() {
        File file = getRootPath();
        if (file == null)
            return null;
        return file.getName();
    }

    /**
     * 是否是jar包
     *
     * @return
     */
    public static boolean isJar() {
        File file = getRootPath();
        if (file == null)
            return false;
        return file.getAbsolutePath().endsWith(JAR_SUFFIX);
    }

    /**
     * 获取环境根目录
     *
     * @return
     */
    private static File getRootPath() {
        String filePath = RootPathUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            filePath = java.net.URLDecoder.decode(filePath, CHARSET_NAME); // 转换处理中文及空格
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return new File(filePath);
    }

    public static void main(String[] args) {
        System.out.println(RootPathUtils.getRootAbsolutePath());
        System.out.println(RootPathUtils.getRootParentPath());
        System.out.println(RootPathUtils.getRootName());
        System.out.println(RootPathUtils.isJar());
    }

}

//package com.dianping.shadow.aspect.classloader;
//
//import java.io.IOException;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.net.URLStreamHandlerFactory;
//import java.util.HashSet;
//import java.util.StringTokenizer;
//
///**
// * Created by jourrey on 17/2/23.
// */
//public class AspectClassLoader extends URLClassLoader {
//
//    private String _name;
//    private WebAppContext _context;
//    private ClassLoader _parent;
//    private HashSet _extensions;
//
//    public AspectClassLoader(ClassLoader parent, WebAppContext context)
//            throws IOException
//    {
//        super(new URL[]{},parent!=null?parent
//                :(Thread.currentThread().getContextClassLoader()!=null?Thread.currentThread().getContextClassLoader()
//                :(AspectClassLoader.class.getClassLoader()!=null?AspectClassLoader.class.getClassLoader()
//                :ClassLoader.getSystemClassLoader())));
//        _parent=getParent();
//        _context=context;
//        if (_parent==null)
//            throw new IllegalArgumentException("no parent classloader!");
//
//        _extensions = new HashSet();
//        _extensions.add(".jar");
//        _extensions.add(".zip");
//
//        String extensions = System.getProperty(WebAppClassLoader.class.getName() + ".extensions");
//        if(extensions!=null)
//        {
//            StringTokenizer tokenizer = new StringTokenizer(extensions, ",;");
//            while(tokenizer.hasMoreTokens())
//                _extensions.add(tokenizer.nextToken().trim());
//        }
//
//        if (context.getExtraClasspath()!=null)
//            addClassPath(context.getExtraClasspath());
//    }
//
//}

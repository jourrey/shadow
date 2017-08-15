package com.dianping.shadow.context.init;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by jourrey on 16/11/10.
 */
public class ContextLoaderListener implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(ContextLoaderListener.class);
    private static final String BASE_PACKAGE = "shadowBasePackage";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        String basePackage = servletContext.getInitParameter(BASE_PACKAGE);
        if (StringUtils.isBlank(basePackage)) {
            LOG.warn("shadowBasePackage can not null");
            return;
        }
        try {
            InitApplicationContext.scan(basePackage);
        } catch (Throwable th) {
            LOG.error("contextInitialized {}", th);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}

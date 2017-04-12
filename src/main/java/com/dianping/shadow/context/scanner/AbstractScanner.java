package com.dianping.shadow.context.scanner;

import com.dianping.shadow.util.ClassUtils;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 扫描器
 * Created by jourrey on 16/10/26.
 */
public abstract class AbstractScanner<T> implements Serializable {
    private static final long serialVersionUID = -8248544591810649490L;

    protected ScannerFilter<T> filter;
    protected ClassLoader classLoader;
    protected Set<T> contains;

    public abstract Set<T> doScan();

    protected void init() {
        this.classLoader = ClassUtils.getDefaultClassLoader();
        this.contains = new LinkedHashSet<T>();
    }

    protected ClassLoader getClassLoader() {
        return classLoader;
    }

    protected static ScannerFilter getDefaultFilter() {
        return DefaultScannerFilter.FILTER.scannerFilter;
    }

    public interface ScannerFilter<T> {
        boolean accept(T t);
    }

    public enum DefaultScannerFilter {
        FILTER(new ScannerFilter() {
            @Override
            public boolean accept(Object o) {
                return true;
            }
        });

        private final ScannerFilter scannerFilter;

        DefaultScannerFilter(ScannerFilter scannerFilter) {
            this.scannerFilter = scannerFilter;
        }
    }
}

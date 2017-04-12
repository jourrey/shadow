package com.dianping.shadow.context.scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 方法扫描器
 * Created by jourrey on 16/10/26.
 */
public class MemberScanner extends AbstractScanner<Member> {
    private static final Logger LOG = LogManager.getLogger(MemberScanner.class);

    private Class<?> clazz;

    public MemberScanner(Class<?> clazz, ScannerFilter<Member> filter) {
        this.clazz = clazz;
        this.filter = filter;
    }

    @Override
    public Set<Member> doScan() {
        LOG.debug("class:{}", clazz);
        init();
        Method[] methods;
        methods = clazz.getDeclaredMethods();
        if (methods == null) {
            return contains;
        }
        for (Method method : methods) {
            if ((filter == null) || filter.accept(method)) {
                contains.add(method);
            }
        }
        return contains;
    }

    public static class MemberScannerBuilder {
        private Class<?> clazz;
        private ScannerFilter filter;

        public MemberScannerBuilder() {
            this.clazz = this.getClass();
            this.filter = getDefaultFilter();
        }

        public MemberScannerBuilder clazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public MemberScannerBuilder filter(ScannerFilter filter) {
            this.filter = filter;
            return this;
        }

        public MemberScanner build() {
            return new MemberScanner(this.clazz, this.filter);
        }
    }

}

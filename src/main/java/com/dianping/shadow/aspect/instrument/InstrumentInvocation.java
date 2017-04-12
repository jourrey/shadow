package com.dianping.shadow.aspect.instrument;

/**
 * Instrument调用对象
 *
 * @author jourrey
 */
public interface InstrumentInvocation {

    String getClassName();

    String getMethodName();

}

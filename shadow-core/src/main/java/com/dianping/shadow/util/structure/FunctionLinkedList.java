package com.dianping.shadow.util.structure;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 函数遍历链表
 * Created by jourrey on 16/10/14.
 */
public class FunctionLinkedList<E> extends LinkedList<E> {

    /**
     * 链表的遍历(从head顺序遍历)
     *
     * @param function
     */
    public void traversalFromHead(Function function) {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            function.call(it.next());
            it.remove();
        }
    }

    /**
     * 链表的遍历(从tail倒序遍历)
     *
     * @param function
     */
    public void traversalFromTail(Function function) {
        Iterator<E> it = descendingIterator();
        while (it.hasNext()) {
            function.call(it.next());
            it.remove();
        }
    }

    /**
     * 双向链表的节点的执行函数
     */
    public interface Function<T> extends Serializable {
        void call(T t);
    }

}

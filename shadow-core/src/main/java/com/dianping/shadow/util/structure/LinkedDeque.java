package com.dianping.shadow.util.structure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 双向链表
 * Created by jourrey on 16/10/14.
 */
public class LinkedDeque<E> extends AbstractCollection<E> implements Deque<E>, Cloneable, Serializable {

    // 双向链表的头指针
    transient private final Node<E> head = new Node<E>();
    // 双向链表的尾指针
    transient private final Node<E> tail = new Node<E>();
    // 双向链表的长度
    transient private int size = 0;

    /**
     * Constructs an empty list.
     */
    public LinkedDeque() {
        head.next = tail;
        tail.prev = head;
    }

    /**
     * 添加节点到链表的头部
     *
     * @param e
     */
    @Override
    public void addFirst(E e) {
        Node<E> node = new Node<E>();
        node.data = e;
        addNode(head, node, head.next);
    }

    /**
     * 添加节点到链表的尾部
     *
     * @param e
     */
    @Override
    public void addLast(E e) {
        Node<E> node = new Node<E>();
        node.data = e;
        addNode(tail.prev, node, tail);
    }

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E removeFirst() {
        final E element = getFirst();
        delNode(head, head.next, head.next.next);
        return element;
    }

    @Override
    public E removeLast() {
        final E element = getFirst();
        delNode(tail.prev.prev, tail.prev, tail);
        return element;
    }

    @Override
    public E pollFirst() {
        final E element = peekFirst();
        delNode(head, head.next, head.next.next);
        return element;
    }

    @Override
    public E pollLast() {
        final E element = peekLast();
        delNode(tail.prev.prev, tail.prev, tail);
        return element;
    }

    @Override
    public E getFirst() {
        if (isTail(head.next))
            throw new NoSuchElementException();
        return head.next.data;
    }

    @Override
    public E getLast() {
        if (isHead(tail.prev))
            throw new NoSuchElementException();
        return tail.prev.data;
    }

    @Override
    public E peekFirst() {
        return null == head.next ? null : head.next.data;
    }

    @Override
    public E peekLast() {
        return null == tail.prev ? null : tail.prev.data;
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (it.next().equals(o)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        Iterator<E> it = descendingIterator();
        while (it.hasNext()) {
            if (it.next().equals(o)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        return offerLast(e);
    }

    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public Iterator<E> iterator() {
        return new IncreasingIterator();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * 链表的遍历(从head顺序遍历)
     *
     * @param nodeFunction
     */
    public void traversalFromHead(NodeFunction nodeFunction) {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            nodeFunction.call(it.next());
            it.remove();
        }
    }

    /**
     * 链表的遍历(从tail倒序遍历)
     *
     * @param nodeFunction
     */
    public void traversalFromTail(NodeFunction nodeFunction) {
        Iterator<E> it = descendingIterator();
        while (it.hasNext()) {
            nodeFunction.call(it.next());
            it.remove();
        }
    }

    /**
     * 队首
     *
     * @param node
     * @return
     */
    private boolean isHead(Node<E> node) {
        return head.equals(node);
    }

    /**
     * 队尾
     *
     * @param node
     * @return
     */
    private boolean isTail(Node<E> node) {
        return tail.equals(node);
    }

    /**
     * 插入节点
     *
     * @param prev
     * @param node
     * @param next
     */
    private void addNode(Node<E> prev, Node<E> node, Node<E> next) {
        prev.next = node;
        node.prev = prev;
        node.next = next;
        next.prev = node;
        size++;
    }

    /**
     * 删除节点
     *
     * @param prev
     * @param node
     * @param next
     */
    private void delNode(Node<E> prev, Node<E> node, Node<E> next) {
        prev.next = node.next;
        next.prev = node.prev;
        size--;
    }

    /**
     * 双向链表的节点的执行函数
     */
    public interface NodeFunction<T> extends Serializable {
        void call(T t);
    }

    /**
     * 向TAIL迭代器
     */
    private class IncreasingIterator implements Iterator<E> {
        private Node<E> current;

        public IncreasingIterator() {
            this.current = head;
        }

        @Override
        public boolean hasNext() {
            return !isTail(current.next);
        }

        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();
            current = current.next;
            return current.data;
        }

        @Override
        public void remove() {
            delNode(current.prev, current, current.next);
        }
    }

    /**
     * 向HEAD迭代器
     */
    private class DescendingIterator implements Iterator<E> {
        private Node<E> current;

        public DescendingIterator() {
            this.current = tail;
        }

        @Override
        public boolean hasNext() {
            return !isHead(current.prev);
        }

        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();
            current = current.prev;
            return current.data;
        }

        @Override
        public void remove() {
            delNode(current.prev, current, current.next);
        }
    }

    /**
     * 双向链表的节点
     */
    private class Node<E> implements Serializable {
        private static final long serialVersionUID = 2400379880648691383L;

        // 节点数据域
        private E data;
        // 节点的prev指针
        private Node<E> prev = null;
        // 节点的next指针
        private Node<E> next = null;

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(data)
                    .append(prev == null ? null : prev.data)
                    .append(next == null ? null : next.data)
                    .toHashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Node<?> node = (Node<?>) o;

            return new EqualsBuilder()
                    .append(data, node.data)
                    .append(prev == null ? null : prev.data, node.prev == null ? null : node.prev.data)
                    .append(next == null ? null : next.data, node.next == null ? null : node.next.data)
                    .isEquals();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("data", data)
                    .append("prev", prev == null ? null : prev.data)
                    .append("next", next == null ? null : next.data)
                    .toString();
        }
    }
}

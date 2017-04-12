package com.dianping.shadow.util.structure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 双向链表
 * Created by jourrey on 16/10/14.
 */
@Deprecated
public class DoublyLinkedList<T> implements Cloneable, Serializable {
    private static final long serialVersionUID = 1985226457679071328L;

    private final Node<T> HEAD = new Node<T>();
    private final Node<T> TAIL = new Node<T>();

    // 双向链表的头指针
    private Node<T> head;
    // 双向链表的尾指针
    private Node<T> tail;
    // 双向链表的长度
    private int size;

    public DoublyLinkedList() {
        head = HEAD;
        tail = TAIL;
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    /**
     * 添加节点到链表的头部
     *
     * @param t
     */
    public void addToHead(T t) {
        Node<T> node = new Node<T>();
        node.data = t;
        addNode(head, node, head.next);
    }

    /**
     * 添加节点到链表的头部
     *
     * @param list
     */
    public void addToHead(List<T> list) {
        for (T t : list) {
            addToHead(t);
        }
    }

    /**
     * 添加节点到链表的尾部
     *
     * @param t
     */
    public void addToTail(T t) {
        Node<T> node = new Node<T>();
        node.data = t;
        addNode(tail.prev, node, tail);
    }

    /**
     * 添加节点到链表的尾部
     *
     * @param list
     */
    public void addToTail(List<T> list) {
        for (T t : list) {
            addToTail(t);
        }
    }

    /**
     * 添加某个值到指定的数值的节点后面
     *
     * @param position
     * @param target
     */
    public void insertAfter(T position, T target) {
        if (null == head.next || null == tail.prev) {
            throw new NotFindNodeException("position");
        }
        Node<T> node = new Node<T>();
        node.data = target;
        Node<T> theNode = head.next;
        while (tail != theNode) {
            if (theNode.data.equals(position)) {
                addNode(theNode, node, theNode.next);
                break;
            }
            theNode = theNode.next;
        }
    }

    /**
     * 添加某个值到指定的数值的节点前面
     *
     * @param position
     * @param target
     */
    public void insertBefore(T position, T target) {
        if (null == head.next || null == tail.prev) {
            throw new NotFindNodeException("position");
        }
        Node<T> node = new Node<T>();
        node.data = target;
        Node<T> theNode = tail.prev;
        while (head != theNode) {
            if (theNode.data.equals(position)) {
                addNode(theNode.prev, node, theNode);
                break;
            }
            theNode = theNode.prev;
        }
    }

    /**
     * 获取链表的头部
     */
    public T getHead() {
        return null == head.next ? null : head.next.data;
    }

    /**
     * 获取链表的尾部
     */
    public T getTail() {
        return null == tail.prev ? null : tail.prev.data;
    }

    /**
     * 从链表的头部删除节点
     */
    public void delHead() {
        delNode(head, head.next, head.next.next);
    }

    /**
     * 从链表的尾部删除节点
     */
    public void delTail() {
        delNode(tail.prev.prev, tail.prev, tail);
    }

    /**
     * 删除链表指定的节点
     *
     * @param target
     */
    public void delOneNode(T target) {
        delCountNode(target, 1);
    }

    /**
     * 删除链表指定的节点
     *
     * @param target
     */
    public void delAllNode(T target) {
        delCountNode(target, -1);
    }

    /**
     * 删除链表指定的节点数量
     *
     * @param target
     * @param num
     */
    public void delCountNode(T target, int num) {
        if (null == head.next || null == tail.prev) {
            throw new NotFindNodeException("position");
        }
        Node<T> theNode = head.next;
        int delNum = 0;
        while (tail != theNode && (delNum < num || -1 == num)) {
            if (theNode.data.equals(target)) {
                delNode(theNode.prev, theNode, theNode.next);
                delNum++;
            }
            theNode = theNode.next;
        }
    }

    /**
     * 链表的遍历(从head顺序遍历)
     *
     * @param nodeFunction
     */
    public void traversalFromHead(NodeFunction nodeFunction) {
        if (getSize() == 0 || nodeFunction == null) {
            return;
        }
        Node<T> theNode = head.next;
        while (tail != theNode) {
            nodeFunction.call(theNode.data);
            theNode = theNode.next;
        }
    }

    /**
     * 链表的遍历(从tail倒序遍历)
     *
     * @param nodeFunction
     */
    public void traversalFromTail(NodeFunction nodeFunction) {
        if (getSize() == 0 || nodeFunction == null) {
            return;
        }
        Node<T> theNode = tail.prev;
        while (head != theNode) {
            nodeFunction.call(theNode.data);
            theNode = theNode.prev;
        }
    }

    /**
     * 转换成List
     */
    public List<T> toList() {
        if (getSize() == 0) {
            return Collections.EMPTY_LIST;
        }
        List<T> list = new ArrayList<T>(getSize());
        Node<T> theNode = head.next;
        while (tail != theNode) {
            list.add(theNode.data);
            theNode = theNode.next;
        }
        return list;
    }

    /**
     * 获取链表长度
     *
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     * 插入节点
     *
     * @param prev
     * @param node
     * @param next
     */
    private void addNode(Node<T> prev, Node<T> node, Node<T> next) {
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
    private void delNode(Node<T> prev, Node<T> node, Node<T> next) {
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
     * 双向链表的节点
     */
    private class Node<T> implements Serializable {
        private static final long serialVersionUID = 2400379880648691383L;

        // 节点数据域
        private T data;
        // 节点的prev指针
        private Node<T> prev = null;
        // 节点的next指针
        private Node<T> next = null;

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

    private static class NotFindNodeException extends RuntimeException {
        private static final long serialVersionUID = 6904862414172144338L;

        public NotFindNodeException(String message) {
            super(message);
        }
    }

    /**
     * 测试方法,不开放
     */
    private void traversalFromHead() {
        Node<T> theNode = head.next;
        while (tail != theNode) {
            System.out.println(theNode);
            theNode = theNode.next;
        }
    }

    public static void main(String[] args) {
        DoublyLinkedList<Integer> doublyLinkedList = new DoublyLinkedList<Integer>();
        doublyLinkedList.addToHead(7);
        for (int i = 0; i < 6; i++) {
            doublyLinkedList.addToHead(i);
        }
        doublyLinkedList.insertAfter(7, 8);
        doublyLinkedList.insertAfter(7, 8);
        doublyLinkedList.insertBefore(7, 8);
        doublyLinkedList.insertBefore(7, 8);
        doublyLinkedList.insertAfter(88, 11);
        doublyLinkedList.insertBefore(88, 14);
        for (int i = 10; i < 16; i++) {
            doublyLinkedList.addToHead(i);
        }
        for (int i = 10; i < 16; i++) {
            doublyLinkedList.addToHead(i);
        }
        doublyLinkedList.traversalFromHead(new NodeFunction<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println(integer);
            }
        });
        System.out.println("---------------------------");
        doublyLinkedList.traversalFromTail(new NodeFunction<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println(integer);
            }
        });
        System.out.println("---------------------------");
        doublyLinkedList.traversalFromHead();
        System.out.println(doublyLinkedList.getSize());
        System.out.println(doublyLinkedList.toList().size());
        System.out.println(doublyLinkedList.toList());
        doublyLinkedList.addToHead(doublyLinkedList.toList());
        System.out.println(doublyLinkedList.getSize());
        doublyLinkedList.addToTail(doublyLinkedList.toList());
        System.out.println(doublyLinkedList.getSize());
        doublyLinkedList.traversalFromHead();
        doublyLinkedList.delHead();
        doublyLinkedList.delTail();
        System.out.println(doublyLinkedList.getSize());
        doublyLinkedList.delOneNode(10);
        System.out.println(doublyLinkedList.getSize());
        doublyLinkedList.delCountNode(10, -1);
        System.out.println(doublyLinkedList.getSize());
        doublyLinkedList.delAllNode(10);
        System.out.println(doublyLinkedList.getSize());
        doublyLinkedList.traversalFromHead();
    }
}

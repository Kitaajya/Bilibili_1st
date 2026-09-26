package org.designer.bilibili_1st.double_link_list;

//双向链表
public class DoubleLinkedList<T>{

    static class Node<T>{
        T data;
        Node<T> prev;
        Node<T> next;
        Node(T data) {
            this.data = data;
        }
    }
    private Node<T> head;
    private Node<T> tail;
    private int size;
    //尾追加
    public void addOnTail(T data){
        Node<T> node= new Node<>(data);
        if(tail==null) {
            tail=node;
            head=tail;
        }else {
            tail.next=node;
            node.prev=tail;
            tail=node;
        }
        size++;
    }
    //头追加
    public void addOnHead(T data){
        Node<T> node=new Node<>(data);
        if(head==null){
            head=node;
            tail=head;
        }else{
            head.prev=node;
            node.next=head;
            head=node;
        }
        size++;
    }
}
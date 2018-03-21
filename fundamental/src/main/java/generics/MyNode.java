package generics;

class K {}

class Node<T> {

    public T data;

    public Node(T data) { this.data = data; }

    public void setData(T data) {
        System.out.println("Node.setData");
        this.data = data;
    }
}

public class MyNode extends Node<K> {
    public MyNode(K data) { super(data); }

    public void setData(K data) {
        System.out.println("MyNode.setData");
        super.setData(data);
    }

    public static void main(String[] args) {
        MyNode mn = new MyNode(new K());
        Node n = mn;            // A raw type - compiler throws an unchecked warning
        System.out.println(n.data);
        n.setData("Hello");
        K x = mn.data;    // Causes a ClassCastException to be thrown.
    }
}
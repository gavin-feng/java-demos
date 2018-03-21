package generics;

/**
 *
 * Because erasure removes type information in the body of a method, what matters at run time
 * is the boundaries: the points where objects enter and leave a method. These are the points
 * at which the compiler performs type checks at compile time, and inserts casting code.
 *
 * 使用 javap -c GenericHolder 比对 javap -c SimpleHolder 的输出，两者是一致的：

     public void set(java.lang.Object);
     0:    aload_0
     1:    aload_1
     2:    putfield #2; //Field obj:Object;
     5:    return
     public java.lang.Object get();
     0:    aload_0
     1:    getfield #2; //Field obj:Object;
     4:    areturn
     public static void main(java.lang.String[]);
     0:    new #3; //class SimpleHolder
     3:    dup
     4:    invokespecial #4; //Method "<init>":()V
     7:    astore_1
     8:    aload_1
     9:    ldc #5; //String Item
     11:   invokevirtual #6; //Method set:(Object;)V
     14:   aload_1
     15:   invokevirtual #7; //Method get:()Object;
     18:   checkcast #8; //class java/lang/String
     21:   astore_2
     22:   return

 * The extra work of checking the incoming type in set( ) is free, because it is performed by
 * the compiler. And the cast for the outgoing value of get( ) is still there, but it’s no less
 * than you’d have to do yourself—and it’s automatically inserted by the compiler, so the code
 * you write (and read) is less noisy.
 *
 * Since get( ) and set( ) produce the same bytecodes, all the action in generics happens at the
 * boundaries—the extra compile-time check for incoming values, and the inserted cast for outgoing
 * values. It helps to counter the confusion of erasure to remember that "the boundaries are where
 * the action takes place."
 *
 * @param <T>
 */
public class GenericHolder<T> {
  private T obj;
  public void set(T obj) { this.obj = obj; }
  public T get() { return obj; }
  public static void main(String[] args) {
    GenericHolder<String> holder =
      new GenericHolder<>();
    holder.set("Item");
    String s = holder.get();
  }
} ///:~

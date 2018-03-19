package lamda;

@FunctionalInterface
public interface MyPredicate<T> {
    boolean test(T t);
}

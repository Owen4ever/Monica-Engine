package game.engine.monica.util;

public class Type<T> {

    public Type(T k) {
        key = k;
    }

    public final T set(T k) {
        T t = key;
        key = k;
        return t;
    }

    public final T get() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;
        Type<T> t = (Type<T>) obj;
        if (key == null)
            return t.key == null;
        return key.equals(t.key);
    }

    @Override
    public int hashCode() {
        return (key == null ? -1024 : key.hashCode());
    }
    private T key;
}

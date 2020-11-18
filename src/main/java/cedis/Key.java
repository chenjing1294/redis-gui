package cedis;

import java.util.Objects;

public class Key {
    private String name;
    private Type type;

    public Key(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return Objects.equals(name, key.name) &&
                type == key.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "Key{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    public static enum Type {
        STRING, HASH, SET, LIST, ZSET, UNKNOWN
    }
}

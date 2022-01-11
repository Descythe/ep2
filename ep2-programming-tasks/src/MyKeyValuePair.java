import java.util.Objects;

public class MyKeyValuePair {
    private final Body key;
    private final ComplexCosmicSystem value;

    public MyKeyValuePair(Body aKey, ComplexCosmicSystem aValue)
    {
        key   = aKey;
        value = aValue;
    }

    public Body key()   { return key; }
    public ComplexCosmicSystem value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyKeyValuePair that = (MyKeyValuePair) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}

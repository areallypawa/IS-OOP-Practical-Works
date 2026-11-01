package product.attribute;

public class Quantity {
    private long value;

    public Quantity(long value) {
        setValue(value);
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.value = value;
    }
}

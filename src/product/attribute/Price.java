package product.attribute;

public class Price {
    private double value;

    public Price(double value) {
        setValue(value);
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.value = value;
    }
}

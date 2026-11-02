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
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException("Price must be a finite non-negative number");
        }
        this.value = value;
    }
}

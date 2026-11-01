package product.attribute;

public class Weight {
    private double value;

    public Weight(double value) {
        setValue(value);
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        this.value = value;
    }
}

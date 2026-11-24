package product.attribute;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Price {
    private double value;

    public Price(double value) {
        setValue(value);
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = round(value);
    }

    public static double multiply(double value, BigDecimal factor) {
        return round(BigDecimal.valueOf(value).multiply(factor)
            .setScale(2, RoundingMode.HALF_UP).doubleValue());
    }

    public static double round(double value) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException("Price must be a finite non-negative number");
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

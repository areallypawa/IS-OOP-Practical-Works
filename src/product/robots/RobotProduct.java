package product.robots;

import product.Product;

public abstract class RobotProduct extends Product {
    private String model;
    private int batteryCapacity;
    private int intelligenceLevel;

    protected RobotProduct(
        String name,
        double price,
        int quantity,
        double weight,

        String model,
        int batteryCapacity,
        int intelligenceLevel
    ) {
        super(name, price, quantity, weight, "Robot Product");
        if (batteryCapacity <= 0) {
            throw new IllegalArgumentException("Battery capacity must be positive");
        }
        if (intelligenceLevel < 0 || intelligenceLevel > 100) {
            throw new IllegalArgumentException("Intelligence level must be between 0 and 100");
        }

        this.model = model;
        this.batteryCapacity = batteryCapacity;
        this.intelligenceLevel = intelligenceLevel;
    }

    protected RobotProduct(RobotProduct source) {
        super(source);
        this.model = source.model;
        this.batteryCapacity = source.batteryCapacity;
        this.intelligenceLevel = source.intelligenceLevel;
    }
}

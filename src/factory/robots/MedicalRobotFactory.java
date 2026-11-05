package factory.robots;

import factory.ProductFactory;
import product.robots.MedicalRobot;

public class MedicalRobotFactory extends ProductFactory {
    private final String model;
    private final int batteryCapacity;
    private final int intelligenceLevel;
    private final int operationAccuracy;
    private final boolean sterile;
    private final int medicineCapacity;

    public MedicalRobotFactory(
        String model,
        int batteryCapacity,
        int intelligenceLevel,
        int operationAccuracy,
        boolean sterile,
        int medicineCapacity
    ) {
        this.model = model;
        this.batteryCapacity = batteryCapacity;
        this.intelligenceLevel = intelligenceLevel;
        this.operationAccuracy = operationAccuracy;
        this.sterile = sterile;
        this.medicineCapacity = medicineCapacity;
    }

    @Override
    public MedicalRobot createProduct(
        String name,
        double price,
        int quantity,
        double weight
    ) {
        return new MedicalRobot(
            name,
            price,
            quantity,
            weight,
            model,
            batteryCapacity,
            intelligenceLevel,
            operationAccuracy,
            sterile,
            medicineCapacity
        );
    }
}

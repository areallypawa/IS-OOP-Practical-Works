package factory.robots;

import factory.ProductFactory;
import product.robots.ScoutDrone;

public class ScoutDroneFactory extends ProductFactory {
    private final String model;
    private final int batteryCapacity;
    private final int intelligenceLevel;
    private final double scanRange;
    private final double movementSpeed;
    private final boolean nightVision;

    public ScoutDroneFactory(
        String model,
        int batteryCapacity,
        int intelligenceLevel,
        double scanRange,
        double movementSpeed,
        boolean nightVision
    ) {
        this.model = model;
        this.batteryCapacity = batteryCapacity;
        this.intelligenceLevel = intelligenceLevel;
        this.scanRange = scanRange;
        this.movementSpeed = movementSpeed;
        this.nightVision = nightVision;
    }

    @Override
    public ScoutDrone createProduct(
        String name,
        double price,
        int quantity,
        double weight
    ) {
        return new ScoutDrone(
            name,
            price,
            quantity,
            weight,
            model,
            batteryCapacity,
            intelligenceLevel,
            scanRange,
            movementSpeed,
            nightVision
        );
    }
}

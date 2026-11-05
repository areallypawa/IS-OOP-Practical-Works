package factory.robots;

import factory.ProductFactory;
import product.robots.GuardianRobot;

public class GuardianRobotFactory extends ProductFactory {
    private final String model;
    private final int batteryCapacity;
    private final int intelligenceLevel;
    private final int armorLevel;
    private final int weaponSlots;
    private final int securityLevel;

    public GuardianRobotFactory(
        String model,
        int batteryCapacity,
        int intelligenceLevel,
        int armorLevel,
        int weaponSlots,
        int securityLevel
    ) {
        this.model = model;
        this.batteryCapacity = batteryCapacity;
        this.intelligenceLevel = intelligenceLevel;
        this.armorLevel = armorLevel;
        this.weaponSlots = weaponSlots;
        this.securityLevel = securityLevel;
    }

    @Override
    public GuardianRobot createProduct(
        String name,
        double price,
        int quantity,
        double weight
    ) {
        return new GuardianRobot(
            name,
            price,
            quantity,
            weight,
            model,
            batteryCapacity,
            intelligenceLevel,
            armorLevel,
            weaponSlots,
            securityLevel
        );
    }
}

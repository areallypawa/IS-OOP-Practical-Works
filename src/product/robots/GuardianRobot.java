package product.robots;

public class GuardianRobot extends RobotProduct {
    private int armorLevel;
    private int weaponSlots;
    private int securityLevel;

    public GuardianRobot(
        String name,
        double price,
        int quantity,
        double weight,
        String model,
        int batteryCapacity,
        int intelligenceLevel,
        int armorLevel,
        int weaponSlots,
        int securityLevel
    ) {
        super(name, price, quantity, weight, model, batteryCapacity, intelligenceLevel);

        if (armorLevel < 0 || armorLevel > 100) {
            throw new IllegalArgumentException("Armor level must be between 0 and 100");
        }
        if (weaponSlots < 0) {
            throw new IllegalArgumentException("Weapon slots cannot be negative");
        }
        if (securityLevel < 0 || securityLevel > 100) {
            throw new IllegalArgumentException("Security level must be between 0 and 100");
        }

        this.armorLevel = armorLevel;
        this.weaponSlots = weaponSlots;
        this.securityLevel = securityLevel;
    }

    private GuardianRobot(GuardianRobot source) {
        super(source);
        this.armorLevel = source.armorLevel;
        this.weaponSlots = source.weaponSlots;
        this.securityLevel = source.securityLevel;
    }

    @Override
    public GuardianRobot clone() {
        return new GuardianRobot(this);
    }
}

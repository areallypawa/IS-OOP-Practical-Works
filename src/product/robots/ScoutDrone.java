package product.robots;

public class ScoutDrone extends RobotProduct {
    private double scanRange;
    private double movementSpeed;
    private boolean nightVision;

    public ScoutDrone(
        String name,
        double price,
        int quantity,
        double weight,
        String model,
        int batteryCapacity,
        int intelligenceLevel,
        double scanRange,
        double movementSpeed,
        boolean nightVision
    ) {
        super(name, price, quantity, weight, model, batteryCapacity, intelligenceLevel);

        if (!Double.isFinite(scanRange) || scanRange <= 0) {
            throw new IllegalArgumentException("Scan range must be finite and positive");
        }
        if (!Double.isFinite(movementSpeed) || movementSpeed <= 0) {
            throw new IllegalArgumentException("Movement speed must be finite and positive");
        }

        this.scanRange = scanRange;
        this.movementSpeed = movementSpeed;
        this.nightVision = nightVision;
    }

    private ScoutDrone(ScoutDrone source) {
        super(source);
        this.scanRange = source.scanRange;
        this.movementSpeed = source.movementSpeed;
        this.nightVision = source.nightVision;
    }

    @Override
    public String getDetails() {
        return super.getDetails()
            + "\nScan range: " + scanRange + " m"
            + "\nMovement speed: " + movementSpeed + " km/h"
            + "\nNight vision: " + nightVision;
    }

    @Override
    public ScoutDrone clone() {
        return new ScoutDrone(this);
    }
}

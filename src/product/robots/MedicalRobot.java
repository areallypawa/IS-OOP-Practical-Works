package product.robots;

public class MedicalRobot extends RobotProduct {
    private int operationAccuracy;
    private boolean sterile;
    private int medicineCapacity;

    public MedicalRobot(
        String name,
        double price,
        int quantity,
        double weight,
        String model,
        int batteryCapacity,
        int intelligenceLevel,
        int operationAccuracy,
        boolean sterile,
        int medicineCapacity
    ) {
        super(name, price, quantity, weight, model, batteryCapacity, intelligenceLevel);

        if (operationAccuracy < 0 || operationAccuracy > 100) {
            throw new IllegalArgumentException("Operation accuracy must be between 0 and 100");
        }
        if (medicineCapacity < 0) {
            throw new IllegalArgumentException("Medicine capacity cannot be negative");
        }

        this.operationAccuracy = operationAccuracy;
        this.sterile = sterile;
        this.medicineCapacity = medicineCapacity;
    }

    private MedicalRobot(MedicalRobot source) {
        super(source);
        this.operationAccuracy = source.operationAccuracy;
        this.sterile = source.sterile;
        this.medicineCapacity = source.medicineCapacity;
    }

    @Override
    public String getDetails() {
        return super.getDetails()
            + "\nOperation accuracy: " + operationAccuracy + "%"
            + "\nSterile: " + sterile
            + "\nMedicine capacity: " + medicineCapacity + " units";
    }

    @Override
    public MedicalRobot clone() {
        return new MedicalRobot(this);
    }
}

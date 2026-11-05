package product.artifact;

public class ChronoBattery extends MagicalArtifact {
    private int energyCapacity;
    private String compatibleModel;
    private int rechargeRate;

    public ChronoBattery(
        String name,
        double price,
        int quantity,
        double weight,
        Rarity rarity,
        boolean isCursed,
        String ownerName,
        int magicPower,
        int energyCapacity,
        String compatibleModel,
        int rechargeRate
    ) {
        super(name, price, quantity, weight, rarity, isCursed, ownerName, magicPower);

        if (energyCapacity <= 0) {
            throw new IllegalArgumentException("Energy capacity must be positive");
        }
        if (compatibleModel == null || compatibleModel.isBlank()) {
            throw new IllegalArgumentException("Compatible model cannot be empty");
        }
        if (rechargeRate <= 0) {
            throw new IllegalArgumentException("Recharge rate must be positive");
        }

        this.energyCapacity = energyCapacity;
        this.compatibleModel = compatibleModel;
        this.rechargeRate = rechargeRate;
    }

    private ChronoBattery(ChronoBattery source) {
        super(source);
        this.energyCapacity = source.energyCapacity;
        this.compatibleModel = source.compatibleModel;
        this.rechargeRate = source.rechargeRate;
    }

    @Override
    public String getDetails() {
        return super.getDetails()
            + "\nEnergy capacity: " + energyCapacity + " mAh"
            + "\nCompatible model: " + compatibleModel
            + "\nRecharge rate: " + rechargeRate + " mAh/min";
    }

    @Override
    public ChronoBattery clone() {
        return new ChronoBattery(this);
    }

    


    
}

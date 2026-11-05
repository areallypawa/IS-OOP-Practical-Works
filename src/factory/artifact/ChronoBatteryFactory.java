package factory.artifact;

import factory.ProductFactory;
import product.artifact.ChronoBattery;
import product.artifact.Rarity;

public class ChronoBatteryFactory extends ProductFactory {
    private final Rarity rarity;
    private final boolean isCursed;
    private final String ownerName;
    private final int magicPower;
    private final int energyCapacity;
    private final String compatibleModel;
    private final int rechargeRate;

    public ChronoBatteryFactory(
        Rarity rarity,
        boolean isCursed,
        String ownerName,
        int magicPower,
        int energyCapacity,
        String compatibleModel,
        int rechargeRate
    ) {
        this.rarity = rarity;
        this.isCursed = isCursed;
        this.ownerName = ownerName;
        this.magicPower = magicPower;
        this.energyCapacity = energyCapacity;
        this.compatibleModel = compatibleModel;
        this.rechargeRate = rechargeRate;
    }

    @Override
    public ChronoBattery createProduct(
        String name,
        double price,
        int quantity,
        double weight
    ) {
        return new ChronoBattery(
            name,
            price,
            quantity,
            weight,
            rarity,
            isCursed,
            ownerName,
            magicPower,
            energyCapacity,
            compatibleModel,
            rechargeRate
        );
    }
}

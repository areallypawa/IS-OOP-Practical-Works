package factory.artifact;

import factory.ProductFactory;
import product.artifact.QuantumCore;
import product.artifact.Rarity;

public class QuantumCoreFactory extends ProductFactory {
    private final Rarity rarity;
    private final boolean isCursed;
    private final String ownerName;
    private final int magicPower;
    private final int computingPower;
    private final String compatibleModel;
    private final int stability;

    public QuantumCoreFactory(
        Rarity rarity,
        boolean isCursed,
        String ownerName,
        int magicPower,
        int computingPower,
        String compatibleModel,
        int stability
    ) {
        this.rarity = rarity;
        this.isCursed = isCursed;
        this.ownerName = ownerName;
        this.magicPower = magicPower;
        this.computingPower = computingPower;
        this.compatibleModel = compatibleModel;
        this.stability = stability;
    }

    @Override
    public QuantumCore createProduct(
        String name,
        double price,
        int quantity,
        double weight
    ) {
        return new QuantumCore(
            name,
            price,
            quantity,
            weight,
            rarity,
            isCursed,
            ownerName,
            magicPower,
            computingPower,
            compatibleModel,
            stability
        );
    }
}

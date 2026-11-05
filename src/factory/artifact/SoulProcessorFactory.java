package factory.artifact;

import factory.ProductFactory;
import product.artifact.Rarity;
import product.artifact.SoulProcessor;

public class SoulProcessorFactory extends ProductFactory {
    private final Rarity rarity;
    private final boolean isCursed;
    private final String ownerName;
    private final int magicPower;
    private final String soulName;
    private final int emotionLevel;
    private final int controlLevel;

    public SoulProcessorFactory(
        Rarity rarity,
        boolean isCursed,
        String ownerName,
        int magicPower,
        String soulName,
        int emotionLevel,
        int controlLevel
    ) {
        this.rarity = rarity;
        this.isCursed = isCursed;
        this.ownerName = ownerName;
        this.magicPower = magicPower;
        this.soulName = soulName;
        this.emotionLevel = emotionLevel;
        this.controlLevel = controlLevel;
    }

    @Override
    public SoulProcessor createProduct(
        String name,
        double price,
        int quantity,
        double weight
    ) {
        return new SoulProcessor(
            name,
            price,
            quantity,
            weight,
            rarity,
            isCursed,
            ownerName,
            magicPower,
            soulName,
            emotionLevel,
            controlLevel
        );
    }
}

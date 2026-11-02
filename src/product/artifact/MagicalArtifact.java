package product.artifact;

import product.Product;

public abstract class MagicalArtifact extends Product {
    private Rarity rarity;
    private boolean isCursed;
    private String ownerName;
    private int magicPower;

    protected MagicalArtifact(
        String name,
        double price,
        int quantity,
        double weight,

        Rarity rarity,
        boolean isCursed,
        String ownerName,
        int magicPower
    ) {
        super(name, price, quantity, weight, "Magical artifact");

        if (rarity == null) {
            throw new IllegalArgumentException("Rarity cannot be null");
        }
        if (magicPower < 0 || magicPower > 100) {
            throw new IllegalArgumentException("Magic power must be between 0 and 100");
        }

        this.rarity = rarity;
        this.isCursed = isCursed;
        this.ownerName = ownerName;
        this.magicPower = magicPower;
    }

    protected MagicalArtifact(MagicalArtifact source) {
        super(source);
        this.rarity = source.rarity;
        this.isCursed = source.isCursed;
        this.ownerName = source.ownerName;
        this.magicPower = source.magicPower;
    }
}

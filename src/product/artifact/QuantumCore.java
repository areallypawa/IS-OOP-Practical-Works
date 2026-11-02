package product.artifact;

public class QuantumCore extends MagicalArtifact {
    private int computingPower;
    private String compatibleModel;
    private int stability;

    public QuantumCore(
        String name,
        double price,
        int quantity,
        double weight,
        Rarity rarity,
        boolean isCursed,
        String ownerName,
        int magicPower,
        int computingPower,
        String compatibleModel,
        int stability
    ) {
        super(name, price, quantity, weight, rarity, isCursed, ownerName, magicPower);

        if (computingPower <= 0) {
            throw new IllegalArgumentException("Computing power must be positive");
        }
        if (compatibleModel == null || compatibleModel.isBlank()) {
            throw new IllegalArgumentException("Compatible model cannot be empty");
        }
        if (stability < 0 || stability > 100) {
            throw new IllegalArgumentException("Stability must be between 0 and 100");
        }

        this.computingPower = computingPower;
        this.compatibleModel = compatibleModel;
        this.stability = stability;
    }

    private QuantumCore(QuantumCore source) {
        super(source);
        this.computingPower = source.computingPower;
        this.compatibleModel = source.compatibleModel;
        this.stability = source.stability;
    }

    @Override
    public QuantumCore clone() {
        return new QuantumCore(this);
    }
}

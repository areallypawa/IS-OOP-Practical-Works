package product.artifact;

public class SoulProcessor extends MagicalArtifact {
    private String soulName;
    private int emotionLevel;
    private int controlLevel;

    public SoulProcessor(
        String name,
        double price,
        long quantity,
        double weight,
        Rarity rarity,
        boolean isCursed,
        String ownerName,
        int magicPower,
        String soulName,
        int emotionLevel,
        int controlLevel
    ) {
        super(name, price, quantity, weight, rarity, isCursed, ownerName, magicPower);

        if (soulName == null || soulName.isBlank()) {
            throw new IllegalArgumentException("Soul name cannot be empty");
        }
        if (emotionLevel < 0 || emotionLevel > 100) {
            throw new IllegalArgumentException("Emotion level must be between 0 and 100");
        }
        if (controlLevel < 0 || controlLevel > 100) {
            throw new IllegalArgumentException("Control level must be between 0 and 100");
        }

        this.soulName = soulName;
        this.emotionLevel = emotionLevel;
        this.controlLevel = controlLevel;
    }

    private SoulProcessor(SoulProcessor source) {
        super(source);
        this.soulName = source.soulName;
        this.emotionLevel = source.emotionLevel;
        this.controlLevel = source.controlLevel;
    }

    @Override
    public SoulProcessor clone() {
        return new SoulProcessor(this);
    }
}

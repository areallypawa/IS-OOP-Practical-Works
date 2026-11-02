package product.artifact;
import product.Product;

public class MagicalArtifact extends Product {
    private String rarity;
    private boolean isCursed;
    private String ownerName;
    private int magicPower;

    protected Artifacts(
        String name,
        double price,
        long quantity,
        double weight,

        String rarity,
        boolean isCursed,
        String ownerName,
        int magicPower
    ) {
         super(name, price, quantity, weight);
         // проверка на рарити из [common, lenegdary ...]
         this.rarity = rarity;
         this.isCursed = isCursed;
         this.ownerName = ownerName;
         // проверка на мощность от 0 до 100
         this.magicPower = magicPower;
    }
}

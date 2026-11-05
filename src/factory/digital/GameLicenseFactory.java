package factory.digital;

import factory.ProductFactory;
import product.digital.GameLicense;

public class GameLicenseFactory extends ProductFactory {
    private final String licenseKey;
    private final int fileSize;
    private final String downloadUrl;
    private final String platform;
    private final int minimumAge;
    private final boolean multiplayer;

    public GameLicenseFactory(
        String licenseKey,
        int fileSize,
        String downloadUrl,
        String platform,
        int minimumAge,
        boolean multiplayer
    ) {
        this.licenseKey = licenseKey;
        this.fileSize = fileSize;
        this.downloadUrl = downloadUrl;
        this.platform = platform;
        this.minimumAge = minimumAge;
        this.multiplayer = multiplayer;
    }

    @Override
    public GameLicense createProduct(
        String name,
        double price,
        int quantity,
        double weight
    ) {
        return new GameLicense(
            name,
            price,
            quantity,
            weight,
            licenseKey,
            fileSize,
            downloadUrl,
            platform,
            minimumAge,
            multiplayer
        );
    }
}

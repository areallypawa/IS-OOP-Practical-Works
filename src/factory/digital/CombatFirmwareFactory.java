package factory.digital;

import factory.ProductFactory;
import product.digital.CombatFirmware;

public class CombatFirmwareFactory extends ProductFactory {
    private final String licenseKey;
    private final int fileSize;
    private final String downloadUrl;
    private final int supportedWeapons;
    private final int aimAccuracy;
    private final String version;

    public CombatFirmwareFactory(
        String licenseKey,
        int fileSize,
        String downloadUrl,
        int supportedWeapons,
        int aimAccuracy,
        String version
    ) {
        this.licenseKey = licenseKey;
        this.fileSize = fileSize;
        this.downloadUrl = downloadUrl;
        this.supportedWeapons = supportedWeapons;
        this.aimAccuracy = aimAccuracy;
        this.version = version;
    }

    @Override
    public CombatFirmware createProduct(
        String name,
        double price,
        int quantity,
        double weight
    ) {
        return new CombatFirmware(
            name,
            price,
            quantity,
            weight,
            licenseKey,
            fileSize,
            downloadUrl,
            supportedWeapons,
            aimAccuracy,
            version
        );
    }
}

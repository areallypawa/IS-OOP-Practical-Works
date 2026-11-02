package product.digital;

public class CombatFirmware extends DigitalProduct {
    private int supportedWeapons;
    private int aimAccuracy;
    private String version;

    public CombatFirmware(
        String name,
        double price,
        long quantity,
        double weight,
        String licenseKey,
        int fileSize,
        String downloadUrl,
        int supportedWeapons,
        int aimAccuracy,
        String version
    ) {
        super(name, price, quantity, weight, licenseKey, fileSize, downloadUrl);

        if (supportedWeapons < 0) {
            throw new IllegalArgumentException("Supported weapons cannot be negative");
        }
        if (aimAccuracy < 0 || aimAccuracy > 100) {
            throw new IllegalArgumentException("Aim accuracy must be between 0 and 100");
        }
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Version cannot be empty");
        }

        this.supportedWeapons = supportedWeapons;
        this.aimAccuracy = aimAccuracy;
        this.version = version;
    }

    private CombatFirmware(CombatFirmware source) {
        super(source);
        this.supportedWeapons = source.supportedWeapons;
        this.aimAccuracy = source.aimAccuracy;
        this.version = source.version;
    }

    @Override
    public CombatFirmware clone() {
        return new CombatFirmware(this);
    }
}

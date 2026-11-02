package product.digital;

public class GameLicense extends DigitalProduct {
    private String platform;
    private int minimumAge;
    private boolean multiplayer;

    public GameLicense(
        String name,
        double price,
        long quantity,
        double weight,
        String licenseKey,
        int fileSize,
        String downloadUrl,
        String platform,
        int minimumAge,
        boolean multiplayer
    ) {
        super(name, price, quantity, weight, licenseKey, fileSize, downloadUrl);

        if (platform == null || platform.isBlank()) {
            throw new IllegalArgumentException("Platform cannot be empty");
        }
        if (minimumAge < 0 || minimumAge > 18) {
            throw new IllegalArgumentException("Minimum age must be between 0 and 18");
        }

        this.platform = platform;
        this.minimumAge = minimumAge;
        this.multiplayer = multiplayer;
    }

    private GameLicense(GameLicense source) {
        super(source);
        this.platform = source.platform;
        this.minimumAge = source.minimumAge;
        this.multiplayer = source.multiplayer;
    }

    @Override
    public GameLicense clone() {
        return new GameLicense(this);
    }
}

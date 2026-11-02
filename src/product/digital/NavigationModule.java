package product.digital;

public class NavigationModule extends DigitalProduct {
    private int mapCount;
    private double maximumRouteDistance;
    private boolean supportsUnknownAreas;

    public NavigationModule(
        String name,
        double price,
        long quantity,
        double weight,
        String licenseKey,
        int fileSize,
        String downloadUrl,
        int mapCount,
        double maximumRouteDistance,
        boolean supportsUnknownAreas
    ) {
        super(name, price, quantity, weight, licenseKey, fileSize, downloadUrl);

        if (mapCount < 0) {
            throw new IllegalArgumentException("Map count cannot be negative");
        }
        if (!Double.isFinite(maximumRouteDistance) || maximumRouteDistance <= 0) {
            throw new IllegalArgumentException("Maximum route distance must be finite and positive");
        }

        this.mapCount = mapCount;
        this.maximumRouteDistance = maximumRouteDistance;
        this.supportsUnknownAreas = supportsUnknownAreas;
    }

    private NavigationModule(NavigationModule source) {
        super(source);
        this.mapCount = source.mapCount;
        this.maximumRouteDistance = source.maximumRouteDistance;
        this.supportsUnknownAreas = source.supportsUnknownAreas;
    }

    @Override
    public NavigationModule clone() {
        return new NavigationModule(this);
    }
}

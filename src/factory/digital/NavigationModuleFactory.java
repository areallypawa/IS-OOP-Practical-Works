package factory.digital;

import factory.ProductFactory;
import product.digital.NavigationModule;

public class NavigationModuleFactory extends ProductFactory {
    private final String licenseKey;
    private final int fileSize;
    private final String downloadUrl;
    private final int mapCount;
    private final double maximumRouteDistance;
    private final boolean supportsUnknownAreas;

    public NavigationModuleFactory(
        String licenseKey,
        int fileSize,
        String downloadUrl,
        int mapCount,
        double maximumRouteDistance,
        boolean supportsUnknownAreas
    ) {
        this.licenseKey = licenseKey;
        this.fileSize = fileSize;
        this.downloadUrl = downloadUrl;
        this.mapCount = mapCount;
        this.maximumRouteDistance = maximumRouteDistance;
        this.supportsUnknownAreas = supportsUnknownAreas;
    }

    @Override
    public NavigationModule createProduct(
        String name,
        double price,
        int quantity,
        double weight
    ) {
        return new NavigationModule(
            name,
            price,
            quantity,
            weight,
            licenseKey,
            fileSize,
            downloadUrl,
            mapCount,
            maximumRouteDistance,
            supportsUnknownAreas
        );
    }
}

package product.digital;

import product.Product;

public abstract class DigitalProduct extends Product {
    private String licenseKey;
    private int fileSize;
    private String downloadUrl;

    protected DigitalProduct(
        String name,
        double price,
        long quantity,
        double weight,

        String licenseKey,
        int fileSize,
        String downloadUrl
    ) {
        super(name, price, quantity, weight, "Digital product");
        if (licenseKey == null || licenseKey.isBlank()) {
            throw new IllegalArgumentException("License key cannot be empty");
        }
        if (fileSize <= 0 || fileSize > 1000) {
            throw new IllegalArgumentException("File size must be between 1 and 1000");
        }
        if (downloadUrl == null || downloadUrl.isBlank()) {
            throw new IllegalArgumentException("Download URL cannot be empty");
        }

        this.licenseKey = licenseKey;
        this.fileSize = fileSize;
        this.downloadUrl = downloadUrl;
    }

    protected DigitalProduct(DigitalProduct source) {
        super(source);
        this.licenseKey = source.licenseKey;
        this.fileSize = source.fileSize;
        this.downloadUrl = source.downloadUrl;
    }
}

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
        if (fileSize <= 0 || fileSize > 1000) {
            throw new IllegalArgumentException("File size must be between 1 and 1000");
        }
        this.licenseKey = licenseKey;
        this.fileSize = fileSize;
        this.downloadUrl = downloadUrl;

    }
}

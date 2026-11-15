package product;

import java.util.HashMap;
import java.util.Map;

public final class ProductType {
    private static final Map<String, ProductType> TYPES = new HashMap<>();

    private final String category;
    private final double baseWeight;
    private final double basePrice;

    private ProductType(String category, double baseWeight, double basePrice) {
        this.category = normalizeCategory(category);
        validateNonNegativeFinite(baseWeight, "Base weight");
        validateNonNegativeFinite(basePrice, "Base price");

        this.baseWeight = baseWeight;
        this.basePrice = basePrice;
    }

    public static ProductType getType(
        String category,
        double baseWeight,
        double basePrice
    ) {
        category = normalizeCategory(category);
        validateNonNegativeFinite(baseWeight, "Base weight");
        validateNonNegativeFinite(basePrice, "Base price");

        ProductType existingType = TYPES.get(category);
        if (existingType != null) {
            return existingType;
        }
        ProductType newProduct = new ProductType(category, baseWeight, basePrice);
        TYPES.put(category, newProduct);
        return newProduct;
    }

    public String getCategory() {
        return category;
    }

    public double getBaseWeight() {
        return baseWeight;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public static int getTypeCount() {
        return TYPES.size();
    }

    private static String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }
        return category.trim();
    }

    private static void validateNonNegativeFinite(double value, String fieldName) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(
                fieldName + " must be finite and non-negative"
            );
        }
    }
}

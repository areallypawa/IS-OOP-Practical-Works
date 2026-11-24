package order;

import java.math.BigDecimal;
import product.Product;
import product.attribute.Price;

public class OrderItem {
    private final Product sourceProduct;
    private final long productId;
    private final String productName;
    private final double unitPrice;
    private final double unitWeight;
    private final int quantity;

    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > product.getQuantity()) {
             throw new IllegalArgumentException("Not enough product in stock");
        }

        this.sourceProduct = product;
        this.productId = product.getId();
        this.productName = product.getName();
        this.unitPrice = product.getPrice();
        this.unitWeight = product.getWeight();
        this.quantity = quantity;
    }

    public Product getSourceProduct() {
        return sourceProduct;
    }

    public long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getUnitWeight() {
        return unitWeight;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return Price.multiply(unitPrice, BigDecimal.valueOf(quantity));
    }

    public double getTotalWeight() {
        return unitWeight * quantity;
    }
}

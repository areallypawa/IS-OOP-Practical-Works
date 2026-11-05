package factory;

import product.Product;

public abstract class ProductFactory {
    public abstract Product createProduct(
        String name,
        double price,
        int quantity,
        double weight
    );
}

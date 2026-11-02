package product;

import product.attribute.Price;
import product.attribute.Quantity;
import product.attribute.Weight;

public abstract class Product {
    private static long nextId = 1;

    private final long id;
    private String name;
    private Price price;
    private Quantity quantity;
    private Weight weight;
    private String category;

    protected Product(String name, double price, long quantity, double weight, String category) {
        this.id = nextId++;
        this.name = name;
        this.price = new Price(price);
        this.quantity = new Quantity(quantity);
        this.weight = new Weight(weight);
        this.category = category;
    }

    protected Product(Product source) {
        this(
            source.name,
            source.price.getValue(),
            source.quantity.getValue(),
            source.weight.getValue(),
            source.category
        );
    }

    public abstract Product clone();
}

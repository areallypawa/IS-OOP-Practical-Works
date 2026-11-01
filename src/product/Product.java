package product;

import product.attribute.Price;
import product.attribute.Quantity;
import product.attribute.Weight;

public abstract class Product {
    private long id = 1;
    private static long nextId = 2;
    private String name;
    private Price price;
    private Quantity quantity;
    private Weight weight;
    private String category;

    protected Product(String name, double price, long quantity, double weight, String category) {
        this.name = name;
        this.price = new Price(price);
        this.quantity = new Quantity(quantity);
        this.weight = new Weight(weight);
        this.category = category;
        this.nextId++;
    }

    public abstract Product clone();
}

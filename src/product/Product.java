package product;

import inventory.Warehouse;
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
    private Warehouse warehouse;

    protected Product(String name, double price, int quantity, double weight, String category) {
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

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price.getValue();
    }

    public int getQuantity() {
        return quantity.getValue();
    }

    public double getWeight() {
        return weight.getValue();
    }

    public String getCategory() {
        return category;
    }

    public void changeQuantity(int delta) {
        int newQuantity;

        try {
            newQuantity = Math.addExact(quantity.getValue(), delta);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Quantity is out of integer range", exception);
        }

        quantity.setValue(newQuantity);
    }

    public void setPrice(Price newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }

        this.price = new Price(newPrice.getValue());
    }

    public final void transferTo(Warehouse other) {
        if (other == null) {
            throw new IllegalArgumentException("Target warehouse cannot be null");
        }
        if (warehouse == null) {
            throw new IllegalStateException("Product is not stored in a warehouse");
        }

        warehouse.transferProductTo(this, other);
    }

    public final Warehouse getWarehouse() {
        return warehouse;
    }

    public final void attachToWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse cannot be null");
        }
        if (this.warehouse != null && this.warehouse != warehouse) {
            throw new IllegalStateException("Product is already stored in another warehouse");
        }

        this.warehouse = warehouse;
    }

    public final void detachFromWarehouse(Warehouse warehouse) {
        if (this.warehouse != warehouse) {
            throw new IllegalStateException("Product does not belong to this warehouse");
        }

        this.warehouse = null;
    }

    public abstract Product clone();
}

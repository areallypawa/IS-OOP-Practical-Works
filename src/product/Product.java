package product;

import inventory.Warehouse;
import product.attribute.Price;
import product.attribute.Quantity;

public abstract class Product {
    private static long nextId = 1;

    private final long id;
    private String name;
    private Price price;
    private Quantity quantity;
    private final ProductType type;
    private Warehouse warehouse;

    protected Product(String name, double price, int quantity, double weight, String category) {
        this(
            name,
            price,
            quantity,
            ProductType.getType(category, weight, price)
        );
    }

    private Product(String name, double price, int quantity, ProductType type) {
        if (type == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }

        this.id = nextId++;
        this.name = name;
        this.price = new Price(price);
        this.quantity = new Quantity(quantity);
        this.type = type;
    }

    protected Product(Product other) {
        this(
            other.name,
            other.price.getValue(),
            other.quantity.getValue(),
            other.type
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
        return type.getBaseWeight();
    }

    public String getCategory() {
        return type.getCategory();
    }

    public ProductType getType() {
        return type;
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

    public String getDetails() {
        return "ID: " + id
            + "\nName: " + name
            + "\nCategory: " + type.getCategory()
            + "\nPrice: $" + price.getValue()
            + "\nQuantity: " + quantity.getValue()
            + "\nWeight: " + type.getBaseWeight() + " kg"
            + "\nType: " + getClass().getSimpleName();
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

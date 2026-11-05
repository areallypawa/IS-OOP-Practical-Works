package inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import product.Product;

public class Warehouse implements Iterable<Product> {
    private final List<Product> products;
    private final int maxSize;

    public Warehouse(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Maximum size must be positive");
        }
        this.maxSize = maxSize;
        products = new ArrayList<>(maxSize);
    }

    public Warehouse(Warehouse source) {
        this(source.maxSize);
        for (Product product : source.products) {
            addProduct(product.clone());
        }

    }

    public void transferFrom(Warehouse source) {
        if (source == null) {
               throw new IllegalArgumentException("Source warehouse cannot be null");
        }
        if (source == this) {
            return;
        }
        if (products.size() + source.products.size() > maxSize) {
                throw new IllegalStateException("Not enough space in target warehouse");
        }

        List<Product> productsToTransfer = new ArrayList<>(source.products);

        for (Product product: productsToTransfer) {
            product.transferTo(this);
        }
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (products.size() >= maxSize) {
            throw new IllegalStateException("Warehouse is full");
        }
        if (product.getWarehouse() != null) {
            throw new IllegalStateException("Product is already stored in a warehouse");
        }

        products.add(product);
        product.attachToWarehouse(this);
    }

    public boolean removeById(long id) {
        Iterator<Product> iterator = products.iterator();

        while (iterator.hasNext()) {
            Product product = iterator.next();

            if (product.getId() == id) {
                iterator.remove();
                product.detachFromWarehouse(this);
                return true;
            }
        }

        return false;
    }

    public Product removeByIndex(int idx) {
        if (idx < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }
        if (idx >= products.size()) {
            throw new IllegalArgumentException("Index out of range");
        }
        Product removedProduct = products.remove(idx);
        removedProduct.detachFromWarehouse(this);
        return removedProduct;
    }

    public Product findById(long id) {
        for (Product product: products) {
            if (product.getId() == id) {
                return product;
            }
        }

        return null;
    }

    public List<Product> findByCategory(String category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        List<Product> foundProducts = new ArrayList<>();

        for (Product product: products) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                foundProducts.add(product);
            }
        }

        return foundProducts;
    }

    public List<Product> findByPrice(double price) {
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Price must be a finite non-negative number");
        }

        List<Product> foundProducts = new ArrayList<>();

        for (Product product : products) {
            if (Double.compare(product.getPrice(), price) == 0) {
                foundProducts.add(product);
            }
        }

        return foundProducts;
    }

    public void transferProductTo(Product product, Warehouse other) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (other == null) {
            throw new IllegalArgumentException("Target warehouse cannot be null");
        }
        if (other == this) {
            return;
        }
        if (product.getWarehouse() != this || !products.contains(product)) {
            throw new IllegalArgumentException("Product does not belong to this warehouse");
        }
        if (other.products.size() >= other.maxSize) {
            throw new IllegalStateException("Target warehouse is full");
        }

        products.remove(product);
        product.detachFromWarehouse(this);

        try {
            other.addProduct(product);
        } catch (RuntimeException exception) {
            products.add(product);
            product.attachToWarehouse(this);
            throw exception;
        }
    }

    public int size() {
        return products.size();
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public Iterator<Product> iterator() {
        return products.iterator();
    }

    public Product getFirst() {
        if (products.isEmpty()) {
            return null;
        }
        return products.get(0);
    }

    public Product getLast() {
        if (products.isEmpty()) {
            return null;
        }
        return products.get(products.size() - 1);
    }

}

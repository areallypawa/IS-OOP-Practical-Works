package shop;

import customer.Customer;
import delivery.CourierDelivery;
import delivery.Delivery;
import delivery.PickupDelivery;
import delivery.PostDelivery;
import demo.DemoData;
import discount.CashbackDiscount;
import discount.Discount;
import discount.FreeDeliveryDiscount;
import discount.GiftDiscount;
import discount.PercentDiscount;
import factory.ProductFactory;
import inventory.Warehouse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import order.Order;
import order.OrderItem;
import order.OrderManager;
import order.OrderMediator;
import order.OrderMediator.Line;
import product.Product;
import product.attribute.Price;

public final class ShopFacade {
    private final Map<String, Warehouse> warehouses = new LinkedHashMap<>();
    private final Map<Long, Customer> customers = new LinkedHashMap<>();
    private final OrderManager manager;
    private final OrderMediator mediator;

    public ShopFacade(int activeOrderLimit) {
        manager = new OrderManager(activeOrderLimit);
        mediator = new OrderMediator(manager);
    }

    public static ShopFacade demo() {
        ShopFacade shop = new ShopFacade(5);
        shop.createWarehouse("main", 100);
        shop.warehouses.put("demo", DemoData.createDemoWarehouse());
        shop.addCustomer("Demo customer", "Central");
        return shop;
    }

    public <R> R execute(ShopCommand<R> command) {
        return command.execute(this);
    }

    public String createWarehouse(String name, int capacity) {
        String key = newWarehouseName(name);
        warehouses.put(key, new Warehouse(capacity));
        return key;
    }

    public String copyWarehouse(String source, String name) {
        String key = newWarehouseName(name);
        warehouses.put(key, new Warehouse(warehouse(source)));
        return key;
    }

    public List<String> warehouseNames() {
        return List.copyOf(warehouses.keySet());
    }

    public WarehouseInfo warehouseInfo(String name) {
        Warehouse warehouse = warehouse(name);
        return new WarehouseInfo(warehouse.size(), warehouse.getMaxSize());
    }

    public Product addProduct(
        String warehouse, ProductFactory factory, String name, Price price, int quantity, double weight
    ) {
        Warehouse target = warehouse(warehouse);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        Product product = factory.createProduct(name, price.getValue(), quantity, weight);
        target.addProduct(product);
        return product;
    }

    public void changePrice(String warehouse, long productId, Price price) {
        product(warehouse, productId).setPrice(price);
    }

    public void changeStock(String warehouse, long productId, int delta) {
        mediator.changeStock(product(warehouse, productId), delta);
    }

    public void removeProduct(String warehouse, long productId) {
        Product product = product(warehouse, productId);
        mediator.checkCanMoveOrRemove(product);
        product.getWarehouse().removeById(productId);
    }

    public void transferProduct(String source, String target, long productId) {
        Product product = product(source, productId);
        Warehouse destination = warehouse(target);
        if (product.getWarehouse() != destination) {
            mediator.checkCanMoveOrRemove(product);
            product.transferTo(destination);
        }
    }

    public void transferAll(String source, String target) {
        Warehouse from = warehouse(source);
        Warehouse to = warehouse(target);
        if (from != to) {
            from.forEach(mediator::checkCanMoveOrRemove);
            to.transferFrom(from);
        }
    }

    public List<Product> products(String name) {
        List<Product> result = new ArrayList<>();
        warehouse(name).forEach(result::add);
        return List.copyOf(result);
    }

    public Product product(String name, long id) {
        Product product = warehouse(name).findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + id + " in " + name);
        }
        return product;
    }

    public List<Product> productsByCategory(String name, String category) {
        return warehouse(name).findByCategory(category);
    }

    public List<Product> productsByPrice(String name, Price price) {
        return warehouse(name).findByPrice(price.getValue());
    }

    public Customer addCustomer(String name, String region) {
        Customer customer = new Customer(name, region);
        customers.put(customer.getId(), customer);
        return customer;
    }

    public List<Customer> customers() {
        return List.copyOf(customers.values());
    }

    public Order createOrder(
        String warehouse, long customerId, List<Line> lines,
        DeliveryType delivery, Promotion promotion
    ) {
        return mediator.createOrder(
            warehouse(warehouse), customer(customerId), lines,
            delivery == null ? null : delivery.create(), discount(warehouse, promotion)
        );
    }

    public void confirmPayment(long id) { mediator.confirmPayment(id); }
    public void ship(long id) { mediator.ship(id); }
    public void confirmDelivery(long id) { mediator.confirmDelivery(id); }
    public void cancel(long id) { mediator.cancel(id); }

    public void addItem(String warehouse, long orderId, Line line) {
        mediator.addItem(warehouse(warehouse), orderId, line);
    }

    public void removeItem(long orderId, OrderItem item) {
        mediator.removeItem(orderId, item);
    }

    public void assignDelivery(long id, DeliveryType delivery) {
        mediator.assignDelivery(id, delivery.create());
    }

    public void applyDiscount(String warehouse, long id, Promotion promotion) {
        Discount discount = discount(warehouse, promotion);
        if (discount == null) {
            throw new IllegalArgumentException("Select a promotion");
        }
        mediator.applyDiscount(id, discount);
    }

    public List<Order> orders() { return manager.getOrders(); }
    public Order order(long id) { return manager.findById(id); }

    public Statistics statistics() {
        return new Statistics(manager.getCustomerCount(), manager.getOrders().size(),
            manager.getTurnover());
    }

    public int activeOrderLimit() { return manager.getMaxActiveOrdersPerCustomer(); }

    private Warehouse warehouse(String name) {
        Warehouse result = warehouses.get(name);
        if (result == null) {
            throw new IllegalArgumentException("Warehouse not found: " + name);
        }
        return result;
    }

    private String newWarehouseName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Warehouse name cannot be empty");
        }
        String key = name.strip();
        if (warehouses.containsKey(key)) {
            throw new IllegalArgumentException("Warehouse already exists: " + key);
        }
        return key;
    }

    private Customer customer(long id) {
        Customer result = customers.get(id);
        if (result == null) {
            throw new IllegalArgumentException("Customer not found: " + id);
        }
        return result;
    }

    private Discount discount(String warehouse, Promotion promotion) {
        if (promotion == null) {
            return null;
        }
        return switch (promotion.type()) {
            case PERCENT -> new PercentDiscount();
            case CASHBACK -> new CashbackDiscount();
            case FREE_DELIVERY -> new FreeDeliveryDiscount();
            case GIFT -> new GiftDiscount(product(warehouse, promotion.giftId()));
        };
    }

    public enum DeliveryType {
        COURIER, PICKUP, POST;

        Delivery create() {
            return switch (this) {
                case COURIER -> new CourierDelivery();
                case PICKUP -> new PickupDelivery();
                case POST -> new PostDelivery();
            };
        }
    }

    public enum DiscountType { PERCENT, CASHBACK, FREE_DELIVERY, GIFT }

    public record Promotion(DiscountType type, long giftId) {}
    public record WarehouseInfo(int size, int capacity) {}
    public record Statistics(int orderingCustomers, int orders, double turnover) {}
}

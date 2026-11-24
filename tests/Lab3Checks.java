import command.ShopConsole;
import customer.Customer;
import delivery.Delivery;
import delivery.DeliveryProxy;
import demo.DemoData;
import inventory.Warehouse;
import java.util.List;
import order.Order;
import order.OrderManager;
import order.OrderMediator;
import order.OrderMediator.Line;
import order.OrderStatus;
import product.Product;
import product.attribute.Price;
import shop.ShopFacade;

public final class Lab3Checks {
    public static void main(String[] args) {
        warehouseCommands();
        orderCommands();
        failedOrdersAndRefunds();
        deliveryProxyAndShipment();
        System.out.println("Lab 3: all checks passed");
    }

    private static void warehouseCommands() {
        ShopFacade shop = shop();
        var console = new ShopConsole(shop);
        var add = console.parse("product add main ScoutDrone \"Study drone\" 100.005 3 1.5");
        check(shop.products("main").isEmpty(), "Parsing should not execute a command");
        shop.execute(add);
        Product product = shop.products("main").get(0);
        long id = product.getId();
        check(console.execute("show products main").contains("Study drone"), "List through warehouse");
        money(100.01, product.getPrice());
        console.execute("warehouse copy main \"new warehouse\"");
        Product copy = shop.products("new warehouse").get(0);
        check(copy.getId() != id && copy.getType() == product.getType(), "Copy with independent ID and shared type");
        console.execute("product price main " + id + " 99.90");
        console.execute("product stock main " + id + " -1");
        money(100.01, copy.getPrice());
        check(copy.getQuantity() == 3 && product.getQuantity() == 2, "Copy stays independent");
        fails("Quantity", () -> console.execute("product stock main " + id + " -3"));
        console.execute("product move main backup " + id);
        check(shop.products("main").isEmpty() && shop.product("backup", id) == product, "Transfer keeps ID");
        console.execute("product remove backup " + id);
        check(shop.products("backup").isEmpty(), "Product removed");
        fails("Unknown command", () -> console.execute("hello"));
        fails("Unknown command", () -> console.execute("product dance"));
        fails("Invalid arguments", () -> console.execute("order ship not-a-number"));
        fails("Invalid arguments", () -> console.execute("product remove \"main"));
        fails("Product not found", () -> console.execute("product remove main " + id));
        check(console.execute("show settings").contains("USD"), "Settings show money units");
    }

    private static void orderCommands() {
        ShopFacade shop = shop();
        var console = new ShopConsole(shop);
        Customer customer = shop.addCustomer("Buyer", "Central");
        Product product = product(shop, 100, 3, 1.5);
        long productId = product.getId();
        String create = "order create main " + customer.getId() + " courier percent " + productId + ":2";
        check(console.execute(create).contains("680.00 USD"), "Existing discount and courier price");
        Order order = shop.orders().get(0);
        long id = order.getId();
        check(order.getStatus() == OrderStatus.PENDING && product.getQuantity() == 1, "Creation only debits stock");
        money(0, shop.statistics().turnover());
        fails("Not enough", () -> console.execute(create));
        fails("active order", () -> console.execute("product remove main " + productId));
        fails("active order", () -> console.execute("product move main backup " + productId));
        fails("active order", () -> console.execute("warehouse move-all main backup"));
        fails("Cannot change", () -> console.execute("order ship " + id));
        console.execute("product price main " + productId + " 777.25");
        console.execute("order pay " + id);
        money(680, order.getTotalPrice());
        console.execute("product price main " + productId + " 500");
        fails("Cannot change", () -> console.execute("order pay " + id));
        money(680, order.getTotalPrice());
        money(680, shop.statistics().turnover());
        console.execute("order ship " + id);
        fails("Cannot change", () -> console.execute("order cancel " + id));
        console.execute("order deliver " + id);
        check(order.getStatus() == OrderStatus.DELIVERED && product.getQuantity() == 1, "Shipping does not debit twice");
        console.execute("product move main backup " + productId);
    }

    private static void failedOrdersAndRefunds() {
        ShopFacade shop = shop();
        var console = new ShopConsole(shop);
        Customer customer = shop.addCustomer("Buyer", "Central");
        Product first = product(shop, 120, 3, 1);
        Product second = product(shop, 50, 1, 25);
        String prefix = "order create main " + customer.getId();
        String firstLine = " " + first.getId() + ":2";
        String secondProduct = " " + second.getId();
        fails("Not enough", () -> console.execute(prefix + " pickup none" + firstLine + secondProduct + ":2"));
        fails("Product not found", () -> console.execute(prefix + " pickup none" + firstLine + " 999999:1"));
        fails("Not enough", () -> console.execute(prefix + " pickup none" + firstLine + firstLine));
        fails("Delivery is not available", () -> console.execute(prefix + " courier none" + firstLine + secondProduct + ":1"));
        fails("Gift makes", () -> console.execute(prefix + " courier gift:" + second.getId() + firstLine));
        check(shop.orders().isEmpty() && first.getQuantity() == 3 && second.getQuantity() == 1,
            "Rejected orders do not change stock or register");

        console.execute(prefix + " pickup cashback" + firstLine);
        Order order = shop.orders().get(0);
        money(0, customer.getBonusBalance());
        fails("active order limit", () -> console.execute(prefix + " pickup none " + first.getId() + ":1"));
        fails("Leave room", () -> shop.changeStock("main", first.getId(), Integer.MAX_VALUE - 1));
        long id = order.getId();
        console.execute("order pay " + id);
        money(12, customer.getBonusBalance());
        money(240, shop.statistics().turnover());
        console.execute("order cancel " + id);
        fails("Cannot change", () -> console.execute("order cancel " + id));
        money(0, customer.getBonusBalance());
        money(0, shop.statistics().turnover());
        check(first.getQuantity() == 3, "Cancellation returns stock exactly once");
        console.execute(prefix + " pickup none" + firstLine);
        console.execute("order cancel " + shop.orders().get(1).getId());
        check(first.getQuantity() == 3, "Pending cancellation also returns stock");
    }

    private static void deliveryProxyAndShipment() {
        var manager = new OrderManager(1);
        var mediator = new OrderMediator(manager);
        var warehouse = new Warehouse(2);
        Product product = DemoData.productFactory("ScoutDrone").createProduct("Drone", 10, 3, 2);
        warehouse.addProduct(product);
        var delivery = new ChangingDelivery();
        Customer customer = new Customer("Buyer", "Central");
        // Keep the old order editing scenario before a delivery is selected.
        Order order = mediator.createOrder(warehouse, customer, List.of(new Line(product.getId(), 1)), null, null);
        mediator.addItem(warehouse, order.getId(), new Line(product.getId(), 1));
        var extraItem = order.getItems().get(1);
        mediator.removeItem(order.getId(), extraItem);
        check(product.getQuantity() == 2, "Order editing debits and returns stock");
        mediator.assignDelivery(order.getId(), delivery);
        check(order.getDelivery() instanceof DeliveryProxy, "Mediator uses existing proxy");
        money(12, order.getTotalPrice());
        money(12, order.getTotalPrice());
        check(delivery.quotes == 1, "Repeated tariff request uses cache");
        delivery.multiplier = 3;
        money(16, order.getTotalPrice());
        check(delivery.quotes == 2, "Tariff change invalidates cache");
        mediator.confirmPayment(order.getId());
        delivery.multiplier = 5;
        delivery.available = false;
        fails("Delivery is not available", () -> mediator.ship(order.getId()));
        check(order.getStatus() == OrderStatus.PAID && product.getQuantity() == 2, "Failed shipment preserves state");
        money(16, order.getTotalPrice());
        money(16, manager.getTurnover());
        mediator.cancel(order.getId());
        check(product.getQuantity() == 3 && manager.getActiveOrderCount(customer) == 0, "Cancellation releases stock and limit");
        money(0, manager.getTurnover());
    }

    private static ShopFacade shop() {
        var shop = new ShopFacade(1);
        shop.createWarehouse("main", 10);
        shop.createWarehouse("backup", 10);
        return shop;
    }

    private static Product product(ShopFacade shop, double price, int quantity, double weight) {
        return shop.addProduct("main", DemoData.productFactory("ScoutDrone"), "Drone", new Price(price), quantity, weight);
    }

    private static void money(double expected, double actual) {
        check(Double.compare(expected, actual) == 0, "Expected " + expected + " USD, got " + actual);
    }

    private static void fails(String message, Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            check(exception.getMessage().contains(message), "Wrong error: " + exception.getMessage());
            return;
        }
        throw new AssertionError("Expected error: " + message);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class ChangingDelivery implements Delivery {
        private int multiplier = 1;
        private int quotes;
        private boolean available = true;

        @Override
        public double getCost(Order order) {
            quotes++;
            return multiplier * order.getTotalWeight();
        }

        @Override
        public String getEstimatedTime() { return "test delivery"; }

        @Override
        public boolean isAvailable(Order order) { return available; }

        @Override
        public long getTariffVersion() { return multiplier; }
    }
}

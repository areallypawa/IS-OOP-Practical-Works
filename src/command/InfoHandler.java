package command;

import demo.DemoData;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import order.Order;
import product.Product;
import shop.ShopCommand;
import shop.ShopFacade.Statistics;

final class InfoHandler extends CommandHandler {
    private static final String HELP = """
        product add <warehouse> <type> <name> <price> <quantity> <kg>
        product price <warehouse> <id> <price>
        product stock <warehouse> <id> <delta>
        product remove <warehouse> <id>
        product move <source> <target> <id>
        warehouse add <name> <capacity>
        warehouse copy <source> <name>
        warehouse move-all <source> <target>
        customer add <name> <region>
        order create <warehouse> <customer-id> <delivery> <promotion> <id:quantity>...
        order pay <order-id>
        order cancel <order-id>
        order ship <order-id>
        order deliver <order-id>
        show products <warehouse>
        show orders [id]
        show statistics | settings | warehouses | customers
        help

        Use double quotes around names with spaces. Prices are in USD, weight in kg.
        Use a dot for decimals, e.g. 1000.25. Stock changes take a signed integer.
        Delivery: courier, pickup, post. Promotion: none, percent, cashback,
        free-delivery, gift:<product-id>. See 'show settings' for product types.
        Look up IDs with 'show'. Errors do not execute unrelated operations.
        Terminal only: enter 'exit' to close.
        """;

    InfoHandler() {
        super(null, "help", "show");
    }

    @Override
    ShopCommand<String> build(List<String> args) {
        if (args.get(0).equals("help")) {
            size(args, 1, "help");
            return shop -> HELP;
        }
        return switch (verb(args)) {
            case "products" -> {
                size(args, 3, "show products <warehouse>");
                String warehouse = args.get(2);
                yield shop -> show(shop.products(warehouse).stream().map(InfoHandler::describeProduct).toList());
            }
            case "orders" -> {
                if (args.size() == 2) {
                    yield shop -> show(shop.orders().stream().map(InfoHandler::describeOrder).toList());
                }
                size(args, 3, "show orders [id]");
                long id = id(args.get(2));
                yield shop -> describeOrder(shop.order(id));
            }
            case "statistics" -> {
                size(args, 2, "show statistics");
                yield shop -> {
                    Statistics stats = shop.statistics();
                    return "Customers with orders: " + stats.orderingCustomers()
                        + " | Orders: " + stats.orders() + " | Net turnover: " + money(stats.turnover());
                };
            }
            case "warehouses" -> {
                size(args, 2, "show warehouses");
                yield shop -> show(shop.warehouseNames().stream().map(name -> {
                    var info = shop.warehouseInfo(name);
                    return name + " (" + info.size() + "/" + info.capacity() + ")";
                }).toList());
            }
            case "customers" -> {
                size(args, 2, "show customers");
                yield shop -> show(shop.customers().stream().map(customer -> "#" + customer.getId()
                    + " " + customer.getName() + " | " + customer.getRegion()
                    + " | bonus: " + money(customer.getBonusBalance())).toList());
            }
            case "settings" -> {
                size(args, 2, "show settings");
                yield shop -> "Active orders per customer: " + shop.activeOrderLimit()
                    + "\nPreset product types: " + String.join(", ", DemoData.productTypes())
                    + "\nCurrency: USD (2 decimals, HALF_UP); weight: kg; quantity: whole units."
                    + "\nDelivery: courier (max 20 kg), post (max 10 kg), pickup."
                    + "\nStock deducted on creation, returned once on cancellation before shipment.";
            }
            default -> throw unknown("show " + args.get(1));
        };
    }

    static String describeOrder(Order order) {
        String lines = order.getItems().stream().map(item -> "#" + item.getProductId()
            + " " + item.getProductName() + " x" + item.getQuantity()
            + " @ " + money(item.getUnitPrice())).collect(Collectors.joining("; "));
        String gifts = order.getGiftProducts().stream().map(Product::getName)
            .collect(Collectors.joining(", "));
        return "Order #" + order.getId() + " " + order.getStatus() + " | " + lines
            + (gifts.isEmpty() ? "" : " | Gifts: " + gifts)
            + " | Total: " + money(order.getTotalPrice());
    }

    private static String describeProduct(Product product) {
        return "#" + product.getId() + " " + product.getName() + " [" + product.getCategory() + "]"
            + " | " + money(product.getPrice()) + " | Available: " + product.getQuantity()
            + " | " + product.getWeight() + " kg/unit";
    }

    private static String money(double amount) {
        return String.format(Locale.ROOT, "%.2f USD", amount);
    }

    private static String show(List<String> lines) {
        return lines.isEmpty() ? "No results" : String.join("\n", lines);
    }
}

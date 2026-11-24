package command;

import demo.DemoData;
import factory.ProductFactory;
import java.util.List;
import product.attribute.Price;
import shop.ShopCommand;

final class ProductHandler extends CommandHandler {
    ProductHandler(CommandHandler next) {
        super(next, "product", "warehouse");
    }

    @Override
    ShopCommand<String> build(List<String> words) {
        String action = verb(words);
        return words.get(0).equals("warehouse") ? warehouse(words, action) : product(words, action);
    }

    private ShopCommand<String> product(List<String> args, String action) {
        return switch (action) {
            case "add" -> {
                size(args, 8, "product add <warehouse> <type> <name> <price> <quantity> <kg>");
                String warehouse = args.get(2);
                ProductFactory factory = DemoData.productFactory(args.get(3));
                if (factory == null) {
                    throw invalid("unknown product type: " + args.get(3) + ". See show settings.");
                }
                String name = args.get(4);
                Price price = new Price(decimal(args.get(5)));
                int quantity = quantity(args.get(6), 0);
                double weight = decimal(args.get(7));
                yield shop -> "Added product #" + shop.addProduct(
                    warehouse, factory, name, price, quantity, weight).getId();
            }
            case "price" -> {
                size(args, 5, "product price <warehouse> <id> <price>");
                String warehouse = args.get(2);
                long id = id(args.get(3));
                Price price = new Price(decimal(args.get(4)));
                yield shop -> {
                    shop.changePrice(warehouse, id, price);
                    return "Updated price for product #" + id;
                };
            }
            case "stock" -> {
                size(args, 5, "product stock <warehouse> <id> <delta>");
                String warehouse = args.get(2);
                long id = id(args.get(3));
                int delta = integer(args.get(4));
                yield shop -> {
                    shop.changeStock(warehouse, id, delta);
                    return "Available: " + shop.product(warehouse, id).getQuantity();
                };
            }
            case "remove" -> {
                size(args, 4, "product remove <warehouse> <id>");
                String warehouse = args.get(2);
                long id = id(args.get(3));
                yield shop -> {
                    shop.removeProduct(warehouse, id);
                    return "Removed product #" + id;
                };
            }
            case "move" -> {
                size(args, 5, "product move <source> <target> <id>");
                String source = args.get(2);
                String target = args.get(3);
                long id = id(args.get(4));
                yield shop -> {
                    shop.transferProduct(source, target, id);
                    return "Product #" + id + " is in " + target;
                };
            }
            default -> throw unknown("product " + action);
        };
    }

    private ShopCommand<String> warehouse(List<String> args, String action) {
        return switch (action) {
            case "add" -> {
                size(args, 4, "warehouse add <name> <capacity>");
                String name = args.get(2);
                int capacity = quantity(args.get(3), 1);
                yield shop -> "Added warehouse " + shop.createWarehouse(name, capacity);
            }
            case "copy" -> {
                size(args, 4, "warehouse copy <source> <name>");
                String source = args.get(2);
                String name = args.get(3);
                yield shop -> "Copied warehouse to " + shop.copyWarehouse(source, name);
            }
            case "move-all" -> {
                size(args, 4, "warehouse move-all <source> <target>");
                String source = args.get(2);
                String target = args.get(3);
                yield shop -> {
                    shop.transferAll(source, target);
                    return "Moved products to " + target;
                };
            }
            default -> throw unknown("warehouse " + action);
        };
    }
}

package command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.ObjLongConsumer;
import order.OrderMediator.Line;
import shop.ShopCommand;
import shop.ShopFacade;
import shop.ShopFacade.DeliveryType;
import shop.ShopFacade.DiscountType;
import shop.ShopFacade.Promotion;

final class OrderHandler extends CommandHandler {
    OrderHandler(CommandHandler next) {
        super(next, "order", "customer");
    }

    @Override
    ShopCommand<String> build(List<String> words) {
        String action = verb(words);
        if (words.get(0).equals("customer")) {
            if (!action.equals("add")) {
                throw unknown("customer " + action);
            }
            size(words, 4, "customer add <name> <region>");
            String name = words.get(2);
            String region = words.get(3);
            return shop -> "Added customer #" + shop.addCustomer(name, region).getId();
        }
        return switch (action) {
            case "create" -> create(words);
            case "pay" -> status(words, ShopFacade::confirmPayment);
            case "ship" -> status(words, ShopFacade::ship);
            case "deliver" -> status(words, ShopFacade::confirmDelivery);
            case "cancel" -> status(words, ShopFacade::cancel);
            default -> throw unknown("order " + action);
        };
    }

    private ShopCommand<String> create(List<String> words) {
        if (words.size() < 7) {
            throw invalid("use order create <warehouse> <customer-id> <delivery> <promotion> <id:quantity>...");
        }
        String warehouse = words.get(2);
        long customerId = id(words.get(3));
        DeliveryType delivery;
        try {
            delivery = DeliveryType.valueOf(words.get(4).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw invalid("delivery must be courier, pickup or post");
        }
        Promotion promo = promotion(words.get(5));
        List<Line> items = new ArrayList<>();
        for (String token : words.subList(6, words.size())) {
            String[] parts = token.split(":", -1);
            if (parts.length != 2) {
                throw invalid("expected product-id:quantity, got " + token);
            }
            items.add(new Line(id(parts[0]), quantity(parts[1], 1)));
        }
        List<Line> lines = List.copyOf(items);
        return shop -> InfoHandler.describeOrder(shop.createOrder(
            warehouse, customerId, lines, delivery, promo));
    }

    private static ShopCommand<String> status(List<String> words, ObjLongConsumer<ShopFacade> action) {
        size(words, 3, "order " + words.get(1) + " <order-id>");
        long id = id(words.get(2));
        return shop -> {
            action.accept(shop, id);
            return "Order #" + id + " is " + shop.order(id).getStatus();
        };
    }

    private static Promotion promotion(String text) {
        return switch (text) {
            case "none" -> null;
            case "percent" -> new Promotion(DiscountType.PERCENT, 0);
            case "cashback" -> new Promotion(DiscountType.CASHBACK, 0);
            case "free-delivery" -> new Promotion(DiscountType.FREE_DELIVERY, 0);
            default -> {
                if (text.startsWith("gift:")) {
                    yield new Promotion(DiscountType.GIFT, id(text.substring(5)));
                }
                throw invalid("promotion must be none, percent, cashback, free-delivery or gift:<id>");
            }
        };
    }
}

package discount;

import order.Order;

public final class FreeDeliveryDiscount implements Discount {

    @Override
    public void apply(Order order) {
        validateOrder(order);
        order.makeDeliveryFree();
    }

    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}

package discount;

import order.Order;

public final class FreeDeliveryDiscount implements Discount {
    @Override
    public void apply(Order order) {
        validateOrder(order);
        // TODO: make the assigned delivery free for this order.
        throw new UnsupportedOperationException("Free delivery discount is not implemented yet");
    }

    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}

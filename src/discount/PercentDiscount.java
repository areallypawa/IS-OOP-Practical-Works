package discount;

import order.Order;

public final class PercentDiscount implements Discount {
    private static final double DISCOUNT_RATE = 0.10;

    @Override
    public void apply(Order order) {
        validateOrder(order);
        // TODO: reduce the order price by DISCOUNT_RATE.
        throw new UnsupportedOperationException("Percent discount is not implemented yet");
    }

    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}

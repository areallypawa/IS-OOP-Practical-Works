package discount;

import order.Order;

public final class CashbackDiscount implements Discount {
    private static final double CASHBACK_RATE = 0.05;

    @Override
    public void apply(Order order) {
        validateOrder(order);
        // TODO: add CASHBACK_RATE of the order price to the customer bonus balance.
        throw new UnsupportedOperationException("Cashback discount is not implemented yet");
    }

    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}

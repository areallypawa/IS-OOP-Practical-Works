package discount;

import order.Order;

public final class CashbackDiscount implements Discount {
    private static final double CASHBACK_RATE = 0.05;

    @Override
    public void apply(Order order) {
        validateOrder(order);

        double cashback = order.getTotalPrice() * CASHBACK_RATE;

        if (cashback > 0) {
            order.getCustomer().addBonus(cashback);
        }
    }


    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}

package discount;

import java.math.BigDecimal;
import order.Order;
import product.attribute.Price;

public final class CashbackDiscount implements Discount {
    private static final BigDecimal CASHBACK_RATE = new BigDecimal("0.05");

    @Override
    public void apply(Order order) {
        validateOrder(order);

        double cashback = Price.multiply(order.getTotalPrice(), CASHBACK_RATE);

        if (cashback > 0) {
            order.addCashback(cashback);
        }
    }


    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}

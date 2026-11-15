package discount;

import order.Order;

public interface Discount {
    void apply(Order order);
}

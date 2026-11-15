package delivery;

import order.Order;

public final class PickupDelivery implements Delivery {
    private static final double COST = 0.0;
    private static final String ESTIMATED_TIME = "1-3 days";

    @Override
    public double getCost(Order order) {
        validateOrder(order);
        return COST;
    }

    @Override
    public String getEstimatedTime() {
        return ESTIMATED_TIME;
    }

    @Override
    public boolean isAvailable(Order order) {
        validateOrder(order);
        return true;
    }

    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}

package delivery;

import order.Order;

public final class CourierDelivery implements Delivery {
    private static final double COST = 500.0;
    private static final String ESTIMATED_TIME = "1-2 days";
    private static final double MAX_WEIGHT = 20.0;

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
        if (order.getTotalWeight() <= MAX_WEIGHT) {
            return true;
        }
        return false;
    }

    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}

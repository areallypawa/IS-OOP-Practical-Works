package delivery;

import order.Order;

public final class PostDelivery implements Delivery {
    private static final double COST = 200.0;
    private static final String ESTIMATED_TIME = "5-10 days";
    private static final double MAX_WEIGHT = 10.0;

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
        return order.getTotalWeight() <= MAX_WEIGHT;
    }

    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}

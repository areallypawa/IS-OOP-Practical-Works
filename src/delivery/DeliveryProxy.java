package delivery;

import java.util.HashMap;
import java.util.Map;
import order.Order;

public final class DeliveryProxy implements Delivery {
    private final Delivery realDelivery;
    private final Map<Long, Double> costCache = new HashMap<>();

    public DeliveryProxy(Delivery realDelivery) {
        if (realDelivery == null) {
            throw new IllegalArgumentException("Real delivery cannot be null");
        }
        this.realDelivery = realDelivery;
    }

    @Override
    public double getCost(Order order) {
        validateOrder(order);

        if (!isAvailable(order)) {
            throw new IllegalStateException("Delivery is not available");
        }

        Double cachedCost = costCache.get(order.getId());
        if (cachedCost != null) {
            System.out.println("Using cached delivery cost for order " + order.getId());
            return cachedCost;
        }

        System.out.println("Calculating delivery cost for order " + order.getId());
        double cost = realDelivery.getCost(order);
        costCache.put(order.getId(), cost);
        return cost;
    }

    @Override
    public String getEstimatedTime() {
        return realDelivery.getEstimatedTime();
    }

    @Override
    public boolean isAvailable(Order order) {
        validateOrder(order);

        String region = order.getCustomer().getRegion();
        if (region == null || region.isBlank()) {
            return false;
        }

        return realDelivery.isAvailable(order);
    }

    public void clearCache() {
        costCache.clear();
    }

    public int getCachedOrderCount() {
        return costCache.size();
    }

    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getCustomer() == null) {
            throw new IllegalArgumentException("Order customer cannot be null");
        }
        if (!Double.isFinite(order.getTotalWeight()) || order.getTotalWeight() < 0) {
            throw new IllegalArgumentException("Order weight must be finite and non-negative");
        }
    }
}

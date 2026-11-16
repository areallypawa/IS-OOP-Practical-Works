package delivery;

import java.util.HashMap;
import java.util.Map;
import logging.LoggingProxy;
import logging.LoggingProxy.Source;
import order.Order;

public final class DeliveryProxy implements Delivery {
    private final Delivery realDelivery;
    private final Map<Long, CachedCost> costCache = new HashMap<>();

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

        long stateVersion = order.getDeliveryStateVersion();
        CachedCost cachedCost = costCache.get(order.getId());
        if (cachedCost != null && cachedCost.stateVersion() == stateVersion) {
            LoggingProxy.log(
                Source.DELIVERY,
                "Using cached cost for order " + order.getId()
            );
            return cachedCost.cost();
        }

        if (cachedCost != null) {
            LoggingProxy.log(
                Source.DELIVERY,
                "Invalidating cached cost for order " + order.getId()
            );
        }

        double cost = LoggingProxy.call(
            Source.DELIVERY,
            "Calculate cost for order " + order.getId(),
            () -> realDelivery.getCost(order)
        );
        costCache.put(order.getId(), new CachedCost(stateVersion, cost));
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

    private record CachedCost(long stateVersion, double cost) {}

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

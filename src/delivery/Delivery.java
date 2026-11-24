package delivery;

import order.Order;
/**
 * Delivery interface
 */
public interface Delivery {
    public double getCost(Order order);
    public String getEstimatedTime();
    public boolean isAvailable(Order order);

    // Strategies with a changing tariff advance this version when their rates change.
    default long getTariffVersion() {
        return 0;
    }
}

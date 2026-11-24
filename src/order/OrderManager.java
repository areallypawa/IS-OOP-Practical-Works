package order;

import customer.Customer;
import logging.LoggingProxy;
import logging.LoggingProxy.Source;
import observer.Observer;
import observer.OrderEvent;
import product.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderManager implements Observer {
    private final Map<Long, Order> orders = new LinkedHashMap<>();
    private final Set<Long> customerIds = new LinkedHashSet<>();
    private final int maxActiveOrdersPerCustomer;
    private BigDecimal turnover = BigDecimal.ZERO;

    public OrderManager(int maxActiveOrdersPerCustomer) {
        if (maxActiveOrdersPerCustomer <= 0) {
            throw new IllegalArgumentException("Maximum active orders must be positive");
        }
        this.maxActiveOrdersPerCustomer = maxActiveOrdersPerCustomer;
    }

    public Order createOrder(Customer customer, List<OrderItem> items) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        Order order = new Order(customer, items);
        registerOrder(order);
        return order;
    }

    void validateNewOrder(Order order) {
        if (getActiveOrderCount(order.getCustomer()) >= maxActiveOrdersPerCustomer) {
            throw new IllegalStateException("Customer has reached the active order limit");
        }

        for (var entry : order.quantitiesByProduct().entrySet()) {
            Product product = entry.getKey();
            if (product.getQuantity() < entry.getValue()) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }
        }
    }

    void registerOrder(Order order) {
        validateNewOrder(order);
        order.quantitiesByProduct().forEach((product, count) ->
            product.changeQuantity(-count.intValue()));
        orders.put(order.getId(), order);
        order.subscribe(this);
        customerIds.add(order.getCustomer().getId());
        LoggingProxy.log(Source.MANAGER, "Order " + order.getId() + " created");
    }

    public Order findById(long orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return order;
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(new ArrayList<>(orders.values()));
    }

    public double getTurnover() {
        return turnover.doubleValue();
    }

    public int getMaxActiveOrdersPerCustomer() {
        return maxActiveOrdersPerCustomer;
    }

    public int getCustomerCount() {
        return customerIds.size();
    }

    public int getActiveOrderCount(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        int count = 0;
        for (Order order : orders.values()) {
            OrderStatus status = order.getStatus();
            if (order.getCustomer().getId() == customer.getId()
                && (status == OrderStatus.PENDING
                    || status == OrderStatus.PAID
                    || status == OrderStatus.SHIPPED)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onOrderEvent(OrderEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Order event cannot be null");
        }

        Order order = findById(event.getOrderId());
        OrderStatus newStatus = event.getNewStatus();

        if (newStatus == OrderStatus.PAID) {
            turnover = turnover.add(BigDecimal.valueOf(order.getTotalPrice()));
        }

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order.getItems());
            if (event.getOldStatus() == OrderStatus.PAID) {
                turnover = turnover.subtract(BigDecimal.valueOf(order.getTotalPrice()));
            }
        }

        LoggingProxy.log(
            Source.MANAGER,
            "Processed " + event.getOldStatus() + " -> " + newStatus
                + " for order " + order.getId()
        );
    }


    private void restoreStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            item.getSourceProduct().changeQuantity(item.getQuantity());
        }
    }
}

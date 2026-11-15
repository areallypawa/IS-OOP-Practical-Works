package order;

import customer.Customer;
import product.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderManager implements OrderStatusListener {
    private final Map<Long, Order> orders = new LinkedHashMap<>();
    private final Set<Long> customerIds = new LinkedHashSet<>();
    private final int maxActiveOrdersPerCustomer;
    private double turnover;

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
        if (items.contains(null)) {
            throw new IllegalArgumentException("Order items cannot contain null");
        }
        if (getActiveOrderCount(customer) >= maxActiveOrdersPerCustomer) {
            throw new IllegalStateException("Customer has reached the active order limit");
        }

        List<OrderItem> itemSnapshot = new ArrayList<>(items);
        validateStock(itemSnapshot);
        deductStock(itemSnapshot);

        Order order = new Order(customer, itemSnapshot);
        order.addStatusListener(this);
        orders.put(order.getId(), order);
        customerIds.add(customer.getId());
        return order;
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
        return turnover;
    }

    public int getCustomerCount() {
        return customerIds.size();
    }

    public int getActiveOrderCount(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        int count = 0;
        for (Order order: orders.values()) {
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
    public void onStatusChanged(
        Order order,
        OrderStatus oldStatus,
        OrderStatus newStatus
    ) {
        if (newStatus == OrderStatus.PAID) {
            turnover += order.getTotalPrice();
        }

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order.getItems());
        }
    }

    private void validateStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = item.getSourceProduct();

            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalStateException( "Not enough stock for product: " + product.getName());
            }
        }
    }

    private void deductStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            item.getSourceProduct().changeQuantity(-item.getQuantity());
        }
    }

    private void restoreStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            item.getSourceProduct().changeQuantity(item.getQuantity());
        }
    }
}

package order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import customer.Customer;
import delivery.Delivery;
import discount.Discount;
import product.Product;

public class Order {
    private static long nextId = 1;

    private final long id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final List<OrderStatusListener> statusListeners = new ArrayList<>();
    private OrderStatus status;
    private Delivery delivery;
    private final List<Discount> discounts = new ArrayList<>();
    private double totalPrice;
    private boolean freeDelivery;
    private final List<Product> giftProducts = new ArrayList<>();

    public Order(Customer customer, List<OrderItem> items) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        if (items.contains(null)) {
            throw new IllegalArgumentException("Order items cannot contain null");
        }

        this.id = nextId++;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.PENDING;
        this.totalPrice = calculateItemsTotal();
    }

    public long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public double getTotalPrice() {
        return totalPrice + getDeliveryCost();
    }

    public double getDeliveryCost() {
        if (delivery == null || freeDelivery) {
            return 0;
        }

        return delivery.getCost(this);
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public List<Discount> getDiscounts() {
        return Collections.unmodifiableList(discounts);
    }

    public List<Product> getGiftProducts() {
        return Collections.unmodifiableList(giftProducts);
    }

    public void setDelivery(Delivery delivery) {
        if (delivery == null) {
            throw new IllegalArgumentException("Delivery cannot be null");
        }

        if (!delivery.isAvailable(this)) {
            throw new IllegalStateException("Delivery is not available for this order");
        }

        this.delivery = delivery;
    }

    public void makeDeliveryFree() {
        freeDelivery = true;
    }

    private double calculateItemsTotal() {
        double result = 0;

        for (OrderItem item : items) {
            result += item.getTotalPrice();
        }

        return result;
    }

    public void addGift(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Gift product cannot be null");
        }

        giftProducts.add(product);
    }

    public double getTotalWeight() {
        double result = 0;

        for (OrderItem item : items) {
            result += item.getTotalWeight();
        }

        return result;
    }

    public void addStatusListener(OrderStatusListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Status listener cannot be null");
        }
        if (!statusListeners.contains(listener)) {
            statusListeners.add(listener);
        }
    }

    public void removeStatusListener(OrderStatusListener listener) {
        statusListeners.remove(listener);
    }

    public void changeStatus(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        boolean transitionAllowed = switch (status) {
            case PENDING -> newStatus == OrderStatus.PAID
                || newStatus == OrderStatus.CANCELLED;
            case PAID -> newStatus == OrderStatus.SHIPPED
                || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!transitionAllowed) {
            throw new IllegalStateException(
                "Cannot change order status from " + status + " to " + newStatus
            );
        }

        OrderStatus oldStatus = status;
        status = newStatus;

        notifyStatusListeners(oldStatus, newStatus);
    }

    private void notifyStatusListeners(
        OrderStatus oldStatus,
        OrderStatus newStatus
    ) {
        for (OrderStatusListener listener : List.copyOf(statusListeners)) {
            listener.onStatusChanged(this, oldStatus, newStatus);
        }
    }

    public void applyDiscount(Discount discount) {
        if (discount == null) {
            throw new IllegalArgumentException("Discount cannot be null");
        }

        discount.apply(this);
        discounts.add(discount);
    }

    public void reducePriceByRate(double rate) {
        if (!Double.isFinite(rate) || rate < 0 || rate > 1) {
            throw new IllegalArgumentException("Rate must be between 0 and 1");
        }

        totalPrice *= 1 - rate;
    }

}

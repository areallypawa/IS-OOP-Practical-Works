package order;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import customer.Customer;
import delivery.Delivery;
import discount.Discount;
import logging.LoggingProxy;
import logging.LoggingProxy.Source;
import observer.Observable;
import observer.Observer;
import observer.OrderEvent;
import product.Product;

public class Order implements Observable, Comparable<Order> {
    private static long nextId = 1;

    private final long id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final List<Observer> observers = new ArrayList<>();
    private OrderStatus status;
    private Delivery delivery;
    private final List<Discount> discounts = new ArrayList<>();
    private double totalPrice;
    private long deliveryStateVersion;
    private boolean freeDelivery;
    private final List<Product> giftProducts = new ArrayList<>();

    public Order(Customer customer, List<OrderItem> items) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        for (OrderItem item : items) {
            if (item == null) {
                throw new IllegalArgumentException("Order items cannot contain null");
            }
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

    public long getDeliveryStateVersion() {
        return deliveryStateVersion;
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
        markDeliveryStateChanged();
        LoggingProxy.log(Source.ORDER, "Delivery assigned to order " + id);
    }

    public void assignTo(Delivery delivery) {
        setDelivery(delivery);
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Order item cannot be null");
        }
        ensureItemsMutable();

        Product product = item.getSourceProduct();
        if (product.getQuantity() < item.getQuantity()) {
            throw new IllegalStateException(
                "Not enough stock for product: " + product.getName()
            );
        }

        product.changeQuantity(-item.getQuantity());
        items.add(item);
        totalPrice += item.getTotalPrice();
        markDeliveryStateChanged();
        LoggingProxy.log(Source.ORDER, "Item added to order " + id);
    }

    public boolean removeItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Order item cannot be null");
        }
        ensureItemsMutable();

        if (!items.contains(item)) {
            return false;
        }
        if (items.size() == 1) {
            throw new IllegalStateException("Order must contain at least one item");
        }

        items.remove(item);
        item.getSourceProduct().changeQuantity(item.getQuantity());
        totalPrice -= item.getTotalPrice();
        markDeliveryStateChanged();
        LoggingProxy.log(Source.ORDER, "Item removed from order " + id);
        return true;
    }

    @Override
    public int compareTo(Order other) {
        if (other == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        int priceComparison = Double.compare(getTotalPrice(), other.getTotalPrice());
        if (priceComparison != 0) {
            return priceComparison;
        }
        return Long.compare(id, other.id);
    }

    @Override
    public String toString() {
        String deliveryName = delivery == null
            ? "not assigned"
            : delivery.getClass().getSimpleName();

        return "Order #" + id
            + " {customer='" + customer.getName() + '\''
            + ", status=" + status
            + ", items=" + items.size()
            + ", delivery=" + deliveryName
            + ", discounts=" + discounts.size()
            + ", gifts=" + giftProducts.size()
            + ", totalPrice=" + getTotalPrice()
            + '}';
    }
    
    public void makeDeliveryFree() {
        freeDelivery = true;
        markDeliveryStateChanged();
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
        if (delivery != null && !delivery.isAvailable(this)) {
            giftProducts.remove(giftProducts.size() - 1);
            throw new IllegalStateException("Gift makes the selected delivery unavailable");
        }
        markDeliveryStateChanged();
    }

    public double getTotalWeight() {
        double result = 0;

        for (OrderItem item : items) {
            result += item.getTotalWeight();
        }
        for (Product giftProduct : giftProducts) {
            result += giftProduct.getWeight();
        }

        return result;
    }

    @Override
    public void subscribe(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
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
        markDeliveryStateChanged();

        notifyObservers(new OrderEvent(id, oldStatus, newStatus, Instant.now()));
        LoggingProxy.log(
            Source.ORDER,
            "Order " + id + " status changed from " + oldStatus + " to " + newStatus
        );
    }

    @Override
    public void notifyObservers(OrderEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Order event cannot be null");
        }
        if (event.getOrderId() != id) {
            throw new IllegalArgumentException("Event belongs to another order");
        }

        for (Observer observer : List.copyOf(observers)) {
            observer.onOrderEvent(event);
        }
    }

    public void applyDiscount(Discount discount) {
        if (discount == null) {
            throw new IllegalArgumentException("Discount cannot be null");
        }

        LoggingProxy.run(
            Source.DISCOUNT,
            "Apply " + discount.getClass().getSimpleName() + " to order " + id,
            () -> discount.apply(this)
        );
        discounts.add(discount);
        markDeliveryStateChanged();
    }

    public void reducePriceByRate(double rate) {
        if (!Double.isFinite(rate) || rate < 0 || rate > 1) {
            throw new IllegalArgumentException("Rate must be between 0 and 1");
        }

        totalPrice *= 1 - rate;
        markDeliveryStateChanged();
    }

    private void markDeliveryStateChanged() {
        deliveryStateVersion++;
    }

    private void ensureItemsMutable() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be changed");
        }
        if (delivery != null || !discounts.isEmpty()) {
            throw new IllegalStateException(
                "Items cannot be changed after assigning delivery or applying discounts"
            );
        }
    }
}

package observer;

import java.time.Instant;
import order.OrderStatus;

public final class OrderEvent {
    private final long orderId;
    private final OrderStatus oldStatus;
    private final OrderStatus newStatus;
    private final Instant timestamp;

    public OrderEvent(
        long orderId,
        OrderStatus oldStatus,
        OrderStatus newStatus,
        Instant timestamp
    ) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be positive");
        }
        if (oldStatus == null) {
            throw new IllegalArgumentException("Old status cannot be null");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }
        if (oldStatus == newStatus) {
            throw new IllegalArgumentException("Order status must change");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }

        this.orderId = orderId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.timestamp = timestamp;
    }

    public long getOrderId() {
        return orderId;
    }

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}

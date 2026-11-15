package order;

public interface OrderStatusListener {
    void onStatusChanged(
        Order order,
        OrderStatus oldStatus,
        OrderStatus newStatus
    );
}

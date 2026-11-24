package order;

import customer.Customer;
import delivery.Delivery;
import delivery.DeliveryProxy;
import discount.Discount;
import inventory.Warehouse;
import java.util.ArrayList;
import java.util.List;
import product.Product;

public final class OrderMediator {
    private final OrderManager manager;

    public OrderMediator(OrderManager manager) {
        this.manager = manager;
    }

    public Order createOrder(
        Warehouse warehouse, Customer customer, List<Line> lines,
        Delivery delivery, Discount discount
    ) {
        List<OrderItem> items = new ArrayList<>();
        for (Line line : lines) {
            items.add(new OrderItem(requireProduct(warehouse, line.productId()), line.quantity()));
        }

        Order draft = new Order(customer, items);
        manager.validateNewOrder(draft);
        if (delivery != null) {
            draft.assignTo(withProxy(delivery));
        }
        if (discount != null) {
            draft.applyDiscount(discount);
        }
        draft.getTotalPrice();
        manager.registerOrder(draft);
        return draft;
    }

    public void confirmPayment(long id) {
        Order order = manager.findById(id);
        if (order.getDelivery() == null) {
            throw new IllegalStateException("Select a delivery before paying");
        }
        order.changeStatus(OrderStatus.PAID);
    }

    public void ship(long id) {
        manager.findById(id).changeStatus(OrderStatus.SHIPPED);
    }

    public void confirmDelivery(long id) {
        manager.findById(id).changeStatus(OrderStatus.DELIVERED);
    }

    public void cancel(long id) {
        manager.findById(id).changeStatus(OrderStatus.CANCELLED);
    }

    public void addItem(Warehouse warehouse, long id, Line line) {
        manager.findById(id).addItem(new OrderItem(
            requireProduct(warehouse, line.productId()), line.quantity()
        ));
    }

    public void removeItem(long id, OrderItem item) {
        if (!manager.findById(id).removeItem(item)) {
            throw new IllegalArgumentException("Order item not found");
        }
    }

    public void assignDelivery(long id, Delivery delivery) {
        manager.findById(id).assignTo(withProxy(delivery));
    }

    public void applyDiscount(long id, Discount discount) {
        manager.findById(id).applyDiscount(discount);
    }

    public void checkCanMoveOrRemove(Product product) {
        for (Order order : manager.getOrders()) {
            OrderStatus status = order.getStatus();
            if ((status == OrderStatus.PENDING || status == OrderStatus.PAID
                || status == OrderStatus.SHIPPED) && orderedQuantity(order, product) > 0) {
                throw new IllegalStateException("Product is used by active order #" + order.getId());
            }
        }
    }

    public void changeStock(Product product, int delta) {
        long returnable = 0;
        for (Order order : manager.getOrders()) {
            if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PAID) {
                returnable += orderedQuantity(order, product);
            }
        }
        if ((long) product.getQuantity() + delta + returnable > Integer.MAX_VALUE) {
            throw new IllegalStateException("Leave room for stock that can be returned by cancellation");
        }
        product.changeQuantity(delta);
    }

    private static long orderedQuantity(Order order, Product product) {
        return order.quantitiesByProduct().getOrDefault(product, 0L);
    }

    private static Delivery withProxy(Delivery delivery) {
        return delivery instanceof DeliveryProxy ? delivery : new DeliveryProxy(delivery);
    }

    private static Product requireProduct(Warehouse warehouse, long id) {
        Product product = warehouse.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        return product;
    }

    public record Line(long productId, int quantity) {}
}

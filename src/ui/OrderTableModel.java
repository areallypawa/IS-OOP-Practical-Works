package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;
import delivery.Delivery;
import order.Order;
import order.OrderManager;

public class OrderTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
        "ID", "Customer", "Status", "Items", "Delivery", "Discounts", "Total"
    };

    private final OrderManager orderManager;
    private final List<Order> rows = new ArrayList<>();

    public OrderTableModel(OrderManager orderManager) {
        if (orderManager == null) {
            throw new IllegalArgumentException("Order manager cannot be null");
        }

        this.orderManager = orderManager;
        refresh();
    }

    public void refresh() {
        rows.clear();
        rows.addAll(orderManager.getOrders());
        fireTableDataChanged();
    }

    public Order getOrderAt(int rowIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return COLUMNS[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Long.class;
            case 3 -> Integer.class;
            case 6 -> Double.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Order order = rows.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> order.getId();
            case 1 -> order.getCustomer().getName();
            case 2 -> order.getStatus().toString();
            case 3 -> order.getItems().size();
            case 4 -> describeDelivery(order.getDelivery());
            case 5 -> order.getDiscounts().isEmpty()
                ? "—"
                : order.getDiscounts().stream()
                    .map(discount -> readableName(discount.getClass().getSimpleName()))
                    .collect(Collectors.joining(", "));
            case 6 -> order.getTotalPrice();
            default -> throw new IllegalArgumentException("Unknown column: " + columnIndex);
        };
    }

    private static String describeDelivery(Delivery delivery) {
        if (delivery == null) {
            return "—";
        }

        String estimatedTime = delivery.getEstimatedTime();
        String name = switch (estimatedTime) {
            case "1-2 days" -> "Courier";
            case "1-3 days" -> "Pickup";
            case "5-10 days" -> "Post";
            default -> readableName(delivery.getClass().getSimpleName());
        };
        return name + " (" + estimatedTime + ")";
    }

    private static String readableName(String className) {
        return className
            .replace("DeliveryProxy", "Delivery")
            .replace("Delivery", " delivery")
            .replace("Discount", "")
            .trim();
    }
}

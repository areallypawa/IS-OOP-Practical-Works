package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import customer.Customer;
import shop.ShopCommand;
import shop.ShopFacade;
import shop.ShopFacade.DeliveryType;
import shop.ShopFacade.DiscountType;
import shop.ShopFacade.Promotion;
import order.Order;
import order.OrderItem;
import order.OrderMediator.Line;
import order.OrderStatus;
import product.Product;

public class OrderPanel extends JPanel {
    private static final String[] DELIVERY_TYPES = {"Courier", "Pickup", "Post"};
    private static final String[] DISCOUNT_TYPES = {
        "Percent", "Cashback", "Free delivery", "Gift"
    };

    private final ShopFacade shop;
    private final Supplier<String> warehouseSupplier;
    private final Runnable warehouseUpdateCallback;
    private final JComboBox<CustomerChoice> customerBox = new JComboBox<>();
    private final OrderTableModel tableModel = new OrderTableModel();
    private final JTable orderTable = new JTable(tableModel);
    private final JLabel summaryLabel = new JLabel();

    public OrderPanel(
        ShopFacade shop,
        Supplier<String> warehouseSupplier,
        Runnable warehouseUpdateCallback
    ) {
        if (warehouseSupplier == null) {
            throw new IllegalArgumentException("Warehouse supplier cannot be null");
        }
        if (warehouseUpdateCallback == null) {
            throw new IllegalArgumentException("Warehouse update callback cannot be null");
        }

        this.shop = shop;
        this.warehouseSupplier = warehouseSupplier;
        this.warehouseUpdateCallback = warehouseUpdateCallback;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(new JScrollPane(orderTable), BorderLayout.CENTER);
        add(createActionsPanel(), BorderLayout.SOUTH);

        orderTable.setFillsViewportHeight(true);
        orderTable.setAutoCreateRowSorter(true);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setToolTipText("Double-click an order to view all details");
        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && orderTable.getSelectedRow() >= 0) {
                    runSafely(OrderPanel.this::showSelectedOrderDetails);
                }
            }
        });

        refreshFromShop();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton createCustomerButton = new JButton("New customer");
        JButton createOrderButton = new JButton("Create order");

        createCustomerButton.addActionListener(event -> runSafely(this::createCustomer));
        createOrderButton.addActionListener(event -> runSafely(this::createOrder));

        customerPanel.add(new JLabel("Customer:"));
        customerPanel.add(customerBox);
        customerPanel.add(createCustomerButton);
        customerPanel.add(createOrderButton);
        panel.add(customerPanel, BorderLayout.CENTER);
        panel.add(summaryLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Add item");
        JButton removeItemButton = new JButton("Remove item");
        JButton deliveryButton = new JButton("Assign delivery");
        JButton discountButton = new JButton("Apply discount");
        JButton statusButton = new JButton("Change status");
        JButton detailsButton = new JButton("Show details");

        addItemButton.addActionListener(event -> runSafely(this::addItem));
        removeItemButton.addActionListener(event -> runSafely(this::removeItem));
        deliveryButton.addActionListener(event -> runSafely(this::assignDelivery));
        discountButton.addActionListener(event -> runSafely(this::applyDiscount));
        statusButton.addActionListener(event -> runSafely(this::changeStatus));
        detailsButton.addActionListener(event -> runSafely(this::showSelectedOrderDetails));

        panel.add(addItemButton);
        panel.add(removeItemButton);
        panel.add(deliveryButton);
        panel.add(discountButton);
        panel.add(statusButton);
        panel.add(detailsButton);
        return panel;
    }

    private void createCustomer() {
        JTextField nameField = new JTextField(18);
        JTextField regionField = new JTextField(18);
        JPanel fields = new JPanel(new GridLayout(0, 2, 6, 6));
        fields.add(new JLabel("Name:"));
        fields.add(nameField);
        fields.add(new JLabel("Region:"));
        fields.add(regionField);

        int result = JOptionPane.showConfirmDialog(
            this,
            fields,
            "New customer",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText();
        String region = regionField.getText();
        Customer customer = shop.execute(api -> api.addCustomer(name, region));
        refreshFromShop();
        customerBox.setSelectedItem(new CustomerChoice(customer));
    }

    private void createOrder() {
        Customer customer = getSelectedCustomer();
        Product product = chooseProduct("Select first order item");
        if (product == null) {
            return;
        }

        Integer quantity = chooseQuantity(product);
        if (quantity == null) {
            return;
        }

        String warehouse = currentWarehouse();
        Line line = new Line(product.getId(), quantity);
        Order order = shop.execute(api -> api.createOrder(warehouse, customer.getId(), List.of(line), null, null));
        refreshAfterChange(true);
        selectOrder(order);
    }

    private void addItem() {
        Order order = getSelectedOrder();
        requirePending(order);
        Product product = chooseProduct("Select product to add");
        if (product == null) {
            return;
        }

        Integer quantity = chooseQuantity(product);
        if (quantity == null) {
            return;
        }

        String warehouse = currentWarehouse();
        Line line = new Line(product.getId(), quantity);
        shop.execute(ShopCommand.action(api -> api.addItem(warehouse, order.getId(), line)));
        refreshAfterChange(true);
    }

    private void removeItem() {
        Order order = getSelectedOrder();
        requirePending(order);
        if (order.getItems().size() == 1) {
            throw new IllegalStateException("An order must contain at least one item");
        }

        OrderItemChoice choice = (OrderItemChoice) JOptionPane.showInputDialog(
            this,
            "Item to remove:",
            "Remove item",
            JOptionPane.PLAIN_MESSAGE,
            null,
            order.getItems().stream().map(OrderItemChoice::new).toArray(),
            null
        );
        if (choice == null) {
            return;
        }

        OrderItem item = choice.item();
        shop.execute(ShopCommand.action(api -> api.removeItem(order.getId(), item)));
        refreshAfterChange(true);
    }

    private void assignDelivery() {
        Order order = getSelectedOrder();
        String type = chooseOption("Delivery type:", "Assign delivery", DELIVERY_TYPES);
        if (type == null) {
            return;
        }

        DeliveryType delivery = switch (type) {
            case "Courier" -> DeliveryType.COURIER;
            case "Pickup" -> DeliveryType.PICKUP;
            case "Post" -> DeliveryType.POST;
            default -> throw new IllegalStateException("Unknown delivery type: " + type);
        };
        shop.execute(ShopCommand.action(api -> api.assignDelivery(order.getId(), delivery)));
        refreshView();
    }

    private void applyDiscount() {
        Order order = getSelectedOrder();
        String type = chooseOption("Discount type:", "Apply discount", DISCOUNT_TYPES);
        if (type == null) {
            return;
        }

        Promotion promotion = switch (type) {
            case "Percent" -> new Promotion(DiscountType.PERCENT, 0);
            case "Cashback" -> new Promotion(DiscountType.CASHBACK, 0);
            case "Free delivery" -> new Promotion(DiscountType.FREE_DELIVERY, 0);
            case "Gift" -> {
                Product gift = chooseProduct("Select gift product");
                yield gift == null ? null : new Promotion(DiscountType.GIFT, gift.getId());
            }
            default -> throw new IllegalStateException("Unknown discount type: " + type);
        };
        if (promotion == null) {
            return;
        }

        String warehouse = currentWarehouse();
        shop.execute(ShopCommand.action(api -> api.applyDiscount(warehouse, order.getId(), promotion)));
        refreshView();
    }

    private void changeStatus() {
        Order order = getSelectedOrder();
        OrderStatus status = (OrderStatus) JOptionPane.showInputDialog(
            this,
            "New status:",
            "Change status",
            JOptionPane.PLAIN_MESSAGE,
            null,
            OrderStatus.values(),
            order.getStatus()
        );
        if (status == null) {
            return;
        }

        long id = order.getId();
        shop.execute(ShopCommand.action(switch (status) {
            case PAID -> api -> api.confirmPayment(id);
            case SHIPPED -> api -> api.ship(id);
            case CANCELLED -> api -> api.cancel(id);
            case DELIVERED -> api -> api.confirmDelivery(id);
            case PENDING -> throw new IllegalArgumentException("Cannot change status to PENDING");
        }));
        refreshAfterChange(status == OrderStatus.CANCELLED);
    }

    private void showSelectedOrderDetails() {
        Order order = getSelectedOrder();
        JTextArea textArea = new JTextArea(buildDetails(order), 22, 65);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCaretPosition(0);

        JOptionPane.showMessageDialog(
            this,
            textArea,
            "Order " + order.getId() + " details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String buildDetails(Order order) {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(order.getId())
            .append("\nCustomer: ").append(order.getCustomer().getName())
            .append("\nRegion: ").append(order.getCustomer().getRegion())
            .append("\nBonus balance: ").append(formatMoney(order.getCustomer().getBonusBalance()))
            .append("\nStatus: ").append(order.getStatus())
            .append("\n\nItems:\n");

        for (OrderItem item : order.getItems()) {
            details.append("• ").append(item.getProductName())
                .append(" (ID ").append(item.getProductId()).append(")")
                .append(" — ").append(item.getQuantity()).append(" × ")
                .append(formatMoney(item.getUnitPrice()))
                .append(" = ").append(formatMoney(item.getTotalPrice()))
                .append('\n');
        }

        details.append("\nTotal weight: ").append(order.getTotalWeight()).append(" kg")
            .append("\nDelivery: ");
        if (order.getDelivery() == null) {
            details.append("not assigned");
        } else {
            details.append(order.getDelivery().getEstimatedTime())
                .append(", cost ").append(formatMoney(order.getDeliveryCost()));
        }

        details.append("\nDiscounts: ");
        if (order.getDiscounts().isEmpty()) {
            details.append("none");
        } else {
            details.append(order.getDiscounts().stream()
                .map(discount -> discount.getClass().getSimpleName())
                .reduce((left, right) -> left + ", " + right)
                .orElse("none"));
        }

        if (!order.getGiftProducts().isEmpty()) {
            details.append("\nGifts: ").append(order.getGiftProducts().stream()
                .map(Product::getName)
                .reduce((left, right) -> left + ", " + right)
                .orElse("none"));
        }
        details.append("\nTotal: ").append(formatMoney(order.getTotalPrice()));
        return details.toString();
    }

    private Product chooseProduct(String title) {
        List<ProductChoice> choices = new ArrayList<>();
        String warehouse = currentWarehouse();
        for (Product product : shop.execute(api -> api.products(warehouse))) {
            if (product.getQuantity() > 0) {
                choices.add(new ProductChoice(product));
            }
        }
        if (choices.isEmpty()) {
            throw new IllegalStateException("The current warehouse has no products in stock");
        }

        ProductChoice choice = (ProductChoice) JOptionPane.showInputDialog(
            this,
            "Product:",
            title,
            JOptionPane.PLAIN_MESSAGE,
            null,
            choices.toArray(),
            choices.get(0)
        );
        return choice == null ? null : choice.product();
    }

    private Integer chooseQuantity(Product product) {
        JSpinner quantitySpinner = new JSpinner(
            new SpinnerNumberModel(1, 1, product.getQuantity(), 1)
        );
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) quantitySpinner.getEditor();
        editor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        quantitySpinner.setPreferredSize(new Dimension(120, quantitySpinner.getPreferredSize().height));

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        quantityPanel.add(quantitySpinner);

        int result = JOptionPane.showConfirmDialog(
            this,
            quantityPanel,
            "Quantity (available: " + product.getQuantity() + ")",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        return result == JOptionPane.OK_OPTION
            ? (Integer) quantitySpinner.getValue()
            : null;
    }

    private String chooseOption(String message, String title, String[] options) {
        return (String) JOptionPane.showInputDialog(
            this,
            message,
            title,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
    }

    private Customer getSelectedCustomer() {
        CustomerChoice choice = (CustomerChoice) customerBox.getSelectedItem();
        if (choice == null) {
            throw new IllegalStateException("Create or select a customer first");
        }
        return choice.customer();
    }

    private Order getSelectedOrder() {
        int viewRow = orderTable.getSelectedRow();
        if (viewRow < 0) {
            throw new IllegalStateException("Select an order in the table first");
        }

        return tableModel.getOrderAt(orderTable.convertRowIndexToModel(viewRow));
    }

    private String currentWarehouse() {
        String warehouse = warehouseSupplier.get();
        if (warehouse == null) {
            throw new IllegalStateException("No warehouse selected");
        }
        return warehouse;
    }

    private void requirePending(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only a pending order can be edited");
        }
    }

    void refreshFromShop() {
        Object selected = customerBox.getSelectedItem();
        customerBox.removeAllItems();
        shop.execute(ShopFacade::customers).forEach(customer -> customerBox.addItem(new CustomerChoice(customer)));
        if (selected != null) {
            customerBox.setSelectedItem(selected);
        }
        refreshView();
    }

    private void selectOrder(Order order) {
        for (int modelRow = 0; modelRow < tableModel.getRowCount(); modelRow++) {
            if (tableModel.getOrderAt(modelRow) == order) {
                int viewRow = orderTable.convertRowIndexToView(modelRow);
                orderTable.setRowSelectionInterval(viewRow, viewRow);
                orderTable.scrollRectToVisible(orderTable.getCellRect(viewRow, 0, true));
                return;
            }
        }
    }

    private void refreshAfterChange(boolean warehouseChanged) {
        refreshView();
        if (warehouseChanged) {
            warehouseUpdateCallback.run();
        }
    }

    private void refreshView() {
        tableModel.setOrders(shop.execute(ShopFacade::orders));
        var stats = shop.execute(ShopFacade::statistics);
        summaryLabel.setText(
            "Customers with orders: " + stats.orderingCustomers()
                + " | Orders: " + stats.orders()
                + " | Net turnover: " + formatMoney(stats.turnover())
        );
    }

    private static String formatMoney(double value) {
        return String.format("%.2f USD", value);
    }

    private void runSafely(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            JOptionPane.showMessageDialog(
                this,
                message == null || message.isBlank()
                    ? exception.getClass().getSimpleName()
                    : message,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private record CustomerChoice(Customer customer) {
        @Override
        public String toString() {
            return customer.getName() + " — " + customer.getRegion();
        }
    }

    private record ProductChoice(Product product) {
        @Override
        public String toString() {
            return product.getName() + " (ID " + product.getId()
                + ", price " + formatMoney(product.getPrice())
                + ", stock " + product.getQuantity() + ")";
        }
    }

    private record OrderItemChoice(OrderItem item) {
        @Override
        public String toString() {
            return item.getProductName() + " — " + item.getQuantity()
                + " × " + formatMoney(item.getUnitPrice());
        }
    }
}

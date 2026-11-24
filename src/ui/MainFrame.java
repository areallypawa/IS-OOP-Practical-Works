package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import product.Product;
import product.attribute.Price;
import shop.ShopFacade;
import shop.ShopCommand;

public class MainFrame extends JFrame {
    private final ShopFacade shop = ShopFacade.demo();
    private final JComboBox<String> warehouseBox = new JComboBox<>();
    private final ProductTableModel tableModel;
    private final JTable productTable;
    private final JLabel statusLabel = new JLabel();

    public MainFrame() {
        super("Object‑oriented programming");

        shop.execute(ShopFacade::warehouseNames).forEach(warehouseBox::addItem);
        tableModel = new ProductTableModel();
        productTable = new JTable(tableModel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        CardLayout contentLayout = new CardLayout();
        JPanel contentPanel = new JPanel(contentLayout);
        contentPanel.add(createWarehousePanel(), "warehouse");
        contentPanel.add(
            new OrderPanel(shop, this::currentWarehouse, this::updateView),
            "orders"
        );

        add(
            createNavigationPanel(contentLayout, contentPanel),
            BorderLayout.NORTH
        );
        add(contentPanel, BorderLayout.CENTER);

        productTable.setFillsViewportHeight(true);
        productTable.setAutoCreateRowSorter(true);
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && productTable.getSelectedRow() >= 0) {
                    runSafely(MainFrame.this::showSelectedProductDetails);
                }
            }
        });

        warehouseBox.addActionListener(event -> updateView());
        updateView();
    }

    private JPanel createNavigationPanel(
        CardLayout contentLayout,
        JPanel contentPanel
    ) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JToggleButton warehouseButton = new JToggleButton("Склад");
        JToggleButton ordersButton = new JToggleButton("Заказы");
        ButtonGroup navigationGroup = new ButtonGroup();

        navigationGroup.add(warehouseButton);
        navigationGroup.add(ordersButton);
        warehouseButton.setSelected(true);

        warehouseButton.addActionListener(event ->
            contentLayout.show(contentPanel, "warehouse")
        );
        ordersButton.addActionListener(event ->
            contentLayout.show(contentPanel, "orders")
        );

        panel.add(warehouseButton);
        panel.add(ordersButton);
        return panel;
    }

    private JPanel createWarehousePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(createHeaderPanel(), BorderLayout.NORTH);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(createProductActionsPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel container = new JPanel(new BorderLayout());
        JPanel warehousePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton createButton = new JButton("New warehouse");
        JButton copyButton = new JButton("Copy warehouse");
        JButton moveAllButton = new JButton("Move all products");

        createButton.addActionListener(event -> runSafely(this::createWarehouse));
        copyButton.addActionListener(event -> runSafely(this::copyWarehouse));
        moveAllButton.addActionListener(event -> runSafely(this::moveAllProducts));

        warehousePanel.add(new JLabel("Current warehouse:"));
        warehousePanel.add(warehouseBox);
        warehousePanel.add(createButton);
        warehousePanel.add(copyButton);
        warehousePanel.add(moveAllButton);

        container.add(warehousePanel, BorderLayout.CENTER);
        container.add(statusLabel, BorderLayout.SOUTH);
        return container;
    }

    private JPanel createProductActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add product");
        JButton deleteButton = new JButton("Delete selected");
        JButton priceButton = new JButton("Change price");
        JButton quantityButton = new JButton("Change quantity");
        JButton transferButton = new JButton("Transfer selected");
        JButton detailsButton = new JButton("Show details");
        JButton searchButton = new JButton("Search");

        addButton.addActionListener(event -> runSafely(this::addProduct));
        deleteButton.addActionListener(event -> runSafely(this::deleteSelectedProduct));
        priceButton.addActionListener(event -> runSafely(this::changeSelectedPrice));
        quantityButton.addActionListener(event -> runSafely(this::changeSelectedQuantity));
        transferButton.addActionListener(event -> runSafely(this::transferSelectedProduct));
        detailsButton.addActionListener(event -> runSafely(this::showSelectedProductDetails));
        searchButton.addActionListener(event -> runSafely(this::searchProducts));

        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(priceButton);
        panel.add(quantityButton);
        panel.add(transferButton);
        panel.add(detailsButton);
        panel.add(searchButton);
        return panel;
    }

    private void createWarehouse() {
        String name = JOptionPane.showInputDialog(this, "Warehouse name:");
        if (name == null) {
            return;
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Warehouse name cannot be empty");
        }

        String sizeInput = JOptionPane.showInputDialog(this, "Maximum product count:", "100");
        if (sizeInput == null) {
            return;
        }

        int capacity = Integer.parseInt(sizeInput.trim());
        addWarehouse(shop.execute(api -> api.createWarehouse(name, capacity)));
    }

    private void copyWarehouse() {
        String source = currentWarehouse();
        String name = JOptionPane.showInputDialog(
            this,
            "Name for warehouse copy:",
            source + " copy"
        );

        if (name == null) {
            return;
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Warehouse name cannot be empty");
        }

        addWarehouse(shop.execute(api -> api.copyWarehouse(source, name)));
    }

    private void moveAllProducts() {
        String source = currentWarehouse();
        String target = chooseOtherWarehouse("Move all products to:");

        if (target == null) {
            return;
        }

        shop.execute(ShopCommand.action(api -> api.transferAll(source, target)));
        updateView();
    }

    private void addProduct() {
        Product product = ProductDialog.showDialog(this, shop, currentWarehouse());
        if (product == null) {
            return;
        }

        updateView();
    }

    private void deleteSelectedProduct() {
        Product product = getSelectedProduct();
        String warehouse = currentWarehouse();
        shop.execute(ShopCommand.action(api -> api.removeProduct(warehouse, product.getId())));
        updateView();
    }

    private void changeSelectedPrice() {
        Product product = getSelectedProduct();
        String input = JOptionPane.showInputDialog(
            this,
            "New price (USD):",
            product.getPrice()
        );

        if (input == null) {
            return;
        }

        String warehouse = currentWarehouse();
        Price price = new Price(Double.parseDouble(input.trim()));
        shop.execute(ShopCommand.action(api -> api.changePrice(warehouse, product.getId(), price)));
        updateView();
    }

    private void changeSelectedQuantity() {
        Product product = getSelectedProduct();
        String input = JOptionPane.showInputDialog(
            this,
            "Quantity change (for example, 5 or -2):",
            "0"
        );

        if (input == null) {
            return;
        }

        String warehouse = currentWarehouse();
        int delta = Integer.parseInt(input.trim());
        shop.execute(ShopCommand.action(api -> api.changeStock(warehouse, product.getId(), delta)));
        updateView();
    }

    private void transferSelectedProduct() {
        Product product = getSelectedProduct();
        String target = chooseOtherWarehouse("Transfer product to:");

        if (target == null) {
            return;
        }

        String source = currentWarehouse();
        shop.execute(ShopCommand.action(api -> api.transferProduct(source, target, product.getId())));
        updateView();
    }

    private void showSelectedProductDetails() {
        Product product = getSelectedProduct();
        JTextArea textArea = new JTextArea(product.getDetails(), 20, 60);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCaretPosition(0);

        JOptionPane.showMessageDialog(
            this,
            textArea,
            product.getClass().getSimpleName() + " details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void searchProducts() {
        String[] searchTypes = {"ID", "Category", "Price"};
        String searchType = (String) JOptionPane.showInputDialog(
            this,
            "Search by:",
            "Search products",
            JOptionPane.PLAIN_MESSAGE,
            null,
            searchTypes,
            searchTypes[0]
        );

        if (searchType == null) {
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Search value:");
        if (input == null) {
            return;
        }

        String warehouse = currentWarehouse();
        List<Product> results = switch (searchType) {
            case "ID" -> shop.execute(api -> List.of(api.product(warehouse, Long.parseLong(input.trim()))));
            case "Category" -> shop.execute(api -> api.productsByCategory(warehouse, input.trim()));
            case "Price" -> shop.execute(api -> api.productsByPrice(warehouse, new Price(Double.parseDouble(input.trim()))));
            default -> throw new IllegalStateException("Unknown search type: " + searchType);
        };

        showSearchResults(results);
    }

    private void showSearchResults(List<Product> products) {
        JTextArea textArea = new JTextArea(12, 60);
        textArea.setEditable(false);

        if (products.isEmpty()) {
            textArea.setText("No products found");
        } else {
            StringBuilder result = new StringBuilder();
            for (Product product : products) {
                result.append("ID: ").append(product.getId())
                    .append(", name: ").append(product.getName())
                    .append(", type: ").append(product.getClass().getSimpleName())
                    .append(", category: ").append(product.getCategory())
                    .append(", price: ").append(product.getPrice())
                    .append(", quantity: ").append(product.getQuantity())
                    .append('\n');
            }
            textArea.setText(result.toString());
        }

        JOptionPane.showMessageDialog(
            this,
            new JScrollPane(textArea),
            "Search results",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private Product getSelectedProduct() {
        int viewRow = productTable.getSelectedRow();

        if (viewRow < 0) {
            throw new IllegalStateException("Select a product in the table first");
        }

        int modelRow = productTable.convertRowIndexToModel(viewRow);
        return tableModel.getProductAt(modelRow);
    }

    private String chooseOtherWarehouse(String title) {
        String current = currentWarehouse();
        String[] options = shop.execute(ShopFacade::warehouseNames).stream()
            .filter(name -> !name.equals(current))
            .toArray(String[]::new);

        if (options.length == 0) {
            throw new IllegalStateException("Create another warehouse first");
        }

        return (String) JOptionPane.showInputDialog(
            this,
            title,
            "Select warehouse",
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
    }

    private void addWarehouse(String name) {
        warehouseBox.addItem(name);
        warehouseBox.setSelectedItem(name);
    }

    private String currentWarehouse() {
        return (String) warehouseBox.getSelectedItem();
    }

    private void updateView() {
        String warehouse = currentWarehouse();
        if (warehouse != null) {
            tableModel.setProducts(shop.execute(api -> api.products(warehouse)));
            var info = shop.execute(api -> api.warehouseInfo(warehouse));
            statusLabel.setText("Products: " + info.size() + " / " + info.capacity());
        }
    }

    private void runSafely(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

}

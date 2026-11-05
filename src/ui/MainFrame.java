package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import inventory.Warehouse;
import product.Product;
import product.attribute.Price;

public class MainFrame extends JFrame {
    private final List<WarehouseEntry> warehouses = new ArrayList<>();
    private final JComboBox<WarehouseEntry> warehouseBox = new JComboBox<>();
    private final ProductTableModel tableModel;
    private final JTable productTable;
    private final JLabel statusLabel = new JLabel();

    public MainFrame() {
        super("Warehouse Manager");

        WarehouseEntry initialWarehouse = new WarehouseEntry(
            "Warehouse 1",
            new Warehouse(100)
        );
        warehouses.add(initialWarehouse);
        warehouseBox.addItem(initialWarehouse);

        tableModel = new ProductTableModel(initialWarehouse.warehouse());
        productTable = new JTable(tableModel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(new JScrollPane(productTable), BorderLayout.CENTER);
        add(createProductActionsPanel(), BorderLayout.SOUTH);

        productTable.setFillsViewportHeight(true);
        productTable.setAutoCreateRowSorter(true);
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        warehouseBox.addActionListener(event -> switchWarehouse());
        updateView();
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
        JButton searchButton = new JButton("Search");

        addButton.addActionListener(event -> runSafely(this::addProduct));
        deleteButton.addActionListener(event -> runSafely(this::deleteSelectedProduct));
        priceButton.addActionListener(event -> runSafely(this::changeSelectedPrice));
        quantityButton.addActionListener(event -> runSafely(this::changeSelectedQuantity));
        transferButton.addActionListener(event -> runSafely(this::transferSelectedProduct));
        searchButton.addActionListener(event -> runSafely(this::searchProducts));

        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(priceButton);
        panel.add(quantityButton);
        panel.add(transferButton);
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

        addWarehouse(new WarehouseEntry(
            name.trim(),
            new Warehouse(Integer.parseInt(sizeInput.trim()))
        ));
    }

    private void copyWarehouse() {
        WarehouseEntry source = currentEntry();
        String name = JOptionPane.showInputDialog(
            this,
            "Name for warehouse copy:",
            source.name() + " copy"
        );

        if (name == null) {
            return;
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Warehouse name cannot be empty");
        }

        addWarehouse(new WarehouseEntry(
            name.trim(),
            new Warehouse(source.warehouse())
        ));
    }

    private void moveAllProducts() {
        WarehouseEntry source = currentEntry();
        WarehouseEntry target = chooseOtherWarehouse("Move all products to:");

        if (target == null) {
            return;
        }

        target.warehouse().transferFrom(source.warehouse());
        updateView();
    }

    private void addProduct() {
        Product product = ProductDialog.showDialog(this);
        if (product == null) {
            return;
        }

        currentWarehouse().addProduct(product);
        updateView();
    }

    private void deleteSelectedProduct() {
        Product product = getSelectedProduct();
        currentWarehouse().removeById(product.getId());
        updateView();
    }

    private void changeSelectedPrice() {
        Product product = getSelectedProduct();
        String input = JOptionPane.showInputDialog(
            this,
            "New price:",
            product.getPrice()
        );

        if (input == null) {
            return;
        }

        product.setPrice(new Price(Double.parseDouble(input.trim())));
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

        product.changeQuantity(Integer.parseInt(input.trim()));
        updateView();
    }

    private void transferSelectedProduct() {
        Product product = getSelectedProduct();
        WarehouseEntry target = chooseOtherWarehouse("Transfer product to:");

        if (target == null) {
            return;
        }

        product.transferTo(target.warehouse());
        updateView();
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

        List<Product> results = switch (searchType) {
            case "ID" -> {
                Product product = currentWarehouse().findById(Long.parseLong(input.trim()));
                yield product == null ? List.of() : List.of(product);
            }
            case "Category" -> currentWarehouse().findByCategory(input.trim());
            case "Price" -> currentWarehouse().findByPrice(Double.parseDouble(input.trim()));
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

    private WarehouseEntry chooseOtherWarehouse(String title) {
        WarehouseEntry current = currentEntry();
        WarehouseEntry[] options = warehouses.stream()
            .filter(entry -> entry != current)
            .toArray(WarehouseEntry[]::new);

        if (options.length == 0) {
            throw new IllegalStateException("Create another warehouse first");
        }

        return (WarehouseEntry) JOptionPane.showInputDialog(
            this,
            title,
            "Select warehouse",
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
    }

    private void addWarehouse(WarehouseEntry entry) {
        warehouses.add(entry);
        warehouseBox.addItem(entry);
        warehouseBox.setSelectedItem(entry);
    }

    private void switchWarehouse() {
        WarehouseEntry entry = (WarehouseEntry) warehouseBox.getSelectedItem();
        if (entry == null) {
            return;
        }

        tableModel.setWarehouse(entry.warehouse());
        updateView();
    }

    private WarehouseEntry currentEntry() {
        WarehouseEntry entry = (WarehouseEntry) warehouseBox.getSelectedItem();
        if (entry == null) {
            throw new IllegalStateException("No warehouse selected");
        }
        return entry;
    }

    private Warehouse currentWarehouse() {
        return currentEntry().warehouse();
    }

    private void updateView() {
        tableModel.refresh();
        WarehouseEntry current = currentEntry();
        statusLabel.setText(
            "Warehouse: " + current.name()
                + " | Products: " + current.warehouse().size()
                + " / " + current.warehouse().getMaxSize()
        );
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

    private record WarehouseEntry(String name, Warehouse warehouse) {
        @Override
        public String toString() {
            return name;
        }
    }
}

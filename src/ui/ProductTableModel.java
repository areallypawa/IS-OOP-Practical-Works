package ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import inventory.Warehouse;
import product.Product;

public class ProductTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
        "ID", "Name", "Category", "Price", "Quantity", "Weight", "Type"
    };

    private Warehouse warehouse;
    private final List<Product> rows = new ArrayList<>();

    public ProductTableModel(Warehouse warehouse) {
        this.warehouse = warehouse;
        refresh();
    }

    public void setWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse cannot be null");
        }

        this.warehouse = warehouse;
        refresh();
    }

    public void refresh() {
        rows.clear();

        for (Product product : warehouse) {
            rows.add(product);
        }

        fireTableDataChanged();
    }

    public Product getProductAt(int rowIndex) {
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product product = rows.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> product.getId();
            case 1 -> product.getName();
            case 2 -> product.getCategory();
            case 3 -> product.getPrice();
            case 4 -> product.getQuantity();
            case 5 -> product.getWeight();
            case 6 -> product.getClass().getSimpleName();
            default -> throw new IllegalArgumentException("Unknown column: " + columnIndex);
        };
    }
}

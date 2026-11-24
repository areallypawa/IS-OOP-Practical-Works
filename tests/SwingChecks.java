import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.util.function.Predicate;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import ui.MainFrame;

public final class SwingChecks {
    public static void main(String[] args) throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Swing checks skipped: display not available");
            return;
        }
        SwingUtilities.invokeAndWait(() -> {
            MainFrame window = new MainFrame();
            try {
                component(window, JToggleButton.class, button -> button.getText().equals("Команды")).doClick();
                JTextField input = component(window, JTextField.class, field -> true);
                JTable products = component(window, JTable.class, table -> table.getColumnName(1).equals("Name"));
                JTable orders = component(window, JTable.class, table -> table.getColumnName(1).equals("Customer"));
                submit(input, "product add main ScoutDrone \"UI product\" 12.00 3 1");
                check(products.getRowCount() == 1 && products.getValueAt(0, 1).equals("UI product"), "Product table refresh");
                Object id = products.getValueAt(0, 0);
                submit(input, "order create main 1 pickup none " + id + ":2");
                check(orders.getRowCount() == 1 && orders.getValueAt(0, 2).equals("PENDING"), "Order table refresh");
                check(products.getValueAt(0, 4).equals(1), "Order stock visible in warehouse table");
                submit(input, "order cancel " + orders.getValueAt(0, 0));
                check(products.getValueAt(0, 4).equals(3), "Returned stock visible in warehouse table");
            } finally {
                window.dispose();
            }
        });
        System.out.println("Swing: all checks passed");
    }

    private static void submit(JTextField input, String command) {
        input.setText(command);
        input.postActionEvent();
    }

    private static <T extends Component> T component(Component root, Class<T> type, Predicate<T> matches) {
        T match = find(root, type, matches);
        if (match == null) {
            throw new AssertionError("Missing Swing component: " + type.getSimpleName());
        }
        return match;
    }

    private static <T extends Component> T find(Component root, Class<T> type, Predicate<T> matches) {
        if (type.isInstance(root) && matches.test(type.cast(root))) {
            return type.cast(root);
        }
        if (root instanceof Container container) {
            for (Component child : container.getComponents()) {
                T match = find(child, type, matches);
                if (match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}

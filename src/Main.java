import command.ShopConsole;
import java.io.IOException;
import javax.swing.SwingUtilities;
import shop.ShopFacade;
import ui.MainFrame;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 1 && args[0].equals("--cli")) {
            new ShopConsole(ShopFacade.demo()).run();
            return;
        }
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

package ui;

import command.ShopConsole;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

final class CommandPanel extends JPanel {
    private final JTextArea output = new JTextArea();
    private final JTextField input = new JTextField();
    private final ShopConsole console;
    private final Runnable updateViews;

    CommandPanel(ShopConsole console, Runnable updateViews) {
        super(new BorderLayout(8, 8));
        this.console = console;
        this.updateViews = updateViews;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setText("Enter help to see the commands.\n");
        add(new JScrollPane(output), BorderLayout.CENTER);

        JButton execute = new JButton("Run");
        execute.addActionListener(event -> submit());
        input.addActionListener(event -> submit());
        JPanel prompt = new JPanel(new BorderLayout(8, 0));
        prompt.add(input, BorderLayout.CENTER);
        prompt.add(execute, BorderLayout.EAST);
        add(prompt, BorderLayout.SOUTH);
    }

    private void submit() {
        String line = input.getText();
        if (line.isBlank()) {
            return;
        }
        input.setText("");
        output.append("\n> " + line + "\n");
        try {
            output.append(console.execute(line) + "\n");
            updateViews.run();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            output.append("Error: " + exception.getMessage() + "\n");
        }
        output.setCaretPosition(output.getDocument().getLength());
        input.requestFocusInWindow();
    }
}

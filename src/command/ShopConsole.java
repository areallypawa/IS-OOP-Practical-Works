package command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import shop.ShopCommand;
import shop.ShopFacade;

public final class ShopConsole {
    private final CommandHandler first = new ProductHandler(new OrderHandler(new InfoHandler()));
    private final ShopFacade shop;

    public ShopConsole(ShopFacade shop) {
        this.shop = shop;
    }

    public String execute(String input) {
        return shop.execute(parse(input));
    }

    public ShopCommand<String> parse(String input) {
        List<String> words = tokenize(input);
        if (words.isEmpty()) {
            throw CommandHandler.invalid("enter a command or help");
        }
        return first.parse(words);
    }

    public void run() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("Shop commands. Type help or exit.");
        while (true) {
            System.out.print("> ");
            System.out.flush();
            String input = reader.readLine();
            if (input == null || input.strip().equals("exit")) {
                return;
            }
            if (!input.isBlank()) {
                try {
                    System.out.println(execute(input));
                } catch (IllegalArgumentException | IllegalStateException exception) {
                    System.out.println("Error: " + exception.getMessage());
                }
            }
        }
    }

    private static List<String> tokenize(String input) {
        List<String> words = new ArrayList<>();
        int index = 0;
        while (index < input.length()) {
            if (Character.isWhitespace(input.charAt(index))) {
                index++;
                continue;
            }
            StringBuilder word = new StringBuilder();
            if (input.charAt(index) == '"') {
                index++;
                while (index < input.length() && input.charAt(index) != '"') {
                    char current = input.charAt(index++);
                    if (current == '\\' && index < input.length()
                        && (input.charAt(index) == '"' || input.charAt(index) == '\\')) {
                        current = input.charAt(index++);
                    }
                    word.append(current);
                }
                if (index == input.length()) {
                    throw CommandHandler.invalid("unclosed quote");
                }
                index++;
                if (index < input.length() && !Character.isWhitespace(input.charAt(index))) {
                    throw CommandHandler.invalid("expected a space after the closing quote");
                }
            } else {
                while (index < input.length() && !Character.isWhitespace(input.charAt(index))) {
                    char current = input.charAt(index++);
                    if (current == '"') {
                        throw CommandHandler.invalid("quotes must enclose a whole argument");
                    }
                    word.append(current);
                }
            }
            words.add(word.toString());
        }
        return words;
    }
}

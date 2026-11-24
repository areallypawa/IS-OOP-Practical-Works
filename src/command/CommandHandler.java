package command;

import java.util.List;
import java.util.Set;
import shop.ShopCommand;

abstract class CommandHandler {
    private final Set<String> groups;
    private final CommandHandler next;

    CommandHandler(CommandHandler next, String... groups) {
        this.groups = Set.of(groups);
        this.next = next;
    }

    final ShopCommand<String> parse(List<String> words) {
        if (!groups.contains(words.get(0))) {
            if (next != null) {
                return next.parse(words);
            }
            throw unknown(words.get(0));
        }
        return build(words);
    }

    abstract ShopCommand<String> build(List<String> words);

    static String verb(List<String> words) {
        if (words.size() < 2) {
            throw invalid("missing action after " + words.get(0));
        }
        return words.get(1);
    }

    static void size(List<String> words, int expected, String usage) {
        if (words.size() != expected) {
            throw invalid("use " + usage);
        }
    }

    static long id(String text) {
        try {
            long value = Long.parseLong(text);
            if (value > 0) {
                return value;
            }
        } catch (NumberFormatException ignored) {
            // Report the same argument error for an invalid or out-of-range ID.
        }
        throw invalid("expected a positive ID, got " + text);
    }

    static int integer(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw invalid("expected an integer, got " + text);
        }
    }

    static int quantity(String text, int minimum) {
        int value = integer(text);
        if (value < minimum) {
            throw invalid("expected an integer of at least " + minimum + ", got " + text);
        }
        return value;
    }

    static double decimal(String text) {
        if (!text.matches("[0-9]+(?:\\.[0-9]+)?")) {
            throw invalid("expected a non-negative decimal with a dot, got " + text);
        }
        double value = Double.parseDouble(text);
        if (!Double.isFinite(value)) {
            throw invalid("number out of range: " + text);
        }
        return value;
    }

    static IllegalArgumentException invalid(String message) {
        return new IllegalArgumentException("Invalid arguments: " + message);
    }

    static IllegalArgumentException unknown(String command) {
        return new IllegalArgumentException("Unknown command: " + command + ". Try help.");
    }
}

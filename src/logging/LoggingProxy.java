package logging;

import java.time.Instant;
import java.util.function.Supplier;

public final class LoggingProxy {
    public enum Source {
        ORDER,
        DELIVERY,
        DISCOUNT,
        MANAGER
    }

    private LoggingProxy() {}

    public static void log(Source source, String message) {
        validate(source, message);
        System.out.println("[" + Instant.now() + "] [" + source + "] " + message);
    }

    public static void run(Source source, String operation, Runnable action) {
        if (action == null) {
            throw new IllegalArgumentException("Logged action cannot be null");
        }

        log(source, operation + " started");
        try {
            action.run();
            log(source, operation + " completed");
        } catch (RuntimeException exception) {
            log(source, operation + " failed: " + errorMessage(exception));
            throw exception;
        }
    }

    public static <T> T call(Source source, String operation, Supplier<T> action) {
        if (action == null) {
            throw new IllegalArgumentException("Logged action cannot be null");
        }

        log(source, operation + " started");
        try {
            T result = action.get();
            log(source, operation + " completed");
            return result;
        } catch (RuntimeException exception) {
            log(source, operation + " failed: " + errorMessage(exception));
            throw exception;
        }
    }

    private static void validate(Source source, String message) {
        if (source == null) {
            throw new IllegalArgumentException("Log source cannot be null");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Log message cannot be null or blank");
        }
    }

    private static String errorMessage(RuntimeException exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank()
            ? exception.getClass().getSimpleName()
            : message;
    }
}

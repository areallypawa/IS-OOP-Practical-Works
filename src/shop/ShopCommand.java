package shop;

import java.util.function.Consumer;

@FunctionalInterface
public interface ShopCommand<R> {
    R execute(ShopFacade shop);

    static ShopCommand<Void> action(Consumer<ShopFacade> operation) {
        return shop -> {
            operation.accept(shop);
            return null;
        };
    }
}

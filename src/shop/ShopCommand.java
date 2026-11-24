package shop;

@FunctionalInterface
public interface ShopCommand<R> {
    R execute(ShopFacade shop);
}

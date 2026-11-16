package discount;

import order.Order;
import product.Product;

public final class GiftDiscount implements Discount {
    private final Product giftProduct;

    public GiftDiscount(Product giftProduct) {
        if (giftProduct == null) {
            throw new IllegalArgumentException("Gift product cannot be null");
        }
        this.giftProduct = giftProduct;
    }

    @Override
    public void apply(Order order) {
        validateOrder(order);
        order.addGift(giftProduct);
    }


    private static void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
    }
}

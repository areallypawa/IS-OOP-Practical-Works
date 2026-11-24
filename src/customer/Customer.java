package customer;

import java.math.BigDecimal;
import product.attribute.Price;

public class Customer {
    private static long nextId = 1;
    private final long id;
    private final String name;
    private final String region;
    private BigDecimal bonusBalance = BigDecimal.ZERO;

    public Customer(
        String name,
        String region
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region cannot be null or blank");
        }

        id = nextId++;
        this.name = name;
        this.region = region;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public double getBonusBalance() {
        return bonusBalance.doubleValue();
    }

    public void addBonus(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Bonus amount must be finite and positive");
        }
        bonusBalance = bonusBalance.add(BigDecimal.valueOf(Price.round(amount)));
    }

    public void removeBonus(double amount) {
        BigDecimal updated = bonusBalance.subtract(BigDecimal.valueOf(Price.round(amount)));
        if (updated.signum() < 0) {
            throw new IllegalStateException("Not enough bonus balance to refund cashback");
        }
        bonusBalance = updated;
    }
}

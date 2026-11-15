package customer;

public class Customer {
    private static long nextId = 1;
    private final long id;
    private final String name;
    private final String region;
    private double bonusBalance;

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
        return bonusBalance;
    }

    public void addBonus(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Bonus amount must be finite and positive");
        }
        bonusBalance += amount;
    }
}

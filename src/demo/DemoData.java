package demo;

import factory.ProductFactory;
import factory.artifact.ChronoBatteryFactory;
import factory.artifact.QuantumCoreFactory;
import factory.artifact.SoulProcessorFactory;
import factory.digital.CombatFirmwareFactory;
import factory.digital.GameLicenseFactory;
import factory.digital.MedicalAIFactory;
import factory.digital.NavigationModuleFactory;
import factory.robots.GuardianRobotFactory;
import factory.robots.MedicalRobotFactory;
import factory.robots.ScoutDroneFactory;
import inventory.Warehouse;
import java.util.List;
import product.artifact.Rarity;

public final class DemoData {
    public static final int DEMO_PRODUCT_COUNT = 75;

    private static final List<ProductFactory> FACTORIES = List.of(
        new ChronoBatteryFactory(Rarity.RARE, false, "Chronos Corp", 90, 5000, "GX", 50),
        new QuantumCoreFactory(Rarity.EPIC, false, "Nova Labs", 95, 1000, "GX", 90),
        new SoulProcessorFactory(Rarity.MYTHIC, true, "Astra Guild", 88, "Astra", 70, 85),
        new CombatFirmwareFactory("COMBAT-DEMO", 250, "https://example.com/combat", 4, 90, "1.0"),
        new GameLicenseFactory("GAME-DEMO", 120, "https://example.com/game", "PC", 12, true),
        new MedicalAIFactory("MED-AI-DEMO", 400, "https://example.com/medical", 95, 500, true),
        new NavigationModuleFactory("NAV-DEMO", 300, "https://example.com/navigation", 100, 5000, true),
        new GuardianRobotFactory("Guardian-X", 5000, 85, 80, 4, 90),
        new MedicalRobotFactory("Medic-X", 4500, 90, 95, true, 100),
        new ScoutDroneFactory("Scout-X", 3000, 75, 1000, 60, true)
    );

    private static final String[] NAMES = {
        "Chrono Battery", "Quantum Core", "Soul Processor", "Combat Firmware",
        "Game License", "Medical AI", "Navigation Module", "Guardian Robot",
        "Medical Robot", "Scout Drone"
    };

    private static final double[] PRICES = {
        1250, 4800, 7200, 650, 120, 2400, 900, 15000, 12800, 8400
    };

    private static final double[] WEIGHTS = {
        1.2, 3.5, 0.8, 0.1, 0.1, 0.1, 0.2, 18.0, 14.0, 6.5
    };

    private DemoData() {}

    public static Warehouse createDemoWarehouse() {
        Warehouse warehouse = new Warehouse(100);

        for (int index = 0; index < DEMO_PRODUCT_COUNT; index++) {
            int typeIndex = index % FACTORIES.size();
            int sequence = index / FACTORIES.size() + 1;
            int quantity = 3 + index % 18;

            warehouse.addProduct(FACTORIES.get(typeIndex).createProduct(
                NAMES[typeIndex] + " " + sequence,
                PRICES[typeIndex],
                quantity,
                WEIGHTS[typeIndex]
            ));
        }

        return warehouse;
    }
}

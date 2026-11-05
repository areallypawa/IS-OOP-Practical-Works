package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
import product.Product;
import product.artifact.Rarity;

public final class ProductDialog {
    private static final String[] CATEGORIES = {
        "Artifact", "Digital", "Robot"
    };
    private static final String[] ARTIFACT_TYPES = {
        "ChronoBattery", "QuantumCore", "SoulProcessor"
    };
    private static final String[] DIGITAL_TYPES = {
        "CombatFirmware", "GameLicense", "MedicalAI", "NavigationModule"
    };
    private static final String[] ROBOT_TYPES = {
        "GuardianRobot", "MedicalRobot", "ScoutDrone"
    };

    private final JPanel rootPanel = new JPanel(new BorderLayout(8, 8));
    private final JPanel commonPanel = new JPanel(new GridLayout(0, 2, 8, 8));
    private final JPanel specificPanel = new JPanel(new GridLayout(0, 2, 8, 8));
    private final JComboBox<String> categoryBox = new JComboBox<>(CATEGORIES);
    private final JComboBox<String> typeBox = new JComboBox<>();
    private final JComboBox<Rarity> rarityBox = new JComboBox<>(Rarity.values());
    private final Map<String, JTextField> textFields = new HashMap<>();
    private final Map<String, JCheckBox> checkBoxes = new HashMap<>();

    private ProductDialog() {
        addTextField(commonPanel, "Name", "name", "New product");
        addTextField(commonPanel, "Price (USD)", "price", "1000.0");
        addTextField(commonPanel, "Quantity", "quantity", "1");
        addTextField(commonPanel, "Weight (kg)", "weight", "1.0");

        JPanel typePanel = new JPanel(new GridLayout(0, 2, 8, 8));
        typePanel.add(new JLabel("Category:"));
        typePanel.add(categoryBox);
        typePanel.add(new JLabel("Product type:"));
        typePanel.add(typeBox);

        JPanel headerPanel = new JPanel(new BorderLayout(8, 8));
        headerPanel.add(typePanel, BorderLayout.NORTH);
        headerPanel.add(commonPanel, BorderLayout.CENTER);

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(new JScrollPane(specificPanel), BorderLayout.CENTER);

        categoryBox.addActionListener(event -> updateProductTypes());
        typeBox.addActionListener(event -> rebuildSpecificFields());
        updateProductTypes();
    }

    private void updateProductTypes() {
        String category = (String) categoryBox.getSelectedItem();
        String[] types = switch (category) {
            case "Artifact" -> ARTIFACT_TYPES;
            case "Digital" -> DIGITAL_TYPES;
            case "Robot" -> ROBOT_TYPES;
            default -> throw new IllegalStateException("Unknown category: " + category);
        };

        typeBox.removeAllItems();
        for (String type : types) {
            typeBox.addItem(type);
        }
        typeBox.setSelectedIndex(0);
    }

    public static Product showDialog(Component parent) {
        ProductDialog dialog = new ProductDialog();

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                parent,
                dialog.rootPanel,
                "Create product",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return null;
            }

            try {
                return dialog.createProduct();
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(
                    parent,
                    exception.getMessage(),
                    "Invalid product data",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void rebuildSpecificFields() {
        specificPanel.removeAll();
        textFields.keySet().removeIf(key -> !isCommonKey(key));
        checkBoxes.clear();

        String type = (String) typeBox.getSelectedItem();
        if (type == null) {
            return;
        }

        textFields.get("name").setText(type);

        switch (type) {
            case "ChronoBattery" -> {
                addArtifactFields();
                addTextField(specificPanel, "Energy capacity (mAh)", "energyCapacity", "5000");
                addTextField(specificPanel, "Compatible model", "compatibleModel", "Guardian-X");
                addTextField(specificPanel, "Recharge rate (mAh/min)", "rechargeRate", "50");
            }
            case "QuantumCore" -> {
                addArtifactFields();
                addTextField(specificPanel, "Computing power (TFLOPS)", "computingPower", "1000");
                addTextField(specificPanel, "Compatible model", "compatibleModel", "Guardian-X");
                addTextField(specificPanel, "Stability (%)", "stability", "90");
            }
            case "SoulProcessor" -> {
                addArtifactFields();
                addTextField(specificPanel, "Soul name", "soulName", "Astra");
                addTextField(specificPanel, "Emotion level (%)", "emotionLevel", "50");
                addTextField(specificPanel, "Control level (%)", "controlLevel", "80");
            }
            case "CombatFirmware" -> {
                addDigitalFields();
                addTextField(specificPanel, "Supported weapons", "supportedWeapons", "4");
                addTextField(specificPanel, "Aim accuracy (%)", "aimAccuracy", "90");
                addTextField(specificPanel, "Version", "version", "1.0");
            }
            case "GameLicense" -> {
                addDigitalFields();
                addTextField(specificPanel, "Platform", "platform", "PC");
                addTextField(specificPanel, "Minimum age (years)", "minimumAge", "12");
                addCheckBox(specificPanel, "Multiplayer", "multiplayer", true);
            }
            case "MedicalAI" -> {
                addDigitalFields();
                addTextField(specificPanel, "Diagnosis accuracy (%)", "diagnosisAccuracy", "95");
                addTextField(specificPanel, "Procedure count", "procedureCount", "500");
                addCheckBox(specificPanel, "Certified", "certified", true);
            }
            case "NavigationModule" -> {
                addDigitalFields();
                addTextField(specificPanel, "Map count", "mapCount", "100");
                addTextField(specificPanel, "Maximum route distance (km)", "maximumRouteDistance", "5000.0");
                addCheckBox(specificPanel, "Supports unknown areas", "supportsUnknownAreas", true);
            }
            case "GuardianRobot" -> {
                addRobotFields();
                addTextField(specificPanel, "Armor level (%)", "armorLevel", "80");
                addTextField(specificPanel, "Weapon slots", "weaponSlots", "4");
                addTextField(specificPanel, "Security level (%)", "securityLevel", "90");
            }
            case "MedicalRobot" -> {
                addRobotFields();
                addTextField(specificPanel, "Operation accuracy (%)", "operationAccuracy", "95");
                addCheckBox(specificPanel, "Sterile", "sterile", true);
                addTextField(specificPanel, "Medicine capacity (units)", "medicineCapacity", "100");
            }
            case "ScoutDrone" -> {
                addRobotFields();
                addTextField(specificPanel, "Scan range (m)", "scanRange", "1000.0");
                addTextField(specificPanel, "Movement speed (km/h)", "movementSpeed", "60.0");
                addCheckBox(specificPanel, "Night vision", "nightVision", true);
            }
            default -> throw new IllegalStateException("Unknown product type: " + type);
        }

        specificPanel.revalidate();
        specificPanel.repaint();
    }

    private void addArtifactFields() {
        specificPanel.add(new JLabel("Rarity:"));
        specificPanel.add(rarityBox);
        addCheckBox(specificPanel, "Cursed", "isCursed", false);
        addTextField(specificPanel, "Owner name", "ownerName", "Chronos Corporation");
        addTextField(specificPanel, "Magic power (%)", "magicPower", "90");
    }

    private void addDigitalFields() {
        addTextField(specificPanel, "License key", "licenseKey", "LICENSE-001");
        addTextField(specificPanel, "File size (MB, 1-1000)", "fileSize", "250");
        addTextField(specificPanel, "Download URL", "downloadUrl", "https://example.com/product");
    }

    private void addRobotFields() {
        addTextField(specificPanel, "Model", "model", "RX-1");
        addTextField(specificPanel, "Battery capacity (mAh)", "batteryCapacity", "5000");
        addTextField(specificPanel, "Intelligence level (%)", "intelligenceLevel", "80");
    }

    private Product createProduct() {
        String type = (String) typeBox.getSelectedItem();
        ProductFactory factory = switch (type) {
            case "ChronoBattery" -> new ChronoBatteryFactory(
                selectedRarity(), checked("isCursed"), text("ownerName"), integer("magicPower"),
                integer("energyCapacity"), text("compatibleModel"), integer("rechargeRate")
            );
            case "QuantumCore" -> new QuantumCoreFactory(
                selectedRarity(), checked("isCursed"), text("ownerName"), integer("magicPower"),
                integer("computingPower"), text("compatibleModel"), integer("stability")
            );
            case "SoulProcessor" -> new SoulProcessorFactory(
                selectedRarity(), checked("isCursed"), text("ownerName"), integer("magicPower"),
                text("soulName"), integer("emotionLevel"), integer("controlLevel")
            );
            case "CombatFirmware" -> new CombatFirmwareFactory(
                text("licenseKey"), integer("fileSize"), text("downloadUrl"),
                integer("supportedWeapons"), integer("aimAccuracy"), text("version")
            );
            case "GameLicense" -> new GameLicenseFactory(
                text("licenseKey"), integer("fileSize"), text("downloadUrl"),
                text("platform"), integer("minimumAge"), checked("multiplayer")
            );
            case "MedicalAI" -> new MedicalAIFactory(
                text("licenseKey"), integer("fileSize"), text("downloadUrl"),
                integer("diagnosisAccuracy"), integer("procedureCount"), checked("certified")
            );
            case "NavigationModule" -> new NavigationModuleFactory(
                text("licenseKey"), integer("fileSize"), text("downloadUrl"),
                integer("mapCount"), decimal("maximumRouteDistance"), checked("supportsUnknownAreas")
            );
            case "GuardianRobot" -> new GuardianRobotFactory(
                text("model"), integer("batteryCapacity"), integer("intelligenceLevel"),
                integer("armorLevel"), integer("weaponSlots"), integer("securityLevel")
            );
            case "MedicalRobot" -> new MedicalRobotFactory(
                text("model"), integer("batteryCapacity"), integer("intelligenceLevel"),
                integer("operationAccuracy"), checked("sterile"), integer("medicineCapacity")
            );
            case "ScoutDrone" -> new ScoutDroneFactory(
                text("model"), integer("batteryCapacity"), integer("intelligenceLevel"),
                decimal("scanRange"), decimal("movementSpeed"), checked("nightVision")
            );
            default -> throw new IllegalStateException("Unknown product type: " + type);
        };

        return factory.createProduct(
            text("name"),
            decimal("price"),
            integer("quantity"),
            decimal("weight")
        );
    }

    private void addTextField(
        JPanel panel,
        String label,
        String key,
        String defaultValue
    ) {
        JTextField field = new JTextField(defaultValue);
        textFields.put(key, field);
        panel.add(new JLabel(label + ":"));
        panel.add(field);
    }

    private void addCheckBox(
        JPanel panel,
        String label,
        String key,
        boolean selected
    ) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(selected);
        checkBoxes.put(key, checkBox);
        panel.add(new JLabel(label + ":"));
        panel.add(checkBox);
    }

    private boolean isCommonKey(String key) {
        return key.equals("name")
            || key.equals("price")
            || key.equals("quantity")
            || key.equals("weight");
    }

    private String text(String key) {
        JTextField field = textFields.get(key);
        if (field == null) {
            throw new IllegalStateException("Missing field: " + key);
        }
        return field.getText().trim();
    }

    private int integer(String key) {
        return Integer.parseInt(text(key));
    }

    private double decimal(String key) {
        return Double.parseDouble(text(key));
    }

    private boolean checked(String key) {
        JCheckBox checkBox = checkBoxes.get(key);
        if (checkBox == null) {
            throw new IllegalStateException("Missing checkbox: " + key);
        }
        return checkBox.isSelected();
    }

    private Rarity selectedRarity() {
        return (Rarity) rarityBox.getSelectedItem();
    }
}

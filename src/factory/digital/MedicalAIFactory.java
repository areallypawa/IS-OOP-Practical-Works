package factory.digital;

import factory.ProductFactory;
import product.digital.MedicalAI;

public class MedicalAIFactory extends ProductFactory {
    private final String licenseKey;
    private final int fileSize;
    private final String downloadUrl;
    private final int diagnosisAccuracy;
    private final int procedureCount;
    private final boolean certified;

    public MedicalAIFactory(
        String licenseKey,
        int fileSize,
        String downloadUrl,
        int diagnosisAccuracy,
        int procedureCount,
        boolean certified
    ) {
        this.licenseKey = licenseKey;
        this.fileSize = fileSize;
        this.downloadUrl = downloadUrl;
        this.diagnosisAccuracy = diagnosisAccuracy;
        this.procedureCount = procedureCount;
        this.certified = certified;
    }

    @Override
    public MedicalAI createProduct(
        String name,
        double price,
        int quantity,
        double weight
    ) {
        return new MedicalAI(
            name,
            price,
            quantity,
            weight,
            licenseKey,
            fileSize,
            downloadUrl,
            diagnosisAccuracy,
            procedureCount,
            certified
        );
    }
}

package product.digital;

public class MedicalAI extends DigitalProduct {
    private int diagnosisAccuracy;
    private int procedureCount;
    private boolean certified;

    public MedicalAI(
        String name,
        double price,
        int quantity,
        double weight,
        String licenseKey,
        int fileSize,
        String downloadUrl,
        int diagnosisAccuracy,
        int procedureCount,
        boolean certified
    ) {
        super(name, price, quantity, weight, licenseKey, fileSize, downloadUrl);

        if (diagnosisAccuracy < 0 || diagnosisAccuracy > 100) {
            throw new IllegalArgumentException("Diagnosis accuracy must be between 0 and 100");
        }
        if (procedureCount < 0) {
            throw new IllegalArgumentException("Procedure count cannot be negative");
        }

        this.diagnosisAccuracy = diagnosisAccuracy;
        this.procedureCount = procedureCount;
        this.certified = certified;
    }

    private MedicalAI(MedicalAI source) {
        super(source);
        this.diagnosisAccuracy = source.diagnosisAccuracy;
        this.procedureCount = source.procedureCount;
        this.certified = source.certified;
    }

    @Override
    public String getDetails() {
        return super.getDetails()
            + "\nDiagnosis accuracy: " + diagnosisAccuracy + "%"
            + "\nProcedure count: " + procedureCount
            + "\nCertified: " + certified;
    }

    @Override
    public MedicalAI clone() {
        return new MedicalAI(this);
    }
}

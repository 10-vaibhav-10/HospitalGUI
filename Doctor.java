public class Doctor extends Person {

    private String specialization;

    /** Constructor with all fields. */
    public Doctor(int id, String name, int age, String specialization) {
        super(id, name, age);
        this.specialization = specialization;
    }

    public String getSpecialization() { return specialization; }

    /** Simple status message for demonstration. */
    @Override
    public void evaluateStatus() {
        System.out.println("Doctor available for patient review.");
    }
}
public class Patient extends Person {

    private String disease;
    private int healthScore;
    private String status;
    private String assignedDoctor;
    private int rewardPoints;
    private String registeredBy;

    /** Full constructor. Automatically evaluates status. */
    public Patient(int id, String name, int age, String disease,
                   int healthScore, String assignedDoctor, String registeredBy) {
        super(id, name, age);
        this.disease = disease;
        this.healthScore = healthScore;
        this.assignedDoctor = assignedDoctor;
        this.registeredBy = registeredBy;
        this.rewardPoints = 0;
        evaluateStatus();
    }

    /** Backward‑compatible constructor (default registeredBy = "Unknown"). */
    public Patient(int id, String name, int age, String disease,
                   int healthScore, String assignedDoctor) {
        this(id, name, age, disease, healthScore, assignedDoctor, "Unknown");
    }

    // Getters
    public String getDisease() { return disease; }
    public int getHealthScore() { return healthScore; }
    public String getStatus() { return status; }
    public String getAssignedDoctor() { return assignedDoctor; }
    public int getRewardPoints() { return rewardPoints; }
    public String getRegisteredBy() { return registeredBy; }

    /** Updates health score, re‑evaluates status, and applies reward/penalty if status changed. */
    public void setHealthScore(int score) {
        String oldStatus = this.status;
        this.healthScore = score;
        evaluateStatus();

        if (!oldStatus.equals(this.status)) {
            if (oldStatus.equals("Critical") && this.status.equals("Stable")) {
                rewardPoints += 10;
                System.out.println("Reward: +10 points for " + name + " (improved from Critical to Stable)");
            } else if (oldStatus.equals("Stable") && this.status.equals("Critical")) {
                rewardPoints -= 5;
                System.out.println("Penalty: -5 points for " + name + " (deteriorated to Critical)");
            } else if (oldStatus.equals("Observation") && this.status.equals("Stable")) {
                rewardPoints += 5;
                System.out.println("Reward: +5 points for " + name + " (now Stable)");
            } else if (oldStatus.equals("Observation") && this.status.equals("Critical")) {
                rewardPoints -= 3;
                System.out.println("Penalty: -3 points for " + name + " (worsened to Critical)");
            }
        }
    }

    public void addRewardPoints(int points) {
        this.rewardPoints += points;
        System.out.println(name + " earned " + points + " reward points. Total: " + rewardPoints);
    }

    public void deductRewardPoints(int points) {
        this.rewardPoints -= points;
        System.out.println(name + " lost " + points + " reward points. Total: " + rewardPoints);
    }

    /** Direct setter for file loading – no messages. */
    public void setRewardPoints(int points) {
        this.rewardPoints = points;
    }

    /** Determines status based on health score thresholds. */
    @Override
    public void evaluateStatus() {
        if (healthScore > 80) status = "Stable";
        else if (healthScore >= 50) status = "Observation";
        else status = "Critical";
    }

    /** Displays all patient information in a horizontal table format. */
    @Override
    public void displayInfo() {
        System.out.printf("| %-4d | %-15s | %-3d | %-15s | %-4d | %-15s | %-15s | %-4d | %-15s |\n",
                id, name, age, disease, healthScore, assignedDoctor, status, rewardPoints, registeredBy);
    }
}
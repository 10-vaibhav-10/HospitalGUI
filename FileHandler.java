import java.io.*;
import java.util.*;

public class FileHandler {

    /**
     * Loads patients from a CSV file (9‑field format).
     * Fields: P<id>;Name;Age;Disease;HealthScore;Doctor;RegisteredBy;RewardPoints;Status
     * Missing fields default to "Unknown" or 0.
     */
    public static void loadPatients(String fileName, Management manager) {
        try (Scanner file = new Scanner(new File(fileName))) {
            while (file.hasNextLine()) {
                String line = file.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(";");
                if (data.length < 7) {
                    System.out.println("Skipping invalid row (too few fields): " + line);
                    continue;
                }
                try {
                    int patientId = Integer.parseInt(data[0].replace("P", "").trim());
                    String name = data[1].trim();
                    int age = Integer.parseInt(data[2].trim());
                    String disease = data[3].trim();
                    int healthScore = (int) Double.parseDouble(data[4].trim());
                    String doctor = data[5].trim();
                    String registeredBy = (data.length >= 7) ? data[6].trim() : "Unknown";
                    int rewardPoints = (data.length >= 8) ? Integer.parseInt(data[7].trim()) : 0;
                    // data[8] = status – ignored, will be recalculated

                    Patient p = new Patient(patientId, name, age, disease, healthScore, doctor, registeredBy);
                    p.setRewardPoints(rewardPoints);
                    manager.addPatient(p, false);
                } catch (NumberFormatException e) {
                    System.out.println("Bad numeric data in row: " + line);
                }
            }
            System.out.println("Patients loaded successfully. Total: " + manager.getPatients().size());
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Starting with empty database.");
        } catch (Exception e) {
            System.out.println("Error reading file.");
            e.printStackTrace();
        }
    }

    /**
     * Saves all patients to a CSV file (9‑field format).
     * Overwrites the existing file.
     */
    public static void savePatients(String fileName, Management manager) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            for (Patient p : manager.getPatients()) {
                writer.println(
                    "P" + p.getId() + ";" +
                    p.getName() + ";" +
                    p.getAge() + ";" +
                    p.getDisease() + ";" +
                    p.getHealthScore() + ";" +
                    p.getAssignedDoctor() + ";" +
                    p.getRegisteredBy() + ";" +
                    p.getRewardPoints() + ";" +
                    p.getStatus()
                );
            }
            System.out.println("Records saved successfully. Format: 9 fields (no placeholders).");
        } catch (Exception e) {
            System.out.println("Error saving file.");
            e.printStackTrace();
        }
    }
}
import java.util.*;
import javax.swing.*;

/** Entry point – offers choice between Text‑Based Interface and GUI. */
public class Main {
    public static void main(String[] args) {
        Management manager = new Management();
        try {
            FileHandler.loadPatients("patients.csv", manager);
        } catch(Exception e) {
            System.out.println("Load error: "+e.getMessage());
        }

        String[] options = {"Text-Based Interface", "Graphical User Interface"};
        int choice = JOptionPane.showOptionDialog(null, "Select Interface Mode:", "Hospital System",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if(choice == 1) {
            SwingUtilities.invokeLater(() -> new GUI(manager));
        } else {
            runTextInterface(manager);
        }
    }

    /** Runs the console‑based text interface. */
    private static void runTextInterface(Management manager) {
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        do {
            System.out.println("\n=== HOSPITAL SYSTEM (TBI) ===");
            System.out.println("1. Add Patient");
            System.out.println("2. View All");
            System.out.println("3. Search by ID");
            System.out.println("4. Search by Name");
            System.out.println("5. Update Health");
            System.out.println("6. Delete Patient");
            System.out.println("7. Run Evaluation");
            System.out.println("8. View Critical Queue");
            System.out.println("9. Process Next Critical");
            System.out.println("10. Sort by ID (Quick)");
            System.out.println("11. Sort by Name (Bubble)");
            System.out.println("12. Sort by Health Score (Quick)");
            System.out.println("13. Binary Search by ID");
            System.out.println("14. Save to File");
            System.out.println("15. View Reward Points");
            System.out.println("16. Exit");
            System.out.print("Choice: ");
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch(NumberFormatException e) {
                System.out.println("Invalid number.");
                continue;
            }
            switch(choice) {
                case 1: addPatientText(sc, manager); break;
                case 2: manager.viewPatients(); break;
                case 3: searchByIdText(sc, manager); break;
                case 4: searchByNameText(sc, manager); break;
                case 5: updateHealthText(sc, manager); break;
                case 6: deletePatientText(sc, manager); break;
                case 7: manager.runEvaluations(); break;
                case 8: manager.viewCriticalQueue(); break;
                case 9: manager.processNextCritical(); break;
                case 10: manager.quickSortById(); break;
                case 11: manager.bubbleSortByName(); break;
                case 12: manager.quickSortByHealthScore(); break;
                case 13: binarySearchText(sc, manager); break;
                case 14: FileHandler.savePatients("patients.csv", manager); break;
                case 15: manager.viewRewardPoints(); break;
                case 16: System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid choice.");
            }
        } while(choice != 16);
        sc.close();
    }

    /** Text‑based add patient with duplicate check. */
    private static void addPatientText(Scanner sc, Management manager) {
        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            if(manager.isIdExists(id)) {
                System.out.println("Error: ID already exists. Please use a unique ID.");
                return;
            }
            System.out.print("Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Age: ");
            int age = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Disease: ");
            String disease = sc.nextLine().trim();
            System.out.print("Health Score (0-100): ");
            int score = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Doctor: ");
            String doctor = sc.nextLine().trim();
            System.out.print("Registered By: ");
            String regBy = sc.nextLine().trim();
            if(name.isEmpty() || disease.isEmpty() || doctor.isEmpty()) {
                System.out.println("Name, Disease, Doctor cannot be empty.");
                return;
            }
            if(score<0 || score>100) {
                System.out.println("Score must be 0-100.");
                return;
            }
            Patient p = new Patient(id, name, age, disease, score, doctor, regBy);
            manager.addPatient(p, true);
        } catch(NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private static void searchByIdText(Scanner sc, Management manager) {
        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            Patient p = manager.searchPatient(id);
            if(p!=null) p.displayInfo();
            else System.out.println("Not found.");
        } catch(NumberFormatException e) { System.out.println("Invalid ID."); }
    }

    private static void searchByNameText(Scanner sc, Management manager) {
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        Patient p = manager.searchPatientByName(name);
        if(p!=null) p.displayInfo();
        else System.out.println("Not found.");
    }

    private static void updateHealthText(Scanner sc, Management manager) {
        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("New Health Score: ");
            int score = Integer.parseInt(sc.nextLine().trim());
            if(score<0 || score>100) { System.out.println("Score 0-100."); return; }
            manager.updateHealth(id, score);
        } catch(NumberFormatException e) { System.out.println("Invalid input."); }
    }

    private static void deletePatientText(Scanner sc, Management manager) {
        try {
            System.out.print("ID to delete: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            manager.deletePatient(id);
        } catch(NumberFormatException e) { System.out.println("Invalid ID."); }
    }

    private static void binarySearchText(Scanner sc, Management manager) {
        try {
            System.out.print("ID to binary search: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            Patient p = manager.binarySearchById(id);
            if(p!=null) System.out.println("Found: "+p.getName());
            else System.out.println("Not found (list may not be sorted by ID).");
        } catch(NumberFormatException e) { System.out.println("Invalid ID."); }
    }
}
import java.util.*;

public class Management {

    private ArrayList<Patient> patients;          // master list
    private PriorityQueue<Patient> criticalQueue; // ordered by health score (lowest first)
    private Map<Integer, String> previousStatus;  // snapshot for evaluation
    private Map<Integer, Integer> previousHealth;

    /** Constructor – initialises collections and priority queue comparator. */
    public Management() {
        patients = new ArrayList<>();
        // PriorityQueue: lowest health score first, tie‑break by ID
        criticalQueue = new PriorityQueue<>((p1, p2) -> {
            int scoreCompare = Integer.compare(p1.getHealthScore(), p2.getHealthScore());
            if (scoreCompare != 0) return scoreCompare;
            return Integer.compare(p1.getId(), p2.getId());
        });
        previousStatus = new HashMap<>();
        previousHealth = new HashMap<>();
    }

    public ArrayList<Patient> getPatients() { return patients; }

    /** Checks whether a patient with given ID already exists. */
    public boolean isIdExists(int id) {
        return searchPatient(id) != null;
    }

    /** Stores current status & health of all patients for future comparison. */
    private void takeSnapshot() {
        for (Patient p : patients) {
            previousStatus.put(p.getId(), p.getStatus());
            previousHealth.put(p.getId(), p.getHealthScore());
        }
    }

    /**
     * Adds a patient. Rejects duplicate IDs.
     * @param showMessage if true, prints confirmation.
     */
    public void addPatient(Patient p, boolean showMessage) {
        if (isIdExists(p.getId())) {
            if (showMessage) System.out.println("Error: Patient ID " + p.getId() + " already exists.");
            return;
        }
        patients.add(p);
        if (p.getStatus().equals("Critical")) criticalQueue.offer(p);
        if (showMessage) System.out.println("Patient added successfully.");
        previousStatus.put(p.getId(), p.getStatus());
        previousHealth.put(p.getId(), p.getHealthScore());
    }

    public void addPatient(Patient p) {
        addPatient(p, false);
    }

    /** Displays all patients (horizontal format). */
    public void viewPatients() {
        if (patients.isEmpty()) { System.out.println("No records."); return; }
        for (Patient p : patients) {
            p.displayInfo();
            System.out.println("----------------");
        }
    }

    /** Linear search by ID. */
    public Patient searchPatient(int id) {
        for (Patient p : patients) if (p.getId() == id) return p;
        return null;
    }

    /** Case‑insensitive linear search by name. */
    public Patient searchPatientByName(String name) {
        for (Patient p : patients) if (p.getName().equalsIgnoreCase(name)) return p;
        return null;
    }

    /**
     * Updates health score, re‑evaluates status, and manages priority queue.
     * If still critical after change, re‑inserts to reorder.
     */
    public void updateHealth(int id, int score) {
        Patient p = searchPatient(id);
        if (p == null) { System.out.println("Patient not found."); return; }
        String oldStatus = p.getStatus();
        p.setHealthScore(score);
        String newStatus = p.getStatus();

        if (newStatus.equals("Critical") && !criticalQueue.contains(p)) {
            criticalQueue.offer(p);
            System.out.println("[WARN] Patient moved to Critical - added to priority queue.");
        } else if (!newStatus.equals("Critical") && criticalQueue.contains(p)) {
            criticalQueue.remove(p);
            System.out.println("[OK] Patient no longer critical - removed from queue.");
        } else if (newStatus.equals("Critical") && criticalQueue.contains(p)) {
            criticalQueue.remove(p);
            criticalQueue.offer(p);
            System.out.println("[CHANGED] Patient health score changed - priority re-evaluated.");
        }

        previousStatus.put(p.getId(), newStatus);
        previousHealth.put(p.getId(), p.getHealthScore());
        System.out.println("Health score updated.");
    }

    /** Deletes patient by ID from all collections and snapshots. */
    public void deletePatient(int id) {
        Patient p = searchPatient(id);
        if (p == null) { System.out.println("Patient not found."); return; }
        patients.remove(p);
        criticalQueue.remove(p);
        previousStatus.remove(id);
        previousHealth.remove(id);
        System.out.println("Deleted.");
    }

    /** Periodic evaluation: compares current state with previous snapshot and updates queue. */
    public void runEvaluations() {
        System.out.println("\n--- Running Periodic Evaluation ---");
        int statusChanges = 0, healthChanges = 0;
        for (Patient p : patients) {
            int id = p.getId();
            String oldStatus = previousStatus.get(id);
            int oldHealth = previousHealth.get(id);
            String newStatus = p.getStatus();
            int newHealth = p.getHealthScore();
            if (oldStatus == null) { oldStatus = newStatus; oldHealth = newHealth; }

            if (!newStatus.equals(oldStatus)) {
                statusChanges++;
                System.out.println("[STATUS] " + p.getName() + " : " + oldStatus + " -> " + newStatus);
            }
            if (newHealth != oldHealth) {
                healthChanges++;
                int diff = newHealth - oldHealth;
                System.out.println("[HEALTH] " + p.getName() + ": " + oldHealth + " -> " + newHealth + " (" + (diff>0?"+":"") + diff + ")");
            }

            if (newStatus.equals("Critical")) {
                if (!criticalQueue.contains(p)) criticalQueue.offer(p);
                else { criticalQueue.remove(p); criticalQueue.offer(p); }
            } else if (criticalQueue.contains(p)) {
                criticalQueue.remove(p);
                System.out.println("[REMOVED] " + p.getName() + " removed from critical queue.");
            }
        }
        if (statusChanges == 0 && healthChanges == 0)
            System.out.println("No changes in patient status or health score since last evaluation.");
        else
            System.out.println("Summary: " + statusChanges + " status change(s), " + healthChanges + " health score change(s).");
        takeSnapshot();
        System.out.println("Evaluation complete.\n");
    }

    /** Processes the next critical patient (lowest health score). */
    public void processNextCritical() {
        Patient p = criticalQueue.poll();
        if (p == null) { System.out.println("No critical patients in queue."); return; }
        System.out.println("\n--- Treating Critical Patient (Priority Order) ---");
        System.out.println("Patient with health score " + p.getHealthScore() + " treated next.");
        p.displayInfo();
        int improvement = new Random().nextInt(30) + 10;
        System.out.println("Administering treatment... Health +" + improvement);
        int newScore = p.getHealthScore() + improvement;
        p.setHealthScore(newScore);
        System.out.println("New health score: " + p.getHealthScore());
        if (!p.getStatus().equals("Critical"))
            System.out.println("[OK] Patient no longer critical - removed from queue.");
        else {
            criticalQueue.offer(p);
            System.out.println("[WARN] Still critical - re-added to queue with new priority.");
        }
    }

    /** Displays the priority queue without consuming it. */
    public void viewCriticalQueue() {
        if (criticalQueue.isEmpty()) { System.out.println("No critical patients in queue."); return; }
        System.out.println("\n--- Critical Priority Queue (lowest health score first) ---");
        List<Patient> sorted = new ArrayList<>(criticalQueue);
        sorted.sort((p1, p2) -> Integer.compare(p1.getHealthScore(), p2.getHealthScore()));
        int pos = 1;
        for (Patient p : sorted) {
            System.out.println("Priority " + pos++ + ": " + p.getName() +
                               " (ID: " + p.getId() + ", Health Score: " + p.getHealthScore() + ")");
        }
    }

    /** Shows all reward points. */
    public void viewRewardPoints() {
        System.out.println("\n--- REWARD POINTS ---");
        for (Patient p : patients)
            System.out.println(p.getName() + " (ID " + p.getId() + "): " + p.getRewardPoints() + " points");
    }

    // -------------------- Sorting Algorithms --------------------
    /** Quick Sort by ID (ascending). */
    public void quickSortById() {
        if (patients.size() <= 1) return;
        quickSortByIdRecursive(0, patients.size() - 1);
        System.out.println("Sorted by ID (Quick Sort)");
    }
    private void quickSortByIdRecursive(int low, int high) {
        if (low < high) {
            int pi = partitionById(low, high);
            quickSortByIdRecursive(low, pi - 1);
            quickSortByIdRecursive(pi + 1, high);
        }
    }
    private int partitionById(int low, int high) {
        Patient pivot = patients.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (patients.get(j).getId() <= pivot.getId()) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        return i + 1;
    }

    /** Quick Sort by Health Score (descending – higher score better). */
    public void quickSortByHealthScore() {
        if (patients.size() <= 1) return;
        quickSortByHealthScoreRecursive(0, patients.size() - 1);
        System.out.println("Sorted by Health Score (Quick Sort)");
    }
    private void quickSortByHealthScoreRecursive(int low, int high) {
        if (low < high) {
            int pi = partitionByHealthScore(low, high);
            quickSortByHealthScoreRecursive(low, pi - 1);
            quickSortByHealthScoreRecursive(pi + 1, high);
        }
    }
    private int partitionByHealthScore(int low, int high) {
        Patient pivot = patients.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (patients.get(j).getHealthScore() >= pivot.getHealthScore()) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        return i + 1;
    }

    /** Bubble Sort by Name (alphabetical, case‑insensitive). */
    public void bubbleSortByName() {
        if (patients.size() <= 1) return;
        int n = patients.size();
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (patients.get(j).getName().compareToIgnoreCase(patients.get(j + 1).getName()) > 0) {
                    swap(j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        System.out.println("Sorted by Name (Bubble Sort)");
    }

    private void swap(int i, int j) {
        Patient temp = patients.get(i);
        patients.set(i, patients.get(j));
        patients.set(j, temp);
    }

    // -------------------- Searching Algorithms --------------------
    /** Binary search by ID (requires list sorted by ID first). */
    public Patient binarySearchById(int targetId) {
        int left = 0, right = patients.size() - 1;
        int iterations = 0;
        while (left <= right) {
            iterations++;
            int mid = left + (right - left) / 2;
            Patient midPatient = patients.get(mid);
            if (midPatient.getId() == targetId) {
                System.out.println("Binary search completed in " + iterations + " iteration(s).");
                return midPatient;
            }
            if (midPatient.getId() < targetId) left = mid + 1;
            else right = mid - 1;
        }
        System.out.println("Binary search completed in " + iterations + " iteration(s). Patient not found.");
        return null;
    }

    public int getPatientCount() { return patients.size(); }
}
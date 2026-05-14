import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/** Swing‑based graphical user interface for the Hospital Management System. */
public class GUI {
    private Management manager;
    private JFrame frame;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JPanel tablePanel;
    private JTextArea evalArea;

    public GUI(Management manager) {
        this.manager = manager;
        initialize();
    }

    /** Builds the main window with tabs, buttons, and table. */
    private void initialize() {
        frame = new JFrame("Hospital Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 750);
        frame.setLayout(new BorderLayout());

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save to File");
        JMenuItem exitItem = new JMenuItem("Exit");
        saveItem.addActionListener(e -> saveToFile());
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);

        JTabbedPane tabbedPane = new JTabbedPane();

        // -------------------- Tab 1: Patient Management --------------------
        JPanel managementPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Patient Operations"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(10);
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(15);
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField(5);
        JLabel diseaseLabel = new JLabel("Disease:");
        JTextField diseaseField = new JTextField(15);
        JLabel scoreLabel = new JLabel("Health Score:");
        JTextField scoreField = new JTextField(5);
        JLabel doctorLabel = new JLabel("Doctor:");
        JTextField doctorField = new JTextField(15);
        JLabel regByLabel = new JLabel("Registered By:");
        JTextField regByField = new JTextField(12);
        JButton addBtn = new JButton("Add Patient");
        JButton updateBtn = new JButton("Update Health");
        JButton deleteBtn = new JButton("Delete Patient");
        JButton searchIdBtn = new JButton("Search by ID");
        JButton searchNameBtn = new JButton("Search by Name");

        gbc.gridx=0; gbc.gridy=0; inputPanel.add(idLabel,gbc);
        gbc.gridx=1; gbc.gridy=0; inputPanel.add(idField,gbc);
        gbc.gridx=2; gbc.gridy=0; inputPanel.add(nameLabel,gbc);
        gbc.gridx=3; gbc.gridy=0; inputPanel.add(nameField,gbc);
        gbc.gridx=0; gbc.gridy=1; inputPanel.add(ageLabel,gbc);
        gbc.gridx=1; gbc.gridy=1; inputPanel.add(ageField,gbc);
        gbc.gridx=2; gbc.gridy=1; inputPanel.add(diseaseLabel,gbc);
        gbc.gridx=3; gbc.gridy=1; inputPanel.add(diseaseField,gbc);
        gbc.gridx=0; gbc.gridy=2; inputPanel.add(scoreLabel,gbc);
        gbc.gridx=1; gbc.gridy=2; inputPanel.add(scoreField,gbc);
        gbc.gridx=2; gbc.gridy=2; inputPanel.add(doctorLabel,gbc);
        gbc.gridx=3; gbc.gridy=2; inputPanel.add(doctorField,gbc);
        gbc.gridx=0; gbc.gridy=3; inputPanel.add(regByLabel,gbc);
        gbc.gridx=1; gbc.gridy=3; inputPanel.add(regByField,gbc);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn); buttonPanel.add(updateBtn); buttonPanel.add(deleteBtn);
        buttonPanel.add(searchIdBtn); buttonPanel.add(searchNameBtn);
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=4; inputPanel.add(buttonPanel,gbc);
        managementPanel.add(inputPanel, BorderLayout.NORTH);

        JTextArea resultArea = new JTextArea(5,40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder("Search Results"));
        managementPanel.add(resultScroll, BorderLayout.CENTER);

        // Button actions
        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                if (manager.isIdExists(id)) {
                    resultArea.setText("Error: Patient ID " + id + " already exists.");
                    return;
                }
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String disease = diseaseField.getText().trim();
                int score = Integer.parseInt(scoreField.getText().trim());
                String doctor = doctorField.getText().trim();
                String regBy = regByField.getText().trim();
                if (name.isEmpty() || disease.isEmpty() || doctor.isEmpty()) {
                    resultArea.setText("Error: Name, Disease, Doctor cannot be empty.");
                    return;
                }
                if (score<0 || score>100) {
                    resultArea.setText("Error: Health score must be 0-100.");
                    return;
                }
                Patient p = new Patient(id, name, age, disease, score, doctor, regBy);
                manager.addPatient(p, true);
                refreshTable();
                resultArea.setText("Patient added successfully.");
                clearFields(idField, nameField, ageField, diseaseField, scoreField, doctorField, regByField);
            } catch(NumberFormatException ex) {
                resultArea.setText("Error: Invalid number format.");
            }
        });
        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                int score = Integer.parseInt(scoreField.getText().trim());
                if (score<0 || score>100) { resultArea.setText("Score must be 0-100."); return; }
                manager.updateHealth(id, score);
                refreshTable();
                resultArea.setText("Health updated for ID "+id);
            } catch(NumberFormatException ex) { resultArea.setText("Invalid ID or score."); }
        });
        deleteBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                manager.deletePatient(id);
                refreshTable();
                resultArea.setText("Deleted patient ID "+id);
            } catch(NumberFormatException ex) { resultArea.setText("Invalid ID."); }
        });
        searchIdBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                Patient p = manager.searchPatient(id);
                if(p!=null) resultArea.setText("Found: "+p.getName()+" | ID:"+p.getId()+" | Score:"+p.getHealthScore());
                else resultArea.setText("Not found.");
            } catch(NumberFormatException ex) { resultArea.setText("Invalid ID."); }
        });
        searchNameBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            Patient p = manager.searchPatientByName(name);
            if(p!=null) resultArea.setText("Found: "+p.getName()+" | ID:"+p.getId());
            else resultArea.setText("Not found.");
        });
        tabbedPane.addTab("Patient Management", managementPanel);

        // -------------------- Tab 2: View & Sort --------------------
        JPanel viewSortPanel = new JPanel(new BorderLayout());
        JPanel sortPanel = new JPanel(new FlowLayout());
        sortPanel.setBorder(BorderFactory.createTitledBorder("Sorting Options"));
        JButton viewAllBtn = new JButton("View All Patients");
        JButton sortByIdBtn = new JButton("Sort by ID (Quick Sort)");
        JButton sortByNameBtn = new JButton("Sort by Name (Bubble Sort)");
        JButton sortByScoreBtn = new JButton("Sort by Health Score (Quick Sort)");
        JButton rewardBtn = new JButton("View Reward Points");
        sortPanel.add(viewAllBtn); sortPanel.add(sortByIdBtn); sortPanel.add(sortByNameBtn);
        sortPanel.add(sortByScoreBtn); sortPanel.add(rewardBtn);
        viewSortPanel.add(sortPanel, BorderLayout.NORTH);
        tablePanel = new JPanel(new BorderLayout());
        String[] cols = {"ID","Name","Age","Disease","Health Score","Status","Doctor","Reward Pts","Registered By"};
        tableModel = new DefaultTableModel(cols,0);
        patientTable = new JTable(tableModel);
        patientTable.setFillsViewportHeight(true);
        JScrollPane tableScroll = new JScrollPane(patientTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.setVisible(false);
        viewSortPanel.add(tablePanel, BorderLayout.CENTER);
        viewAllBtn.addActionListener(e -> refreshTable());
        sortByIdBtn.addActionListener(e -> { manager.quickSortById(); refreshTable(); showStatus("Sorted by ID"); });
        sortByNameBtn.addActionListener(e -> { manager.bubbleSortByName(); refreshTable(); showStatus("Sorted by Name"); });
        sortByScoreBtn.addActionListener(e -> { manager.quickSortByHealthScore(); refreshTable(); showStatus("Sorted by Health Score"); });
        rewardBtn.addActionListener(e -> showRewardPoints());
        tabbedPane.addTab("View & Sort", viewSortPanel);

        // -------------------- Tab 3: Critical Queue --------------------
        JPanel queuePanel = new JPanel(new BorderLayout());
        JPanel qButtonPanel = new JPanel(new FlowLayout());
        JButton viewQueueBtn = new JButton("View Critical Queue");
        JButton processQueueBtn = new JButton("Process Next Critical Patient");
        qButtonPanel.add(viewQueueBtn); qButtonPanel.add(processQueueBtn);
        JTextArea queueArea = new JTextArea(10,50);
        queueArea.setEditable(false);
        queueArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane queueScroll = new JScrollPane(queueArea);
        queueScroll.setBorder(BorderFactory.createTitledBorder("Priority Queue (lowest health first)"));
        queuePanel.add(qButtonPanel, BorderLayout.NORTH);
        queuePanel.add(queueScroll, BorderLayout.CENTER);
        viewQueueBtn.addActionListener(e -> displayCriticalQueue(queueArea));
        processQueueBtn.addActionListener(e -> { manager.processNextCritical(); refreshTable(); displayCriticalQueue(queueArea); showStatus("Processed next critical"); });
        tabbedPane.addTab("Critical Queue", queuePanel);

        // -------------------- Tab 4: Evaluation --------------------
        JPanel evalPanel = new JPanel(new BorderLayout());
        JPanel eButtonPanel = new JPanel(new FlowLayout());
        JButton runEvalBtn = new JButton("Run Periodic Evaluation");
        JButton binarySearchBtn = new JButton("Binary Search by ID");
        eButtonPanel.add(runEvalBtn); eButtonPanel.add(binarySearchBtn);
        evalArea = new JTextArea(15,50);
        evalArea.setEditable(false);
        evalArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        evalArea.setBackground(new Color(30,30,30));
        evalArea.setForeground(new Color(0,255,0));
        JScrollPane evalScroll = new JScrollPane(evalArea);
        evalScroll.setBorder(BorderFactory.createTitledBorder("Evaluation Output"));
        evalPanel.add(eButtonPanel, BorderLayout.NORTH);
        evalPanel.add(evalScroll, BorderLayout.CENTER);
        runEvalBtn.addActionListener(e -> runEvaluationWithGUICapture());
        binarySearchBtn.addActionListener(e -> binarySearchDialog());
        tabbedPane.addTab("Evaluation", evalPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        statusLabel = new JLabel("Ready - " + manager.getPatientCount() + " patients", SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel.setPreferredSize(new Dimension(frame.getWidth(),25));
        frame.add(statusLabel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    /** Refreshes the patient table and shows it if hidden. */
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Patient p : manager.getPatients()) {
            tableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getAge(), p.getDisease(),
                p.getHealthScore(), p.getStatus(), p.getAssignedDoctor(),
                p.getRewardPoints(), p.getRegisteredBy()
            });
        }
        if(!tablePanel.isVisible()) tablePanel.setVisible(true);
        showStatus("Total patients: "+manager.getPatientCount());
    }

    /** Displays the current critical queue (lowest health first). */
    private void displayCriticalQueue(JTextArea area) {
        StringBuilder sb = new StringBuilder("=== CRITICAL PRIORITY QUEUE (lowest health first) ===\n");
        boolean has = false;
        int pos=1;
        for(Patient p : manager.getPatients()) {
            if(p.getStatus().equals("Critical")) {
                sb.append(pos++).append(". ID: ").append(p.getId()).append(" | ")
                  .append(p.getName()).append(" | Health Score: ").append(p.getHealthScore()).append("\n");
                has=true;
            }
        }
        if(!has) sb.append("No critical patients.\n");
        area.setText(sb.toString());
    }

    /** Shows reward points in a popup dialog. */
    private void showRewardPoints() {
        StringBuilder sb = new StringBuilder("Reward Points:\n");
        for(Patient p : manager.getPatients())
            sb.append(p.getName()).append(" (ID ").append(p.getId()).append("): ").append(p.getRewardPoints()).append(" pts\n");
        JOptionPane.showMessageDialog(frame, sb.toString(), "Reward Points", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Runs evaluation and captures console output into the evaluation text area. */
    private void runEvaluationWithGUICapture() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        manager.runEvaluations();
        System.out.flush();
        System.setOut(old);
        evalArea.append("=== Periodic Evaluation ===\n"+new java.util.Date()+"\n"+baos.toString()+"\n");
        evalArea.setCaretPosition(evalArea.getDocument().getLength());
        refreshTable();
        showStatus("Evaluation completed");
    }

    /** Binary search dialog (requires sorted list by ID). */
    private void binarySearchDialog() {
        String idStr = JOptionPane.showInputDialog(frame, "Enter ID (list must be sorted by ID first)");
        if(idStr!=null) {
            try {
                int id = Integer.parseInt(idStr.trim());
                long start = System.nanoTime();
                Patient p = manager.binarySearchById(id);
                long end = System.nanoTime();
                long us = (end-start)/1000;
                if(p!=null) {
                    evalArea.append("Binary Search: Found ID "+id+" ("+p.getName()+") in "+us+" ns\n");
                    JOptionPane.showMessageDialog(frame, "Found: "+p.getName()+"\nTime: "+us+" ns");
                } else {
                    evalArea.append("Binary Search: ID "+id+" not found. Time: "+us+" ns\n");
                    JOptionPane.showMessageDialog(frame, "Not found.");
                }
                evalArea.setCaretPosition(evalArea.getDocument().getLength());
            } catch(NumberFormatException ex) { JOptionPane.showMessageDialog(frame,"Invalid ID"); }
        }
    }

    private void saveToFile() {
        FileHandler.savePatients("patients.csv", manager);
        showStatus("Saved to patients.csv");
        JOptionPane.showMessageDialog(frame, "Saved.");
    }

    /** About dialog with system description. */
    private void showAbout() {
        String message = 
            "===========================================================\n" +
            "           HOSPITAL MANAGEMENT SYSTEM v2.0\n" +
            "===========================================================\n\n" +
            "SYSTEM OVERVIEW:\n" +
            "A comprehensive patient management system for hospital\n" +
            "administrators to efficiently manage patient records.\n\n" +
            "KEY FEATURES:\n" +
            "- Complete CRUD Operations (Add, View, Update, Delete)\n" +
            "- Priority Queue for Critical Patients (lowest health first)\n" +
            "- Automated Periodic Health Evaluations\n" +
            "- Reward & Penalty System (status-based point system)\n" +
            "- Dual Interface: GUI (Swing) + Text-Based Interface\n" +
            "- CSV File Persistence with Group Member Names\n\n" +
            "ALGORITHMS IMPLEMENTED:\n" +
            "- Quick Sort (O(n log n))  -> Sort by ID & Health Score\n" +
            "- Bubble Sort (O(n^2))     -> Sort by Patient Name\n" +
            "- Binary Search (O(log n)) -> Search by Patient ID\n" +
            "- Linear Search (O(n))     -> Search by Patient Name\n\n" +
            "TECHNICAL DETAILS:\n" +
            "- GUI Framework: Swing\n" +
            "- Data Storage: CSV (9-field format)\n" +
            "- Collections: ArrayList, PriorityQueue, HashMap\n\n" +
            "PROJECT INFORMATION:\n" +
            "- Subject: ICT711 - Programming and Algorithms\n" +
            "- Assessment: 4 (Individual Project)\n" +
            "===========================================================\n" +
            "(c) 2026 Hospital Management System\n" +
            "===========================================================";
        JOptionPane.showMessageDialog(frame, message, "About Hospital Management System", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showStatus(String msg) {
        statusLabel.setText(msg + " | Total: " + manager.getPatientCount());
    }

    private void clearFields(JTextField... fields) {
        for(JTextField f : fields) f.setText("");
    }
}
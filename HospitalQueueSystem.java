import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/* ======================================================
   MAIN CLASS
   ====================================================== */
public class HospitalQueueSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalFrame::new);
    }
}

/* ======================================================
   PATIENT CLASS
   ====================================================== */
class Patient {
    private static int counter = 1;

    private int queueNumber;
    private String name;
    private int age;
    private String condition;
    private String priority;
    private String status;
    private LocalDateTime arrivalTime;

    public Patient(String name, int age, String condition, String priority) {
        this.queueNumber = counter++;
        this.name = name;
        this.age = age;
        this.condition = condition;
        this.priority = priority;
        this.status = "Waiting";
        this.arrivalTime = LocalDateTime.now();
    }

    public int getQueueNumber() { return queueNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCondition() { return condition; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Object[] toRow() {
        return new Object[]{
                queueNumber,
                name,
                age,
                condition,
                priority,
                arrivalTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                status
        };
    }
}

/* ======================================================
   DOCTOR CLASS
   ====================================================== */
class Doctor {
    private String name;
    private boolean available = true;

    public Doctor(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

/* ======================================================
   GUI FRAME
   ====================================================== */
class HospitalFrame extends JFrame {

    private List<Patient> patientList = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField nameField, ageField, conditionField;
    private JComboBox<String> priorityBox;

    public HospitalFrame() {

        doctors.add(new Doctor("Dr. Sharma"));
        doctors.add(new Doctor("Dr. Mehta"));

        setTitle("Hospital Queue Automation System");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    /* ================= TOP PANEL ================= */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(25, 118, 210));

        JLabel title = new JLabel("HOSPITAL QUEUE & PATIENT FLOW MANAGEMENT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title);

        return panel;
    }

    /* ================= TABLE PANEL ================= */
    private JScrollPane createTablePanel() {

        String[] columns = {"Queue#", "Name", "Age", "Condition", "Priority", "Arrival", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.setDefaultRenderer(Object.class, new StatusColorRenderer());

        return new JScrollPane(table);
    }

    /* ================= BIG REGISTRATION PANEL ================= */
    private JPanel createBottomPanel() {

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Patient Registration"));
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(1000, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        nameField = new JTextField(18);
        ageField = new JTextField(10);
        conditionField = new JTextField(18);
        priorityBox = new JComboBox<>(new String[]{"Normal", "Emergency"});

        nameField.setFont(fieldFont);
        ageField.setFont(fieldFont);
        conditionField.setFont(fieldFont);
        priorityBox.setFont(fieldFont);

        JButton addBtn = new JButton("Register");
        JButton callBtn = new JButton("Call Next");
        JButton completeBtn = new JButton("Complete");
        JButton deleteBtn = new JButton("Delete");

        Dimension btnSize = new Dimension(140, 40);
        addBtn.setPreferredSize(btnSize);
        callBtn.setPreferredSize(btnSize);
        completeBtn.setPreferredSize(btnSize);
        deleteBtn.setPreferredSize(btnSize);

        addBtn.addActionListener(e -> registerPatient());
        callBtn.addActionListener(e -> callNextPatient());
        completeBtn.addActionListener(e -> completePatient());
        deleteBtn.addActionListener(e -> deletePatient());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 3;
        panel.add(ageField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Condition:"), gbc);
        gbc.gridx = 1;
        panel.add(conditionField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 3;
        panel.add(priorityBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(addBtn, gbc);
        gbc.gridx = 1;
        panel.add(callBtn, gbc);
        gbc.gridx = 2;
        panel.add(completeBtn, gbc);
        gbc.gridx = 3;
        panel.add(deleteBtn, gbc);

        return panel;
    }

    /* ================= REGISTER ================= */
    private void registerPatient() {
        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String condition = conditionField.getText().trim();
            String priority = priorityBox.getSelectedItem().toString();

            if (name.isEmpty() || condition.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            Patient p = new Patient(name, age, condition, priority);

            if (priority.equals("Emergency"))
                patientList.add(0, p);
            else
                patientList.add(p);

            refreshTable();

            nameField.setText("");
            ageField.setText("");
            conditionField.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric age!");
        }
    }

    /* ================= CALL NEXT ================= */
    private void callNextPatient() {

        for (Patient p : patientList) {
            if (p.getStatus().equals("Waiting")) {

                Optional<Doctor> availableDoctor =
                        doctors.stream().filter(Doctor::isAvailable).findFirst();

                if (availableDoctor.isPresent()) {
                    p.setStatus("In Consultation (" + availableDoctor.get().getName() + ")");
                    availableDoctor.get().setAvailable(false);
                    refreshTable();
                    return;
                } else {
                    JOptionPane.showMessageDialog(this, "No doctors available!");
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(this, "No waiting patients!");
    }

    /* ================= COMPLETE ================= */
    private void completePatient() {
        int row = table.getSelectedRow();

        if (row >= 0) {
            patientList.get(row).setStatus("Completed");
            doctors.forEach(d -> d.setAvailable(true));
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Select patient first!");
        }
    }

    /* ================= DELETE ================= */
    private void deletePatient() {
        int row = table.getSelectedRow();

        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this patient?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                patientList.remove(row);
                refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select patient to delete!");
        }
    }

    /* ================= REFRESH TABLE ================= */
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Patient p : patientList)
            tableModel.addRow(p.toRow());
    }
}

/* ======================================================
   STATUS COLOR RENDERER
   ====================================================== */
class StatusColorRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        String priority = table.getValueAt(row, 4).toString();
        String status = table.getValueAt(row, 6).toString();

        if (!isSelected) {

            if (status.contains("Completed"))
                c.setBackground(new Color(200, 255, 200));
            else if (status.contains("In Consultation"))
                c.setBackground(new Color(255, 255, 180));
            else if (priority.equals("Emergency"))
                c.setBackground(new Color(255, 200, 200));
            else
                c.setBackground(Color.WHITE);
        }

        return c;
    }
}
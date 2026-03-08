package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SupervisorPanel extends JPanel {
    private JTable leaveTable;
    private DefaultTableModel tableModel;
    private Employee currentSupervisor;
    private List<Employee> allEmployees;

    public SupervisorPanel(Employee supervisor, List<Employee> allEmployees) {
        this.currentSupervisor = supervisor;
        this.allEmployees = allEmployees;
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Supervisor - Manage Leave Requests"));

        String[] columns = {"Emp ID", "Start Date", "End Date", "Reason", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        leaveTable = new JTable(tableModel);
        
        loadLeaveRequests();

        JButton approveButton = new JButton("Approve");
        approveButton.setBackground(new Color(34, 139, 34));
        approveButton.setForeground(Color.WHITE);

        JButton denyButton = new JButton("Deny");
        denyButton.setBackground(new Color(178, 34, 34));
        denyButton.setForeground(Color.WHITE);

        approveButton.addActionListener(e -> processAction("APPROVED"));
        denyButton.addActionListener(e -> processAction("DENIED"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(approveButton);
        buttonPanel.add(denyButton);

        add(new JScrollPane(leaveTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void processAction(String newStatus) {
        int selectedRow = leaveTable.getSelectedRow();
        if (selectedRow != -1) {
            String empId = (String) tableModel.getValueAt(selectedRow, 0);
            tableModel.setValueAt(newStatus, selectedRow, 4);
            updateCSVStatus(empId, newStatus);
            JOptionPane.showMessageDialog(this, "Request for " + empId + " updated to " + newStatus);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row first.");
        }
    }

    private void updateCSVStatus(String empId, String newStatus) {
        // Simple implementation: Read all, update the specific line, write all back
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("leave_requests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5 && data[0].trim().equals(empId)) {
                    data[4] = newStatus;
                    line = String.join(",", data);
                }
                lines.add(line);
            }
        } catch (IOException e) { e.printStackTrace(); }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("leave_requests.csv"))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

   private void loadLeaveRequests() {
    // 1. Get the name of the supervisor to filter by
    String supervisorName = currentSupervisor.getLastName() + ", " + currentSupervisor.getFirstName();
    
    // 2. Filter: Only load requests from employees whose supervisor matches currentSupervisor
    for (Employee emp : allEmployees) {
        String empSupervisor = emp.getSupervisor(); // Ensure this returns "Last, First"
        
        // Normalize for comparison
        if (empSupervisor != null && empSupervisor.equalsIgnoreCase(supervisorName)) {
            // Add to tableModel only if they are a direct report
            // ... load their specific leave requests ...
        }
    }
}
}
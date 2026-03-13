package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.motorphpayroll.model.ISupervisor;

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
        String startDate = (String) tableModel.getValueAt(selectedRow, 1);

        // 1. Update the physical CSV file
        updateCSVStatus(empId, startDate, newStatus);
        
        // 2. Update the UI table immediately
        tableModel.setValueAt(newStatus, selectedRow, 4);
        
        JOptionPane.showMessageDialog(this, "Request for " + empId + " has been " + newStatus);
    } else {
        JOptionPane.showMessageDialog(this, "Please select a row first.");
    }
}

    private void updateCSVStatus(String empId, String startDate, String newStatus) {
    List<String> lines = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader("leave_requests.csv"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            // Match both ID and Start Date to ensure we update the correct request
            if (data.length >= 5 && data[0].trim().equals(empId) && data[1].trim().equals(startDate)) {
                data[4] = newStatus;
                line = String.join(",", data);
            }
            lines.add(line);
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading CSV: " + e.getMessage());
    }

    try (BufferedWriter bw = new BufferedWriter(new FileWriter("leave_requests.csv"))) {
        for (String line : lines) {
            bw.write(line);
            bw.newLine();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error writing to CSV: " + e.getMessage());
    }
}

   private void loadLeaveRequests() {
    tableModel.setRowCount(0); // Clear existing rows
    
    // 1. Identify which Employee IDs report to this supervisor
    List<String> subordinateIds = new ArrayList<>();
    String supervisorName = (currentSupervisor.getLastName() + "," + currentSupervisor.getFirstName()).replace(" ", "").toLowerCase();
    
    for (Employee emp : allEmployees) {
        String empSupervisor = emp.getSupervisor().replace("\"", "").replace(" ", "").toLowerCase();
        if (empSupervisor.equals(supervisorName)) {
            subordinateIds.add(emp.getEmployeeNumber());
        }
    }

    // 2. Read the leave_requests.csv file
    File file = new File("leave_requests.csv");
    if (!file.exists()) {
        return; // No file yet, so no requests to show
    }

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        // Skip header if you added one
        br.readLine(); 

        while ((line = br.readLine()) != null) {
            // Split by comma while ignoring commas inside quotes
            String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            
            if (data.length >= 5) {
                String empIdInRequest = data[0].replace("\"", "").trim();
                
                // 3. Only add to table if the requester is a subordinate of this supervisor
                if (subordinateIds.contains(empIdInRequest)) {
                    // Clean data for display
                    for (int i = 0; i < data.length; i++) {
                        data[i] = data[i].replace("\"", "");
                    }
                    tableModel.addRow(data);
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error loading leave requests: " + e.getMessage());
    }
}
}
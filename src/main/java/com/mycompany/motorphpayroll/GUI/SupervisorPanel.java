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
    private Employee currentUser;
    private List<Employee> allEmployees;

    public SupervisorPanel(Employee loggedInUser, List<Employee> allEmployees) {
        this.currentUser = loggedInUser;
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
        approveButton.addActionListener(e -> processAction("APPROVED"));

        JButton denyButton = new JButton("Deny");
        denyButton.setBackground(new Color(178, 34, 34));
        denyButton.setForeground(Color.WHITE);
        denyButton.addActionListener(e -> processAction("DENIED"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(approveButton);
        buttonPanel.add(denyButton);

        add(new JScrollPane(leaveTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadLeaveRequests() {
        tableModel.setRowCount(0);
        // Clean name for matching
        String supervisorName = (currentUser.getLastName() + "," + currentUser.getFirstName())
                                .toLowerCase().replace(" ", "");
        List<String> subordinateIds = new ArrayList<>();

        for (Employee emp : allEmployees) {
            String empSup = emp.getSupervisor() != null ? 
                            emp.getSupervisor().toLowerCase().replace("\"", "").replace(" ", "") : "";
            if (empSup.equals(supervisorName)) {
                subordinateIds.add(emp.getEmployeeNumber());
            }
        }

        File file = new File("leave_requests.csv");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length >= 5) {
                    String empId = data[0].replace("\"", "").trim();
                    if (subordinateIds.contains(empId)) {
                        tableModel.addRow(data);
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void processAction(String status) {
        int row = leaveTable.getSelectedRow();
        if (row != -1) {
            String id = (String) tableModel.getValueAt(row, 0);
            String date = (String) tableModel.getValueAt(row, 1);
            updateCSVStatus(id, date, status);
            loadLeaveRequests(); // Refresh table
        } else {
            JOptionPane.showMessageDialog(this, "Please select a request.");
        }
    }

    private void updateCSVStatus(String empId, String date, String status) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("leave_requests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length >= 5 && data[0].replace("\"", "").trim().equals(empId) && data[1].trim().equals(date)) {
                    data[4] = status;
                    line = String.join(",", data);
                }
                lines.add(line);
            }
        } catch (IOException e) { e.printStackTrace(); }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("leave_requests.csv"))) {
            for (String line : lines) { bw.write(line); bw.newLine(); }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
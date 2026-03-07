package com.mycompany.motorphpayroll.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.Vector;

public class SupervisorPanel extends JPanel {
    private JTable leaveTable;
    private DefaultTableModel tableModel;
    private JButton approveButton;

    public SupervisorPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Supervisor - Pending Leave Requests"));

        // Table Setup
        String[] columns = {"Emp ID", "Start Date", "End Date", "Reason", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        leaveTable = new JTable(tableModel);
        
        loadLeaveRequests();

        approveButton = new JButton("Approve Selected Leave");
        approveButton.setBackground(new Color(34, 139, 34)); // Forest Green
        approveButton.setForeground(Color.WHITE);

        add(new JScrollPane(leaveTable), BorderLayout.CENTER);
        add(approveButton, BorderLayout.SOUTH);

        // Action Listener for Approval
        approveButton.addActionListener(e -> {
            int selectedRow = leaveTable.getSelectedRow();
            if (selectedRow != -1) {
                String empId = (String) tableModel.getValueAt(selectedRow, 0);
                tableModel.setValueAt("APPROVED", selectedRow, 4);
                JOptionPane.showMessageDialog(this, "Leave for " + empId + " has been approved.");
                // Note: In a full app, you'd call a method to update the CSV file here
            } else {
                JOptionPane.showMessageDialog(this, "Please select a request to approve.");
            }
        });
    }

    private void loadLeaveRequests() {
        // This simulates reading from the leave_requests.csv we created earlier
        try (BufferedReader br = new BufferedReader(new FileReader("leave_requests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5 && data[4].equalsIgnoreCase("PENDING")) {
                    tableModel.addRow(data);
                }
            }
        } catch (IOException e) {
            // If file doesn't exist yet, just show empty table
        }
    }
}
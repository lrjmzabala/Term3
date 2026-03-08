package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LeaveApprovalPanel extends JPanel {
    private JTable approvalTable;
    private DefaultTableModel tableModel;

    public LeaveApprovalPanel(Employee supervisor) {
        setLayout(new BorderLayout());
        
        String[] cols = {"Emp ID", "Name", "Dates", "Reason", "Action"};
        tableModel = new DefaultTableModel(cols, 0);
        approvalTable = new JTable(tableModel);
        
        JButton btnApprove = new JButton("Approve");
        JButton btnDeny = new JButton("Deny");
        
        // Add listeners to trigger status updates in your CSV/Database
        btnApprove.addActionListener(e -> updateLeaveStatus("APPROVED"));
        btnDeny.addActionListener(e -> updateLeaveStatus("DENIED"));

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnApprove);
        btnPanel.add(btnDeny);

        add(new JScrollPane(approvalTable), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        
        loadPendingLeaves(supervisor);
    }

    private void loadPendingLeaves(Employee supervisor) {
        // Filter your leave CSV to only show requests 
        // where the requestor's supervisor is this 'supervisor'
    }

    private void updateLeaveStatus(String status) {
        int row = approvalTable.getSelectedRow();
        if (row != -1) {
            // Logic to update leaves.csv with the new status
        }
    }
}
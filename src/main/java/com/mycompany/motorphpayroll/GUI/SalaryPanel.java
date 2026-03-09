package com.mycompany.motorphpayroll.GUI;

import javax.swing.*;
import java.awt.*;

public class SalaryPanel extends JPanel {

    private JTextField startDateField, endDateField;
    private JLabel salaryLabel;
    private JButton viewSalaryButton;
    private JTextArea breakdownArea; // New area for the detailed breakdown

    public SalaryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- TOP: Input Section ---
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Start Date (MM/DD/YYYY):"));
        startDateField = new JTextField(10);
        inputPanel.add(startDateField);

        inputPanel.add(new JLabel("End Date (MM/DD/YYYY):"));
        endDateField = new JTextField(10);
        inputPanel.add(endDateField);

        viewSalaryButton = new JButton("View Salary");
        inputPanel.add(viewSalaryButton);

        salaryLabel = new JLabel("💰 Net Salary: PHP 0.00");
        salaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputPanel.add(salaryLabel);

        add(inputPanel, BorderLayout.NORTH);

        // --- CENTER: Breakdown Section ---
        breakdownArea = new JTextArea();
        breakdownArea.setEditable(false);
        breakdownArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Monospaced keeps columns aligned
        
        JScrollPane scrollPane = new JScrollPane(breakdownArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Salary Breakdown Details"));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Updates the UI with the full calculation results.
     */
    public void displaySalaryDetails(double netSalary, String breakdownText) {
        salaryLabel.setText("💰 Net Salary: PHP " + String.format("%,.2f", netSalary));
        breakdownArea.setText(breakdownText);
    }

    public String getStartDate() {
        return startDateField.getText();
    }

    public String getEndDate() {
        return endDateField.getText();
    }

    public JButton getViewSalaryButton() {
        return viewSalaryButton;
    }
}
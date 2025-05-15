package com.mycompany.motorphpayroll.GUI;

import javax.swing.*;
import java.awt.*;

public class SalaryPanel extends JPanel {

    private JTextField startDateField, endDateField;
    private JLabel salaryLabel;
    private JButton viewSalaryButton;

    public SalaryPanel() {
        setLayout(new GridLayout(3, 2)); // Simple Grid layout

        add(new JLabel("Start Date (MM/DD/YYYY):"));
        startDateField = new JTextField(10);
        add(startDateField);

        add(new JLabel("End Date (MM/DD/YYYY):"));
        endDateField = new JTextField(10);
        add(endDateField);

        viewSalaryButton = new JButton("View Salary");
        add(viewSalaryButton);

        salaryLabel = new JLabel("💰 Net Salary: PHP 0.00");
        add(salaryLabel);
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

    public void updateSalaryLabel(double netSalary) {
        salaryLabel.setText("💰 Net Salary: PHP " + String.format("%,.2f", netSalary));
    }
}

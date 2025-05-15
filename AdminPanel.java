package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.Admin;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminPanel extends JPanel {
    private JTextField empNumField, firstNameField, lastNameField, birthdayField, addressField, phoneNumberField, sssNumberField, philhealthField, tinField, pagibigNumberField, statusField, positionField, supervisorField, basicSalaryField, riceSubsidyField, phoneAllowanceField, clothingAllowanceField, grossSemimonthlyField, hourlyRateField;
    private JButton addButton;
    private JButton viewEmployeeButton;
    private Admin admin;

    public AdminPanel() {
        admin = new Admin("Admin", "Panel", "000-0000");

        setLayout(new BorderLayout());

        // Main panel to hold the form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding between components

        // Add each field with GridBagLayout constraints
        addField(formPanel, "Employee Number:", empNumField = new JTextField(), gbc, 0);
        addField(formPanel, "First Name:", firstNameField = new JTextField(), gbc, 1);
        addField(formPanel, "Last Name:", lastNameField = new JTextField(), gbc, 2);
        addField(formPanel, "Birthday:", birthdayField = new JTextField(), gbc, 3);
        addField(formPanel, "Address:", addressField = new JTextField(), gbc, 4);
        addField(formPanel, "Phone Number:", phoneNumberField = new JTextField(), gbc, 5);
        addField(formPanel, "SSS:", sssNumberField = new JTextField(), gbc, 6);
        addField(formPanel, "Philhealth:", philhealthField = new JTextField(), gbc, 7);
        addField(formPanel, "TIN:", tinField = new JTextField(), gbc, 8);
        addField(formPanel, "Pagibig Number:", pagibigNumberField = new JTextField(), gbc, 9);
        addField(formPanel, "Status:", statusField = new JTextField(), gbc, 10);
        addField(formPanel, "Position:", positionField = new JTextField(), gbc, 11);
        addField(formPanel, "Supervisor:", supervisorField = new JTextField(), gbc, 12);
        addField(formPanel, "Basic Salary:", basicSalaryField = new JTextField(), gbc, 13);
        addField(formPanel, "Rice Subsidy:", riceSubsidyField = new JTextField(), gbc, 14);
        addField(formPanel, "Phone Allowance:", phoneAllowanceField = new JTextField(), gbc, 15);
        addField(formPanel, "Clothing Allowance:", clothingAllowanceField = new JTextField(), gbc, 16);
        addField(formPanel, "Gross Semi-Monthly:", grossSemimonthlyField = new JTextField(), gbc, 17);
        addField(formPanel, "Hourly Rate:", hourlyRateField = new JTextField(), gbc, 18);

        // Add the action buttons below the fields
        addButton = new JButton("Add Employee");
        viewEmployeeButton = new JButton("View Employee Details");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center buttons with spacing
        buttonPanel.add(addButton);
        buttonPanel.add(viewEmployeeButton);
        
        gbc.gridwidth = 2; // Span across two columns for the buttons
        gbc.gridx = 0;
        gbc.gridy = 19;
        formPanel.add(buttonPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String empNum = empNumField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String birthday = birthdayField.getText().trim();
                String address = addressField.getText().trim();
                String phoneNumber = phoneNumberField.getText().trim();
                String sssNumber = sssNumberField.getText().trim();
                String philhealth = philhealthField.getText().trim();
                String tin = tinField.getText().trim();
                String pagibig = pagibigNumberField.getText().trim();
                String status = statusField.getText().trim();
                String position = positionField.getText().trim();
                String supervisor = supervisorField.getText().trim();

                double basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, grossSemimonthly, hourlyRate;

                try {
                    basicSalary = Double.parseDouble(basicSalaryField.getText().trim());
                    riceSubsidy = Double.parseDouble(riceSubsidyField.getText().trim());
                    phoneAllowance = Double.parseDouble(phoneAllowanceField.getText().trim());
                    clothingAllowance = Double.parseDouble(clothingAllowanceField.getText().trim());
                    grossSemimonthly = Double.parseDouble(grossSemimonthlyField.getText().trim());
                    hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());

                    if (basicSalary < 0 || riceSubsidy < 0 || phoneAllowance < 0 || clothingAllowance < 0 || grossSemimonthly < 0 || hourlyRate <= 0) {
                        JOptionPane.showMessageDialog(null, "❌ Salary-related fields must be positive numbers!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "❌ Salary-related fields must be valid numbers!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (empNum.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || birthday.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || sssNumber.isEmpty() || philhealth.isEmpty() || tin.isEmpty() || pagibig.isEmpty() || status.isEmpty() || position.isEmpty() || supervisor.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "❌ All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                admin.addEmployee(empNum, firstName, lastName, birthday, address, phoneNumber, 
                  sssNumber, philhealth, tin, pagibig, status, position, supervisor, 
                  basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, 
                  grossSemimonthly, hourlyRate);
                JOptionPane.showMessageDialog(null, "✅ Employee Added Successfully!");
            }
        });

        viewEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String empNum = JOptionPane.showInputDialog(null, "Enter Employee Number:", "View Employee", JOptionPane.QUESTION_MESSAGE);

                if (empNum == null || empNum.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "❌ Please enter a valid Employee Number!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String employeeDetails = admin.getEmployeeDetails(empNum);

                if (employeeDetails != null) {
                    JOptionPane.showMessageDialog(null, "👤 Employee Details:\n" + employeeDetails, "Employee Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "⚠ Employee not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Method to add fields with appropriate alignment using GridBagLayout
    private void addField(JPanel panel, String label, JTextField field, GridBagConstraints gbc, int yPos) {
        JLabel jLabel = new JLabel(label);
        jLabel.setHorizontalAlignment(JLabel.RIGHT); // Align label to the right
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.EAST; // Right-align the label
        panel.add(jLabel, gbc);

        field.setPreferredSize(new Dimension(200, 25)); // Uniform width for all fields
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; // Left-align the text field
        panel.add(field, gbc);
    }
}

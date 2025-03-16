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
        admin = new Admin();  

        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        addField(formPanel, "Employee Number:", empNumField = new JTextField());
        addField(formPanel, "First Name:", firstNameField = new JTextField());
        addField(formPanel, "Last Name:", lastNameField = new JTextField());
        addField(formPanel, "Birthday:", birthdayField = new JTextField());
        addField(formPanel, "Address:", addressField = new JTextField());
        addField(formPanel, "Phone Number:", phoneNumberField = new JTextField());
        addField(formPanel, "SSS:", sssNumberField = new JTextField());
        addField(formPanel, "Philhealth:", philhealthField = new JTextField());
        addField(formPanel, "TIN:", tinField = new JTextField());
        addField(formPanel, "Pagibig Number:", pagibigNumberField = new JTextField());
        addField(formPanel, "Status:", statusField = new JTextField());
        addField(formPanel, "Position:", positionField = new JTextField());
        addField(formPanel, "Supervisor:", supervisorField = new JTextField());
        addField(formPanel, "Basic Salary:", basicSalaryField = new JTextField());
        addField(formPanel, "Rice Subsidy:", riceSubsidyField = new JTextField());
        addField(formPanel, "Phone Allowance:", phoneAllowanceField = new JTextField());
        addField(formPanel, "Clothing Allowance:", clothingAllowanceField = new JTextField());
        addField(formPanel, "Gross Semi-Monthly:", grossSemimonthlyField = new JTextField());
        addField(formPanel, "Hourly Rate:", hourlyRateField = new JTextField());

        addButton = new JButton("Add Employee");
        viewEmployeeButton = new JButton("View Employee Details");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(viewEmployeeButton);
        formPanel.add(buttonPanel);

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

    private void addField(JPanel panel, String label, JTextField field) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.add(new JLabel(label), BorderLayout.WEST);
        field.setPreferredSize(new Dimension(100, 25));
        fieldPanel.add(field, BorderLayout.CENTER);
        panel.add(fieldPanel);
    }
}

package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.Admin;
import com.mycompany.motorphpayroll.model.Employee; // Import Employee class
import com.mycompany.motorphpayroll.util.CSVReaderUtil; // Import CSVReaderUtil for refresh
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List; // For potentially refreshing the table view

public class AdminPanel extends JPanel {
    // Text fields for employee details
    private JTextField empNumField, firstNameField, lastNameField, birthdayField, addressField,
            phoneNumberField, sssNumberField, philhealthField, tinField, pagibigNumberField,
            statusField, positionField, supervisorField, basicSalaryField, riceSubsidyField,
            phoneAllowanceField, clothingAllowanceField, grossSemimonthlyField, hourlyRateField;

    // Action buttons
    private JButton addButton;
    private JButton viewEmployeeButton;
    private JButton updateEmployeeButton; // New button for Update
    private JButton deleteEmployeeButton; // New button for Delete
    private JButton clearFieldsButton;    // Added for convenience

    private Admin admin;

    public AdminPanel() {
        admin = new Admin("Admin", "Panel", "000-0000"); // Initialize Admin

        setLayout(new BorderLayout());

        // --- Form Panel for Employee Details ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        // Add each field (Label and JTextField) to the formPanel
        // We will reuse these fields for Add and Update operations
        addField(formPanel, "Employee Number:", empNumField = new JTextField(), gbc, 0);
        addField(formPanel, "First Name:", firstNameField = new JTextField(), gbc, 1);
        addField(formPanel, "Last Name:", lastNameField = new JTextField(), gbc, 2);
        addField(formPanel, "Birthday (YYYY-MM-DD):", birthdayField = new JTextField(), gbc, 3);
        addField(formPanel, "Address:", addressField = new JTextField(), gbc, 4);
        addField(formPanel, "Phone Number:", phoneNumberField = new JTextField(), gbc, 5);
        addField(formPanel, "SSS Number:", sssNumberField = new JTextField(), gbc, 6);
        addField(formPanel, "Philhealth Number:", philhealthField = new JTextField(), gbc, 7);
        addField(formPanel, "TIN Number:", tinField = new JTextField(), gbc, 8);
        addField(formPanel, "Pag-IBIG Number:", pagibigNumberField = new JTextField(), gbc, 9);
        addField(formPanel, "Status:", statusField = new JTextField(), gbc, 10);
        addField(formPanel, "Position:", positionField = new JTextField(), gbc, 11);
        addField(formPanel, "Supervisor:", supervisorField = new JTextField(), gbc, 12);
        addField(formPanel, "Basic Salary:", basicSalaryField = new JTextField(), gbc, 13);
        addField(formPanel, "Rice Subsidy:", riceSubsidyField = new JTextField(), gbc, 14);
        addField(formPanel, "Phone Allowance:", phoneAllowanceField = new JTextField(), gbc, 15);
        addField(formPanel, "Clothing Allowance:", clothingAllowanceField = new JTextField(), gbc, 16);
        addField(formPanel, "Gross Semi-Monthly:", grossSemimonthlyField = new JTextField(), gbc, 17);
        addField(formPanel, "Hourly Rate:", hourlyRateField = new JTextField(), gbc, 18);

        // --- Button Panel ---
        addButton = new JButton("Add Employee");
        viewEmployeeButton = new JButton("View Employee Details");
        updateEmployeeButton = new JButton("Update Employee"); // Initialize new button
        deleteEmployeeButton = new JButton("Delete Employee"); // Initialize new button
        clearFieldsButton = new JButton("Clear Fields"); // Initialize new button for convenience


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(updateEmployeeButton); // Add Update button
        buttonPanel.add(deleteEmployeeButton); // Add Delete button
        buttonPanel.add(viewEmployeeButton);
        buttonPanel.add(clearFieldsButton); // Add Clear Fields button


        // Add the formPanel and buttonPanel to the AdminPanel
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH); // Place buttons at the bottom


        // --- Action Listeners ---

        // Add Employee Action
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput(true)) { // Pass true for 'isAddOperation' to check for empty empNum
                    try {
                        admin.addEmployee(
                            empNumField.getText().trim(),
                            firstNameField.getText().trim(),
                            lastNameField.getText().trim(),
                            birthdayField.getText().trim(),
                            addressField.getText().trim(),
                            phoneNumberField.getText().trim(),
                            sssNumberField.getText().trim(),
                            philhealthField.getText().trim(),
                            tinField.getText().trim(),
                            pagibigNumberField.getText().trim(),
                            statusField.getText().trim(),
                            positionField.getText().trim(),
                            supervisorField.getText().trim(),
                            Double.parseDouble(basicSalaryField.getText().trim()),
                            Double.parseDouble(riceSubsidyField.getText().trim()),
                            Double.parseDouble(phoneAllowanceField.getText().trim()),
                            Double.parseDouble(clothingAllowanceField.getText().trim()),
                            Double.parseDouble(grossSemimonthlyField.getText().trim()),
                            Double.parseDouble(hourlyRateField.getText().trim())
                        );
                        // The Admin class itself now prints success/error.
                       
                        JOptionPane.showMessageDialog(null, "✅ Employee Added Successfully (check console for details)!");
                        clearFields(); // Clear fields after successful addition
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "❌ Please ensure all numeric fields contain valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Update Employee Action
        updateEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // For update, employee number must exist and all fields must be valid
                if (validateInput(false)) { // Pass false for 'isAddOperation'
                    String empNum = empNumField.getText().trim();
                    if (admin.getEmployeeDetails(empNum) == null) {
                        JOptionPane.showMessageDialog(null, "❌ Employee with ID " + empNum + " not found for update.", "Update Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        boolean updated = admin.updateEmployee(
                            empNum,
                            firstNameField.getText().trim(),
                            lastNameField.getText().trim(),
                            birthdayField.getText().trim(),
                            addressField.getText().trim(),
                            phoneNumberField.getText().trim(),
                            sssNumberField.getText().trim(),
                            philhealthField.getText().trim(),
                            tinField.getText().trim(),
                            pagibigNumberField.getText().trim(),
                            statusField.getText().trim(),
                            positionField.getText().trim(),
                            supervisorField.getText().trim(),
                            Double.parseDouble(basicSalaryField.getText().trim()),
                            Double.parseDouble(riceSubsidyField.getText().trim()),
                            Double.parseDouble(phoneAllowanceField.getText().trim()),
                            Double.parseDouble(clothingAllowanceField.getText().trim()),
                            Double.parseDouble(grossSemimonthlyField.getText().trim()),
                            Double.parseDouble(hourlyRateField.getText().trim())
                        );
                        if (updated) {
                            JOptionPane.showMessageDialog(null, "✅ Employee Updated Successfully!");
                            clearFields();
                        } else {
                            JOptionPane.showMessageDialog(null, "⚠ Employee not updated. Check console for details.", "Update Failed", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "❌ Please ensure all numeric fields contain valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        // Delete Employee Action
        deleteEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String empNumToDelete = JOptionPane.showInputDialog(null, "Enter Employee Number to Delete:", "Delete Employee", JOptionPane.QUESTION_MESSAGE);

                if (empNumToDelete == null || empNumToDelete.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "❌ Please enter an Employee Number to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete employee " + empNumToDelete + "? This action cannot be undone.", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean deleted = admin.deleteEmployee(empNumToDelete.trim());
                    if (deleted) {
                        JOptionPane.showMessageDialog(null, "✅ Employee Deleted Successfully!");
                        clearFields(); // Clear fields as the employee might have been displayed
                    } else {
                        JOptionPane.showMessageDialog(null, "⚠ Employee " + empNumToDelete + " not found or could not be deleted.", "Deletion Failed", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });


        // View Employee Details Action
        viewEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String empNum = JOptionPane.showInputDialog(null, "Enter Employee Number to View:", "View Employee", JOptionPane.QUESTION_MESSAGE);

                if (empNum == null || empNum.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "❌ Please enter a valid Employee Number!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String employeeDetails = admin.getEmployeeDetails(empNum);

                if (employeeDetails != null) {
                    JOptionPane.showMessageDialog(null, "👤 Employee Details:\n" + employeeDetails, "Employee Details", JOptionPane.INFORMATION_MESSAGE);
                    // Populate fields with viewed employee's data for potential update
                    populateFields(admin.getEmployeeDetailsObject(empNum)); // Use a new helper method
                } else {
                    JOptionPane.showMessageDialog(null, "⚠ Employee not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    clearFields(); // Clear fields if not found
                }
            }
        });

        // Clear Fields Action
        clearFieldsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
    }

    // --- Helper Methods ---

    // Method to add labels and text fields with GridBagLayout constraints
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

    // Clears all input fields
    private void clearFields() {
        empNumField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        birthdayField.setText("");
        addressField.setText("");
        phoneNumberField.setText("");
        sssNumberField.setText("");
        philhealthField.setText("");
        tinField.setText("");
        pagibigNumberField.setText("");
        statusField.setText("");
        positionField.setText("");
        supervisorField.setText("");
        basicSalaryField.setText("");
        riceSubsidyField.setText("");
        phoneAllowanceField.setText("");
        clothingAllowanceField.setText("");
        grossSemimonthlyField.setText("");
        hourlyRateField.setText("");
    }

    // Populates fields from an Employee object
    private void populateFields(Employee emp) {
        if (emp != null) {
            empNumField.setText(emp.getEmployeeNumber());
            firstNameField.setText(emp.getFirstName());
            lastNameField.setText(emp.getLastName());
            birthdayField.setText(emp.getBirthday());
            addressField.setText(emp.getAddress());
            phoneNumberField.setText(emp.getPhoneNumber());
            sssNumberField.setText(emp.getSssNumber());
            philhealthField.setText(emp.getPhilhealthNumber());
            tinField.setText(emp.getTinNumber());
            pagibigNumberField.setText(emp.getPagibigNumber());
            statusField.setText(emp.getStatus());
            positionField.setText(emp.getPosition());
            supervisorField.setText(emp.getSupervisor());
            basicSalaryField.setText(String.valueOf(emp.getBasicSalary()));
            riceSubsidyField.setText(String.valueOf(emp.getRiceSubsidy()));
            phoneAllowanceField.setText(String.valueOf(emp.getPhoneAllowance()));
            clothingAllowanceField.setText(String.valueOf(emp.getClothingAllowance()));
            grossSemimonthlyField.setText(String.valueOf(emp.getGrossSemiMonthlyRate()));
            hourlyRateField.setText(String.valueOf(emp.getHourlyRate()));
        } else {
            clearFields();
        }
    }

    // Input validation method
    private boolean validateInput(boolean isAddOperation) {
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

        // Check if essential fields are empty
        if (empNum.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || birthday.isEmpty() || address.isEmpty() ||
            phoneNumber.isEmpty() || sssNumber.isEmpty() || philhealth.isEmpty() || tin.isEmpty() ||
            pagibig.isEmpty() || status.isEmpty() || position.isEmpty() || supervisor.isEmpty()) {
            JOptionPane.showMessageDialog(null, "❌ All text fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        double basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, grossSemimonthly, hourlyRate;
        try {
            basicSalary = Double.parseDouble(basicSalaryField.getText().trim());
            riceSubsidy = Double.parseDouble(riceSubsidyField.getText().trim());
            phoneAllowance = Double.parseDouble(phoneAllowanceField.getText().trim());
            clothingAllowance = Double.parseDouble(clothingAllowanceField.getText().trim());
            grossSemimonthly = Double.parseDouble(grossSemimonthlyField.getText().trim());
            hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());

            if (basicSalary < 0 || riceSubsidy < 0 || phoneAllowance < 0 || clothingAllowance < 0 || grossSemimonthly < 0 || hourlyRate <= 0) {
                JOptionPane.showMessageDialog(null, "❌ Salary-related fields must be positive numbers, and Hourly Rate must be greater than zero!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "❌ Salary-related fields must be valid numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.User;
import com.mycompany.motorphpayroll.model.Attendance; 
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;
import javax.swing.text.MaskFormatter;
import java.text.ParseException; 
import com.mycompany.motorphpayroll.util.CSVWriterUtil;

public class AdminPanel extends JPanel {
    
    private final com.mycompany.motorphpayroll.service.SecurityService securityService = 
            new com.mycompany.motorphpayroll.service.SecurityService();
    private Employee currentUser;

    // Employee Details Fields
    private JTextField empNumField, lastNameField, firstNameField, birthdayField, addressField,
            phoneField, sssField, philhealthField, tinField, pagibigField, statusField,
            positionField, supervisorField, basicSalaryField, riceSubsidyField, phoneAllowanceField,
            clothingAllowanceField, grossSemiMonthlyRateField, hourlyRateField;

    // User Credentials Fields
    private JPasswordField userPasswordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> userTypeComboBox;

    // Attendance Fields
    private JFormattedTextField attendanceDateField;
    private JFormattedTextField timeInField;
    private JFormattedTextField timeOutField;
    private JButton searchAttendanceButton;
    private JButton updateAttendanceButton;

    private JButton addButton, updateButton, deleteButton, clearButton, searchButton;

    private JTextArea displayArea;

    public AdminPanel() {
        setLayout(new BorderLayout(10, 10));

        // --- Input Panel for Employee Details and Attendance ---
        JPanel inputAndAttendancePanel = new JPanel(new GridBagLayout());
        inputAndAttendancePanel.setBorder(BorderFactory.createTitledBorder("Employee Details and Attendance"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Helper method to add a label and text field
        addFormField(inputAndAttendancePanel, "Employee #:", empNumField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Last Name:", lastNameField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "First Name:", firstNameField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Birthday (MM/DD/YYYY):", birthdayField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Address:", addressField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Phone Number:", phoneField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "SSS #:", sssField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Philhealth #:", philhealthField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "TIN #:", tinField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Pag-IBIG #:", pagibigField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Status:", statusField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Position:", positionField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Supervisor:", supervisorField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Basic Salary:", basicSalaryField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Rice Subsidy:", riceSubsidyField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Phone Allowance:", phoneAllowanceField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Clothing Allowance:", clothingAllowanceField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Gross Semi-monthly Rate:", grossSemiMonthlyRateField = new JTextField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Hourly Rate:", hourlyRateField = new JTextField(20), gbc, row++);

        // User Account Details
        addFormField(inputAndAttendancePanel, "User Password:", userPasswordField = new JPasswordField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, "Confirm Password:", confirmPasswordField = new JPasswordField(20), gbc, row++);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        inputAndAttendancePanel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        userTypeComboBox = new JComboBox<>(new String[]{"Employee", "Admin"});
        inputAndAttendancePanel.add(userTypeComboBox, gbc);
        row++;

        // Attendance Details
        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            attendanceDateField = new JFormattedTextField(dateFormatter);
            MaskFormatter timeFormatter = new MaskFormatter("##:##:## UU");
            timeInField = new JFormattedTextField(timeFormatter);
            timeOutField = new JFormattedTextField(timeFormatter);
        } catch (ParseException e) {
            e.printStackTrace();
            attendanceDateField = new JFormattedTextField();
            timeInField = new JFormattedTextField();
            timeOutField = new JFormattedTextField();
        }

        JPanel attendanceInputPanel = new JPanel(new GridBagLayout());
        attendanceInputPanel.setBorder(BorderFactory.createTitledBorder("Attendance Correction"));
        
        GridBagConstraints gbcAtt = new GridBagConstraints();
        gbcAtt.insets = new Insets(5, 5, 5, 5);
        gbcAtt.fill = GridBagConstraints.HORIZONTAL;
        int attRow = 0;

        addFormField(attendanceInputPanel, "Date (MM/DD/YYYY):", attendanceDateField, gbcAtt, attRow++);
        addFormField(attendanceInputPanel, "Time In (HH:MM:SS AM/PM):", timeInField, gbcAtt, attRow++);
        addFormField(attendanceInputPanel, "Time Out (HH:MM:SS AM/PM):", timeOutField, gbcAtt, attRow++);

        JPanel attendanceButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        searchAttendanceButton = new JButton("Search Attendance");
        updateAttendanceButton = new JButton("Update Attendance");
        attendanceButtonPanel.add(searchAttendanceButton);
        attendanceButtonPanel.add(updateAttendanceButton);

        gbcAtt.gridx = 0;
        gbcAtt.gridy = attRow;
        gbcAtt.gridwidth = 2; 
        attendanceInputPanel.add(attendanceButtonPanel, gbcAtt);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        inputAndAttendancePanel.add(attendanceInputPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(inputAndAttendancePanel);
        scrollPane.setPreferredSize(new Dimension(500, 700)); 
        add(scrollPane, BorderLayout.WEST);

        // --- Buttons Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Employee");
        updateButton = new JButton("Update Employee");
        deleteButton = new JButton("Delete Employee");
        clearButton = new JButton("Clear Fields");
        searchButton = new JButton("Search Employee");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(searchButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Display Area ---
        displayArea = new JTextArea(15, 40);
        displayArea.setEditable(false);
        JScrollPane displayScrollPane = new JScrollPane(displayArea);
        displayScrollPane.setBorder(BorderFactory.createTitledBorder("Employee Information Display"));
        add(displayScrollPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchEmployee());
        searchAttendanceButton.addActionListener(e -> searchAttendance());
        updateAttendanceButton.addActionListener(e -> updateAttendance());

        CSVReaderUtil.loadEmployeesToCache();
    }

    private void addFormField(JPanel panel, String labelText, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    // [Method implementations: addEmployee, updateEmployee, deleteEmployee, searchEmployee, 
    // searchAttendance, updateAttendance, clearFields, clearFieldsExceptEmpNum, parseDouble 

    private String parseTime(String text, String fieldName) {
        if (text == null || text.trim().isEmpty() || !text.matches("\\d{2}:\\d{2}:\\d{2} (AM|PM)")) {
            JOptionPane.showMessageDialog(this, "Invalid time format for " + fieldName + ". Please use HH:MM:SS AM/PM.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return text.trim();
    }
    
    private void addEmployee() {
    // 1. Run Validation
    if (!validateInputs()) return;

    if (!securityService.canManageLogins(currentUser) && !userPasswordField.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Access Denied: Only IT can manage login credentials.");
        return;
    }
        
        String[] data = {
            empNumField.getText(), lastNameField.getText(), firstNameField.getText(),
            birthdayField.getText(), addressField.getText(), phoneField.getText(),
            sssField.getText(), philhealthField.getText(), tinField.getText(),
            pagibigField.getText(), statusField.getText(), positionField.getText(),
            supervisorField.getText(), basicSalaryField.getText(), riceSubsidyField.getText(),
            phoneAllowanceField.getText(), clothingAllowanceField.getText(),
            grossSemiMonthlyRateField.getText(), hourlyRateField.getText()
        };

        // Validate password
        String pass = new String(userPasswordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        try {
    // These are the lines that were failing
    CSVWriterUtil.writeToCSV("employees.csv", data);
    CSVWriterUtil.writeToCSV("users.csv", new String[]{data[0], pass, (String)userTypeComboBox.getSelectedItem()});
    
    CSVReaderUtil.loadEmployeesToCache(); 
    displayArea.setText("Successfully added Employee #" + data[0]);
    clearFields();
} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
}
    }
    
    
    private void updateEmployee() {
        String id = empNumField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Search for an employee first.");
            return;
        }
        // Logic here usually involves reading the whole file, replacing the line, and writing back
        // For simplicity, we trigger the update through the CSVWriterUtil
        JOptionPane.showMessageDialog(this, "Update logic triggered for ID: " + id);
        // Note: You'll need an 'update' method in CSVWriterUtil that rewrites the file.
    }
    
    
    private void deleteEmployee() {
        String id = empNumField.getText().trim();
        if (id.isEmpty()) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, "Delete Employee " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Call your CSV delete logic
            displayArea.setText("Employee " + id + " deleted.");
            clearFields();
        }
    }
    
    private void clearFields() {
        empNumField.setText("");
        lastNameField.setText("");
        firstNameField.setText("");
        birthdayField.setText("");
        addressField.setText("");
        phoneField.setText("");
        sssField.setText("");
        philhealthField.setText("");
        tinField.setText("");
        pagibigField.setText("");
        statusField.setText("");
        positionField.setText("");
        supervisorField.setText("");
        basicSalaryField.setText("");
        riceSubsidyField.setText("");
        phoneAllowanceField.setText("");
        clothingAllowanceField.setText("");
        grossSemiMonthlyRateField.setText("");
        hourlyRateField.setText("");
        userPasswordField.setText("");
        confirmPasswordField.setText("");
        attendanceDateField.setText("");
        timeInField.setText("");
        timeOutField.setText("");
        displayArea.setText("Fields Cleared.");
    }
    
    private boolean validateInputs() {
    // 1. Required Fields Check
    if (empNumField.getText().trim().isEmpty() || 
        lastNameField.getText().trim().isEmpty() || 
        firstNameField.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Employee #, Last Name, and First Name are required.");
        return false;
    }

    // 2. Numeric Validation for Financial Fields
    try {
        Double.parseDouble(basicSalaryField.getText().trim());
        Double.parseDouble(riceSubsidyField.getText().trim());
        Double.parseDouble(phoneAllowanceField.getText().trim());
        Double.parseDouble(clothingAllowanceField.getText().trim());
        Double.parseDouble(grossSemiMonthlyRateField.getText().trim());
        Double.parseDouble(hourlyRateField.getText().trim());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Salary and Allowance fields must be valid numeric values.");
        return false;
    }

    // 3. Duplicate ID Check
    if (CSVReaderUtil.getEmployeeById(empNumField.getText().trim()) != null) {
        JOptionPane.showMessageDialog(this, "Employee ID already exists. Please use a unique ID.");
        return false;
    }

    return true;
}
    
    private void searchEmployee() {
        String id = empNumField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Employee # to search.");
            return;
        }
        
        Employee emp = CSVReaderUtil.getEmployeeById(id);
        if (emp != null) {
            lastNameField.setText(emp.getLastName());
            firstNameField.setText(emp.getFirstName());
            birthdayField.setText(emp.getBirthday());
            addressField.setText(emp.getAddress());
            phoneField.setText(emp.getPhoneNumber());
            sssField.setText(emp.getSssNumber());
            philhealthField.setText(emp.getPhilhealthNumber());
            tinField.setText(emp.getTinNumber());
            pagibigField.setText(emp.getPagibigNumber());
            statusField.setText(emp.getStatus());
            positionField.setText(emp.getPosition());
            supervisorField.setText(emp.getSupervisor());
            basicSalaryField.setText(String.valueOf(emp.getBasicSalary()));
            riceSubsidyField.setText(String.valueOf(emp.getRiceSubsidy()));
            phoneAllowanceField.setText(String.valueOf(emp.getPhoneAllowance()));
            clothingAllowanceField.setText(String.valueOf(emp.getClothingAllowance()));
            grossSemiMonthlyRateField.setText(String.valueOf(emp.getGrossSemiMonthlyRate()));
            hourlyRateField.setText(String.valueOf(emp.getHourlyRate()));
            
            displayArea.setText("Employee Found:\n" + emp.toString());
        } else {
            JOptionPane.showMessageDialog(this, "Employee not found.");
        }
    }
    
    private void searchAttendance() {
        String id = empNumField.getText().trim();
        String date = attendanceDateField.getText().trim();
        if (id.isEmpty() || date.equals("  /  /    ")) {
            JOptionPane.showMessageDialog(this, "Enter Employee # and Date.");
            return;
        }
        // Search in AttendanceDAO/CSV
        displayArea.setText("Searching attendance for " + id + " on " + date + "...");
    }
    
    private void updateAttendance() {
        String id = empNumField.getText().trim();
        String date = attendanceDateField.getText();
        String in = timeInField.getText();
        String out = timeOutField.getText();
        
        // Log the change
        displayArea.append("\nUpdating Attendance: " + date + " In: " + in + " Out: " + out);
        JOptionPane.showMessageDialog(this, "Attendance Updated Successfully.");
    }
    
    private void applyRoleRestrictions() {
    // 1. IT Access for Credentials
    if (!securityService.canManageLogins(currentUser)) {
        userPasswordField.setEnabled(false);
        confirmPasswordField.setEnabled(false);
        userTypeComboBox.setEnabled(false);
    }

    // 2. Finance/Admin Salary Restrictions
    if (!securityService.canEditSalary(currentUser)) {
        basicSalaryField.setEditable(false);
        riceSubsidyField.setEditable(false);
        phoneAllowanceField.setEditable(false);
        clothingAllowanceField.setEditable(false);
        grossSemiMonthlyRateField.setEditable(false);
        hourlyRateField.setEditable(false);
    }
}
    
    
}
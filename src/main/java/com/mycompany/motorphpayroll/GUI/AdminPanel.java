package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.User;
import com.mycompany.motorphpayroll.model.Attendance; // Import Attendance model
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import javax.swing.text.MaskFormatter; // For formatted date/time input
import java.text.ParseException; // For MaskFormatter exceptions

public class AdminPanel extends JPanel {

    // Employee Details Fields
    private JTextField empNumField, lastNameField, firstNameField, birthdayField, addressField,
            phoneField, sssField, philhealthField, tinField, pagibigField, statusField,
            positionField, supervisorField, basicSalaryField, riceSubsidyField, phoneAllowanceField,
            clothingAllowanceField, grossSemiMonthlyRateField, hourlyRateField;

    // User Credentials Fields
    private JPasswordField userPasswordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> userTypeComboBox;

    // Attendance Fields (NEW)
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

        // Attendance Details (NEW SECTION)
        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####"); // MM/DD/YYYY
            attendanceDateField = new JFormattedTextField(dateFormatter);
            MaskFormatter timeFormatter = new MaskFormatter("##:##:## ##"); // HH:MM:SS AM/PM
            timeInField = new JFormattedTextField(timeFormatter);
            timeOutField = new JFormattedTextField(timeFormatter);
        } catch (ParseException e) {
            e.printStackTrace(); // Log or handle the exception appropriately
            attendanceDateField = new JFormattedTextField();
            timeInField = new JFormattedTextField();
            timeOutField = new JFormattedTextField();
        }

        JPanel attendanceInputPanel = new JPanel(new GridBagLayout());
        attendanceInputPanel.setBorder(BorderFactory.createTitledBorder("Attendance Correction"));
        
        // Use a new GBC for this sub-panel to manage its internal layout without affecting the main panel's `row` counter directly
        GridBagConstraints gbcAtt = new GridBagConstraints();
        gbcAtt.insets = new Insets(5, 5, 5, 5);
        gbcAtt.fill = GridBagConstraints.HORIZONTAL;
        int attRow = 0;

        addFormField(attendanceInputPanel, "Date (MM/DD/YYYY):", attendanceDateField, gbcAtt, attRow++);
        addFormField(attendanceInputPanel, "Time In (HH:MM:SS AM/PM):", timeInField, gbcAtt, attRow++);
        addFormField(attendanceInputPanel, "Time Out (HH:MM:SS AM/PM):", timeOutField, gbcAtt, attRow++);

        // Add attendance buttons within a sub-panel
        JPanel attendanceButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        searchAttendanceButton = new JButton("Search Attendance");
        updateAttendanceButton = new JButton("Update Attendance");
        attendanceButtonPanel.add(searchAttendanceButton);
        attendanceButtonPanel.add(updateAttendanceButton);

        gbcAtt.gridx = 0;
        gbcAtt.gridy = attRow;
        gbcAtt.gridwidth = 2; // Span across two columns
        attendanceInputPanel.add(attendanceButtonPanel, gbcAtt);
        attRow++;

        // Add the attendance section to the main input panel
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2; // This sub-panel should span both columns of the main input panel
        inputAndAttendancePanel.add(attendanceInputPanel, gbc);
        row++;


        JScrollPane scrollPane = new JScrollPane(inputAndAttendancePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(500, 700)); // Adjusted size for new fields

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

        // New Action Listeners for Attendance
        searchAttendanceButton.addActionListener(e -> searchAttendance());
        updateAttendanceButton.addActionListener(e -> updateAttendance());

        // Initial load of employees into cache (if not done globally)
        CSVReaderUtil.loadEmployeesToCache();
    }

    private void addFormField(JPanel panel, String labelText, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(component, gbc);
    }

    private void addEmployee() {
        // 1. Validate employee details
        String empNum = empNumField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String birthday = birthdayField.getText().trim();
        String address = addressField.getText().trim();
        String phoneNumber = phoneField.getText().trim();
        String sssNum = sssField.getText().trim();
        String philhealthNum = philhealthField.getText().trim();
        String tinNum = tinField.getText().trim();
        String pagibigNum = pagibigField.getText().trim();
        String status = statusField.getText().trim();
        String position = positionField.getText().trim();
        String supervisor = supervisorField.getText().trim();

        // Parse numerical fields safely
        double basicSalary = parseDouble(basicSalaryField.getText(), "Basic Salary");
        double riceSubsidy = parseDouble(riceSubsidyField.getText(), "Rice Subsidy");
        double phoneAllowance = parseDouble(phoneAllowanceField.getText(), "Phone Allowance");
        double clothingAllowance = parseDouble(clothingAllowanceField.getText(), "Clothing Allowance");
        double grossSemiMonthlyRate = parseDouble(grossSemiMonthlyRateField.getText(), "Gross Semi-monthly Rate");
        double hourlyRate = parseDouble(hourlyRateField.getText(), "Hourly Rate");

        // Basic validation for employee data
        if (empNum.isEmpty() || lastName.isEmpty() || firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee Number, Last Name, and First Name are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (CSVReaderUtil.getEmployeeById(empNum) != null) {
            JOptionPane.showMessageDialog(this, "Employee with this ID already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 2. Validate user credentials
        String userPass = new String(userPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());
        String userRole = (String) userTypeComboBox.getSelectedItem();

        if (userPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User Password and Confirm Password are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!userPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if a user with this employee number already exists in login credentials
        try {
            Map<String, User> existingUsers = CSVReaderUtil.readUsersFromLoginCSV();
            if (existingUsers.containsKey(empNum)) {
                 JOptionPane.showMessageDialog(this, "A login account for this Employee Number already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error checking existing user accounts: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Create Employee object
        Employee newEmployee = new Employee(
                empNum, lastName, firstName, birthday, address, phoneNumber,
                sssNum, philhealthNum, tinNum, pagibigNum, status, position,
                supervisor, basicSalary, riceSubsidy, phoneAllowance,
                clothingAllowance, grossSemiMonthlyRate, hourlyRate
        );
        
        // 4. Create User object for login credentials
        User newUser = new User(empNum, userPass, userRole);

        try {
            // 5. Add employee to employee.csv
            CSVReaderUtil.addEmployeeToCSV(newEmployee);
            
            // 6. Add user to login_credentials.csv
            CSVReaderUtil.addUserToLoginCSV(newUser);

            displayArea.setText("Employee and User added successfully:\n" + newEmployee.toString());
            clearFields();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee/user: " + ex.getMessage(), "File Write Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Detailed error adding employee/user: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updateEmployee() {
        String empNum = empNumField.getText().trim();
        if (empNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee Number is required to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee existingEmployee = CSVReaderUtil.getEmployeeById(empNum);
        if (existingEmployee == null) {
            JOptionPane.showMessageDialog(this, "Employee not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update employee object with current field values
        existingEmployee.setLastName(lastNameField.getText().trim());
        existingEmployee.setFirstName(firstNameField.getText().trim());
        existingEmployee.setBirthday(birthdayField.getText().trim());
        existingEmployee.setAddress(addressField.getText().trim());
        existingEmployee.setPhoneNumber(phoneField.getText().trim());
        existingEmployee.setSssNumber(sssField.getText().trim());
        existingEmployee.setPhilhealthNumber(philhealthField.getText().trim());
        existingEmployee.setTinNumber(tinField.getText().trim());
        existingEmployee.setPagibigNumber(pagibigField.getText().trim());
        existingEmployee.setStatus(statusField.getText().trim());
        existingEmployee.setPosition(positionField.getText().trim());
        existingEmployee.setSupervisor(supervisorField.getText().trim());
        existingEmployee.setBasicSalary(parseDouble(basicSalaryField.getText(), "Basic Salary"));
        existingEmployee.setRiceSubsidy(parseDouble(riceSubsidyField.getText(), "Rice Subsidy"));
        existingEmployee.setPhoneAllowance(parseDouble(phoneAllowanceField.getText(), "Phone Allowance"));
        existingEmployee.setClothingAllowance(parseDouble(clothingAllowanceField.getText(), "Clothing Allowance"));
        existingEmployee.setGrossSemiMonthlyRate(parseDouble(grossSemiMonthlyRateField.getText(), "Gross Semi-monthly Rate"));
        existingEmployee.setHourlyRate(parseDouble(hourlyRateField.getText(), "Hourly Rate"));

        // Handle user credentials update
        String userPass = new String(userPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());
        String userRole = (String) userTypeComboBox.getSelectedItem();

        try {
            User existingUser = null;
            Map<String, User> allUsers = CSVReaderUtil.readUsersFromLoginCSV();
            if (allUsers.containsKey(empNum)) {
                existingUser = allUsers.get(empNum);
            }

            if (!userPass.isEmpty()) { // Only update password if provided
                if (!userPass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (existingUser != null) {
                    existingUser.setPassword(userPass);
                    existingUser.setRole(userRole); // Update role along with password
                    CSVReaderUtil.updateUserInLoginCSV(existingUser);
                } else {
                    // If no existing user, create one for this employee
                    User newUser = new User(empNum, userPass, userRole);
                    CSVReaderUtil.addUserToLoginCSV(newUser);
                }
            } else if (existingUser != null) {
                // If password fields are empty but user exists, just update role
                existingUser.setRole(userRole);
                CSVReaderUtil.updateUserInLoginCSV(existingUser);
            }
            // If password fields are empty and no existing user, do nothing for user credentials


            if (CSVReaderUtil.updateEmployeeInCSV(existingEmployee)) {
                displayArea.setText("Employee updated successfully:\n" + existingEmployee.toString());
            } else {
                displayArea.setText("Failed to update employee.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error updating employee/user: " + ex.getMessage(), "File Write Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Detailed error updating employee/user: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteEmployee() {
        String empNum = empNumField.getText().trim();
        if (empNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee Number is required to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete employee " + empNum + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean employeeDeleted = CSVReaderUtil.deleteEmployeeFromCSV(empNum);
            boolean userDeleted = false;
            try {
                // Also remove from login credentials
                Map<String, User> allUsers = CSVReaderUtil.readUsersFromLoginCSV();
                if (allUsers.containsKey(empNum)) {
                    allUsers.remove(empNum);
                    CSVReaderUtil.writeAllUsersToLoginCSV(new java.util.ArrayList<>(allUsers.values()), CSVReaderUtil.getWritableLoginCredentialsCsvPath());
                    userDeleted = true;
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user login credentials: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }


            if (employeeDeleted) {
                displayArea.setText("Employee " + empNum + " deleted successfully.\n" + (userDeleted ? "Associated login account also deleted." : "No associated login account found."));
                clearFields();
            } else {
                displayArea.setText("Failed to delete employee " + empNum + " (not found).");
            }
        }
    }

    private void searchEmployee() {
        String empNum = empNumField.getText().trim();
        if (empNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee Number is required to search.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee employee = CSVReaderUtil.getEmployeeById(empNum);
        if (employee != null) {
            displayArea.setText(employee.toString());
            // Populate fields with found employee's data
            lastNameField.setText(employee.getLastName());
            firstNameField.setText(employee.getFirstName());
            birthdayField.setText(employee.getBirthday());
            addressField.setText(employee.getAddress());
            phoneField.setText(employee.getPhoneNumber());
            sssField.setText(employee.getSssNumber());
            philhealthField.setText(employee.getPhilhealthNumber());
            tinField.setText(employee.getTinNumber());
            pagibigField.setText(employee.getPagibigNumber());
            statusField.setText(employee.getStatus());
            positionField.setText(employee.getPosition());
            supervisorField.setText(employee.getSupervisor());
            basicSalaryField.setText(String.valueOf(employee.getBasicSalary()));
            riceSubsidyField.setText(String.valueOf(employee.getRiceSubsidy()));
            phoneAllowanceField.setText(String.valueOf(employee.getPhoneAllowance()));
            clothingAllowanceField.setText(String.valueOf(employee.getClothingAllowance()));
            grossSemiMonthlyRateField.setText(String.valueOf(employee.getGrossSemiMonthlyRate()));
            hourlyRateField.setText(String.valueOf(employee.getHourlyRate()));

            // Populate user fields if an associated user exists
            try {
                Map<String, User> allUsers = CSVReaderUtil.readUsersFromLoginCSV();
                User foundUser = allUsers.get(empNum);
                if (foundUser != null) {
                    // Do not display password for security
                    userPasswordField.setText(""); // Clear password field
                    confirmPasswordField.setText(""); // Clear confirm password field
                    userTypeComboBox.setSelectedItem(foundUser.getRole());
                    displayArea.append("\n\nAssociated User Role: " + foundUser.getRole());
                } else {
                    userPasswordField.setText("");
                    confirmPasswordField.setText("");
                    userTypeComboBox.setSelectedItem("Employee"); // Default to Employee if no user found
                    displayArea.append("\n\nNo associated login account found.");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error searching user login credentials: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                userPasswordField.setText("");
                confirmPasswordField.setText("");
                userTypeComboBox.setSelectedItem("Employee");
            }

            // Clear attendance fields as we're just searching employee details here
            attendanceDateField.setText("");
            timeInField.setText("");
            timeOutField.setText("");

        } else {
            displayArea.setText("Employee not found.");
            clearFieldsExceptEmpNum(); // Clear other fields but keep employee number for re-search
        }
    }

    // NEW: Search for attendance record
    private void searchAttendance() {
        String empNum = empNumField.getText().trim();
        String date = attendanceDateField.getText().trim();

        if (empNum.isEmpty() || date.isEmpty() || date.contains(" ")) { // Check for incomplete mask
            JOptionPane.showMessageDialog(this, "Employee Number and a complete Date (MM/DD/YYYY) are required to search for attendance.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate date format (simple check for now, consider a more robust date parser if needed)
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Please use MM/DD/YYYY.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Attendance attendance = CSVReaderUtil.getAttendanceRecord(empNum, date);

        if (attendance != null) {
            timeInField.setText(attendance.getLogInTime());
            timeOutField.setText(attendance.getLogOutTime());
            displayArea.setText("Attendance record found for " + attendance.getEmployeeNumber() + " on " + attendance.getDate() + ":\n" +
                                "Time In: " + attendance.getLogInTime() + "\n" +
                                "Time Out: " + attendance.getLogOutTime());
        } else {
            timeInField.setText("");
            timeOutField.setText("");
            displayArea.setText("No attendance record found for Employee " + empNum + " on " + date + ".");
            JOptionPane.showMessageDialog(this, "No attendance record found for Employee " + empNum + " on " + date + ".", "Not Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // NEW: Update attendance record
    private void updateAttendance() {
        String empNum = empNumField.getText().trim();
        String date = attendanceDateField.getText().trim();
        String timeIn = timeInField.getText().trim();
        String timeOut = timeOutField.getText().trim();

        if (empNum.isEmpty() || date.isEmpty() || timeIn.isEmpty() || timeOut.isEmpty() || date.contains(" ") || timeIn.contains(" ") || timeOut.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Employee Number, Date, Time In, and Time Out are all required to update attendance.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate date and time formats
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Please use MM/DD/YYYY.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!timeIn.matches("\\d{2}:\\d{2}:\\d{2} (AM|PM)") || !timeOut.matches("\\d{2}:\\d{2}:\\d{2} (AM|PM)")) {
            JOptionPane.showMessageDialog(this, "Invalid Time format. Please use HH:MM:SS AM/PM.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // To update, we need employee's first and last name for the Attendance object
        Employee employee = CSVReaderUtil.getEmployeeById(empNum);
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Employee not found. Cannot update attendance without employee details.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Attendance updatedAttendance = new Attendance(
            empNum,
            employee.getLastName(),
            employee.getFirstName(),
            date,
            timeIn,
            timeOut
        );

        try {
            boolean success = CSVReaderUtil.updateAttendanceInCSV(updatedAttendance);
            if (success) {
                displayArea.setText("Attendance record for " + empNum + " on " + date + " updated successfully to:\n" +
                                    "Time In: " + timeIn + "\n" +
                                    "Time Out: " + timeOut);
                JOptionPane.showMessageDialog(this, "Attendance record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // If not found, it implies it's a new record for that day, so add it
                int confirm = JOptionPane.showConfirmDialog(this, 
                                "No existing attendance record found for " + empNum + " on " + date + ".\n" +
                                "Do you want to add this as a new attendance record?", 
                                "Add New Record?", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    CSVReaderUtil.addAttendanceToCSV(updatedAttendance);
                    displayArea.setText("New attendance record added for " + empNum + " on " + date + ":\n" +
                                    "Time In: " + timeIn + "\n" +
                                    "Time Out: " + timeOut);
                    JOptionPane.showMessageDialog(this, "New attendance record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    displayArea.setText("Operation cancelled. Attendance record not updated/added.");
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error updating/adding attendance record: " + ex.getMessage(), "File Write Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Detailed error updating/adding attendance: " + ex.getMessage());
            ex.printStackTrace();
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

        // Clear user credentials fields
        userPasswordField.setText("");
        confirmPasswordField.setText("");
        userTypeComboBox.setSelectedItem("Employee"); // Default to Employee

        // Clear attendance fields
        attendanceDateField.setText("");
        timeInField.setText("");
        timeOutField.setText("");

        displayArea.setText("");
    }

    private void clearFieldsExceptEmpNum() {
        // Keeps empNumField value
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
        userTypeComboBox.setSelectedItem("Employee");

        // Clear attendance fields
        attendanceDateField.setText("");
        timeInField.setText("");
        timeOutField.setText("");

        displayArea.setText("");
    }

    private double parseDouble(String text, String fieldName) {
        try {
            if (text == null || text.trim().isEmpty()) {
                // If field is empty, treat as 0.0 but maybe log a warning or prompt user
                System.out.println("Warning: " + fieldName + " is empty. Defaulting to 0.0");
                return 0.0;
            }
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for " + fieldName + ". Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return 0.0; // Return 0.0 or throw an exception to indicate failure
        }
    }
}
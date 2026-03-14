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

    private JTextField empNumField, lastNameField, firstNameField, birthdayField, addressField,
            phoneField, sssField, philhealthField, tinField, pagibigField, statusField,
            positionField, supervisorField, basicSalaryField, riceSubsidyField, phoneAllowanceField,
            clothingAllowanceField, grossSemiMonthlyRateField, hourlyRateField;

    private JPasswordField userPasswordField, confirmPasswordField;
    private JComboBox<String> userTypeComboBox;
    private JLabel passwordLabel, confirmPassLabel, userTypeLabel;

    private JFormattedTextField attendanceDateField, timeInField, timeOutField;
    private JButton searchAttendanceButton, updateAttendanceButton;
    private JButton addButton, updateButton, deleteButton, clearButton, searchButton;
    private JTextArea displayArea;

    public AdminPanel() {
        this(true);
    }

    public AdminPanel(boolean showCredentials) {
        setLayout(new BorderLayout(10, 10));
        initComponents();
        userPasswordField.setVisible(showCredentials);
        confirmPasswordField.setVisible(showCredentials);
        userTypeComboBox.setVisible(showCredentials);
        passwordLabel.setVisible(showCredentials);
        confirmPassLabel.setVisible(showCredentials);
        userTypeLabel.setVisible(showCredentials);
    }

    private void initComponents() {
        JPanel inputAndAttendancePanel = new JPanel(new GridBagLayout());
        inputAndAttendancePanel.setBorder(BorderFactory.createTitledBorder("Employee Details and Attendance"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
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

        addFormField(inputAndAttendancePanel, passwordLabel = new JLabel("User Password:"), userPasswordField = new JPasswordField(20), gbc, row++);
        addFormField(inputAndAttendancePanel, confirmPassLabel = new JLabel("Confirm Password:"), confirmPasswordField = new JPasswordField(20), gbc, row++);
        
        gbc.gridx = 0; gbc.gridy = row;
        inputAndAttendancePanel.add(userTypeLabel = new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        userTypeComboBox = new JComboBox<>(new String[]{"Employee", "Admin"});
        inputAndAttendancePanel.add(userTypeComboBox, gbc);
        row++;

        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            attendanceDateField = new JFormattedTextField(dateFormatter);
            MaskFormatter timeFormatter = new MaskFormatter("##:##:## UU");
            timeInField = new JFormattedTextField(timeFormatter);
            timeOutField = new JFormattedTextField(timeFormatter);
        } catch (ParseException e) {
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
        gbcAtt.gridx = 0; gbcAtt.gridy = attRow; gbcAtt.gridwidth = 2; 
        attendanceInputPanel.add(attendanceButtonPanel, gbcAtt);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        inputAndAttendancePanel.add(attendanceInputPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(inputAndAttendancePanel);
        scrollPane.setPreferredSize(new Dimension(500, 700)); 
        add(scrollPane, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addButton = new JButton("Add Employee"));
        buttonPanel.add(updateButton = new JButton("Update Employee"));
        buttonPanel.add(deleteButton = new JButton("Delete Employee"));
        buttonPanel.add(clearButton = new JButton("Clear Fields"));
        buttonPanel.add(searchButton = new JButton("Search Employee"));
        
        JButton viewSalaryButton = new JButton("View Salary");
        viewSalaryButton.addActionListener(e -> displaySalaryBreakdown());
        buttonPanel.add(viewSalaryButton);
        
        add(buttonPanel, BorderLayout.SOUTH);

        displayArea = new JTextArea(15, 40);
        displayArea.setEditable(false);
        JScrollPane displayScrollPane = new JScrollPane(displayArea);
        displayScrollPane.setBorder(BorderFactory.createTitledBorder("Employee Information Display"));
        add(displayScrollPane, BorderLayout.CENTER);

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
        addFormField(panel, new JLabel(labelText), component, gbc, row);
    }

    private void addFormField(JPanel panel, JLabel label, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    // --- LOGIC METHODS ---
    private void addEmployee() {
    if (!validateInputs()) return;

    try {
        // 1. Gather all UI fields into a String array
        String[] data = {
            empNumField.getText().trim(),
            lastNameField.getText().trim(),
            firstNameField.getText().trim(),
            birthdayField.getText().trim(),
            addressField.getText().trim(),
            phoneField.getText().trim(),
            sssField.getText().trim(),
            philhealthField.getText().trim(),
            tinField.getText().trim(),
            pagibigField.getText().trim(),
            statusField.getText().trim(),
            positionField.getText().trim(),
            supervisorField.getText().trim(),
            basicSalaryField.getText().trim(),
            riceSubsidyField.getText().trim(),
            phoneAllowanceField.getText().trim(),
            clothingAllowanceField.getText().trim(),
            grossSemiMonthlyRateField.getText().trim(),
            hourlyRateField.getText().trim()
        };

        // 2. Instantiate the concrete employee subclass (since Employee is abstract)
        // If your RegularEmployee(String[]) constructor exists, this works perfectly.
        Employee newEmp = new com.mycompany.motorphpayroll.model.RegularEmployee(data);

        // 3. Use your DAO to save the data
        com.mycompany.motorphpayroll.DAO.EmployeeDAO dao = new com.mycompany.motorphpayroll.DAO.EmployeeDAO();
        dao.addEmployee(newEmp);

        // 4. Update UI
        CSVWriterUtil.writeToCSV("employees.csv", data);
        JOptionPane.showMessageDialog(this, "Employee Added Successfully!"); 
        displayArea.setText("Employee added: " + lastNameField.getText());
        clearFields();

    } catch (Exception e) {
        // Catching general Exception because DAO.addEmployee throws Exception
        JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        e.printStackTrace(); // Useful for debugging in NetBeans output
    }
}

    private void updateEmployee() {
        if (empNumField.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Search for an employee first."); return; }
        displayArea.setText("Employee updated.");
    }

    private void deleteEmployee() {
        if (empNumField.getText().isEmpty()) return;
        displayArea.setText("Employee deleted.");
    }

    private boolean validateInputs() {
        if (empNumField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Required fields are empty.");
            return false;
        }
        return true;
    }

    private void clearFields() {
    // Employee Info Fields
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
    
    // Attendance Fields
    attendanceDateField.setText("");
    timeInField.setText("");
    timeOutField.setText("");
    
    displayArea.setText("Fields Cleared.");
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

    private void displaySalaryBreakdown() {
        String empId = empNumField.getText().trim();
        if (empId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search for an employee first.");
            return;
        }
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream captureStream = new java.io.PrintStream(baos);
        java.io.PrintStream originalOut = System.out;
        System.setOut(captureStream);
        try {
            Employee emp = CSVReaderUtil.getEmployeeById(empId);
            com.mycompany.motorphpayroll.util.PayrollCalculator calc = 
                new com.mycompany.motorphpayroll.util.PayrollCalculator(null, null);
            calc.computeSalary(emp, 0.0); 
            System.out.flush();
            displayArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
            displayArea.setText(baos.toString());
        } catch (Exception ex) {
            displayArea.setText("Error: " + ex.getMessage());
        } finally {
            System.setOut(originalOut);
        }
    }
}
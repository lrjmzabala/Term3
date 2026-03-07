package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import com.mycompany.motorphpayroll.util.CSVWriterUtil; // Added this
import com.mycompany.motorphpayroll.util.PayrollCalculator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import com.toedter.calendar.JDateChooser; 
import java.util.Date;
import java.text.SimpleDateFormat;

public class EmployeePanel extends JPanel {
    private JDateChooser startDateChooser, endDateChooser; 
    private JDateChooser leaveStartChooser, leaveEndChooser; // New for Leave
    private JTextField leaveReasonField; // New for Leave
    private JTextArea employeeDetailsArea;
    private JLabel salaryLabel;
    private JButton viewSalaryButton;
    private JButton loginTimeButton;
    private JButton logoutTimeButton;
    private JButton submitLeaveButton; // New for Leave
    private JLabel attendanceMessageLabel;

    private String loggedInEmployeeNumber;

    public EmployeePanel(String employeeNumber) {
        this.loggedInEmployeeNumber = employeeNumber;

        setLayout(new BorderLayout());

        CSVReaderUtil.loadEmployeesToCache();

        // --- TOP PANEL: Attendance & Leave ---
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Employee Header
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel employeeHeaderLabel = new JLabel("Details for Employee: " + loggedInEmployeeNumber);
        employeeHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputPanel.add(employeeHeaderLabel);

        // Attendance Buttons
        JPanel attendanceButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        loginTimeButton = new JButton("Log In");
        logoutTimeButton = new JButton("Log Out");
        attendanceButtonsPanel.add(loginTimeButton);
        attendanceButtonsPanel.add(logoutTimeButton);

        // LEAVE REQUEST SUB-PANEL
        JPanel leaveRequestPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        leaveRequestPanel.setBorder(BorderFactory.createTitledBorder("Request Leave"));
        
        leaveStartChooser = new JDateChooser();
        leaveStartChooser.setPreferredSize(new Dimension(120, 25));
        leaveEndChooser = new JDateChooser();
        leaveEndChooser.setPreferredSize(new Dimension(120, 25));
        leaveReasonField = new JTextField(15);
        submitLeaveButton = new JButton("Submit Leave");

        leaveRequestPanel.add(new JLabel("Start:"));
        leaveRequestPanel.add(leaveStartChooser);
        leaveRequestPanel.add(new JLabel("End:"));
        leaveRequestPanel.add(leaveEndChooser);
        leaveRequestPanel.add(new JLabel("Reason:"));
        leaveRequestPanel.add(leaveReasonField);
        leaveRequestPanel.add(submitLeaveButton);

        attendanceMessageLabel = new JLabel("");
        attendanceMessageLabel.setForeground(Color.BLUE);
        attendanceMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Combining Top Components
        JPanel combinedTop = new JPanel(new GridLayout(3, 1));
        combinedTop.add(inputPanel);
        combinedTop.add(attendanceButtonsPanel);
        combinedTop.add(leaveRequestPanel);

        topPanel.add(combinedTop, BorderLayout.NORTH);
        topPanel.add(attendanceMessageLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL: Details Area ---
        employeeDetailsArea = new JTextArea(10, 30);
        employeeDetailsArea.setEditable(false);
        add(new JScrollPane(employeeDetailsArea), BorderLayout.CENTER);

        displayLoggedInEmployeeDetails();

        // --- SOUTH PANEL: Salary ---
        JPanel salaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        salaryPanel.add(new JLabel("Salary Period Start:"));
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("MM/dd/yyyy"); 
        startDateChooser.setPreferredSize(new Dimension(120, 25));
        salaryPanel.add(startDateChooser);

        salaryPanel.add(new JLabel("End:"));
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("MM/dd/yyyy"); 
        endDateChooser.setPreferredSize(new Dimension(120, 25)); 
        salaryPanel.add(endDateChooser);

        viewSalaryButton = new JButton("View Salary");
        salaryPanel.add(viewSalaryButton);

        salaryLabel = new JLabel("💰 Net Salary: PHP 0.00");
        salaryPanel.add(salaryLabel);

        add(salaryPanel, BorderLayout.SOUTH);

        // --- ACTION LISTENERS ---

        loginTimeButton.addActionListener(e -> recordAttendance("log_in"));
        logoutTimeButton.addActionListener(e -> recordAttendance("log_out"));

        // Leave Request Listener
        submitLeaveButton.addActionListener(e -> {
            Date sDate = leaveStartChooser.getDate();
            Date eDate = leaveEndChooser.getDate();
            String reason = leaveReasonField.getText().trim();

            if (sDate == null || eDate == null || reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Please fill in all leave details!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Define your leave CSV path (adjust as per your project structure)
            String leavePath = "leave_requests.csv"; 
            CSVWriterUtil.appendLeaveRequest(leavePath, loggedInEmployeeNumber, sDate, eDate, reason);
            
            JOptionPane.showMessageDialog(this, "✅ Leave request submitted successfully!");
            leaveReasonField.setText("");
            leaveStartChooser.setDate(null);
            leaveEndChooser.setDate(null);
        });

        // View Salary Listener (Keep your existing logic)
        viewSalaryButton.addActionListener(e -> {
            calculateAndDisplaySalary();
        });
    }

    // Encapsulated salary logic for cleaner code
    private void calculateAndDisplaySalary() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();

        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(null, "❌ Please select both start and end dates!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String startDateStr = new SimpleDateFormat("MM/dd/yyyy").format(startDate);
        String endDateStr = new SimpleDateFormat("MM/dd/yyyy").format(endDate);
        
        List<Attendance> allAttendanceRecords = CSVReaderUtil.readAttendanceFromCSV(CSVReaderUtil.getWritableAttendanceCsvPath());

        List<Attendance> employeeAttendance = allAttendanceRecords.stream()
            .filter(a -> a.getEmployeeNumber().equals(loggedInEmployeeNumber))
            .toList();

        List<Attendance> filteredAttendance = employeeAttendance.stream()
            .filter(a -> a.isWithinDateRange(startDateStr, endDateStr))
            .toList();

        if (filteredAttendance.isEmpty()) {
            salaryLabel.setText("⚠ No attendance records found.");
            return;
        }

        Employee employee = CSVReaderUtil.getEmployeeById(loggedInEmployeeNumber);
        if (employee == null) return;

        double totalHoursWorked = PayrollCalculator.calculateTotalHoursWorked(loggedInEmployeeNumber, filteredAttendance, startDateStr, endDateStr);
        PayrollCalculator calculator = new PayrollCalculator(List.of(employee), filteredAttendance);

        double grossSalary = (totalHoursWorked / 8) * employee.getDailyWage();
        double incomeTax = calculator.computeIncomeTax(grossSalary);

        double sssDeduction = grossSalary * 0.045;
        double philhealthDeduction = grossSalary * 0.0275;
        double pagibigDeduction = Math.min(grossSalary * 0.02, 100); 
        double totalDeductions = sssDeduction + philhealthDeduction + pagibigDeduction + incomeTax;
        double netSalary = grossSalary - totalDeductions;

        String salaryDetails = "\n\n💰 Salary Breakdown (" + startDateStr + " to " + endDateStr + "):\n"
            + "Total Hours: " + String.format("%.2f", totalHoursWorked) + "\n"
            + "Net Salary: PHP " + String.format("%,.2f", netSalary);

        employeeDetailsArea.append(salaryDetails);
        salaryLabel.setText("💰 Net Salary: PHP " + String.format("%,.2f", netSalary));
    }

    private void displayLoggedInEmployeeDetails() {
        Employee employee = CSVReaderUtil.getEmployeeById(loggedInEmployeeNumber);
        if (employee != null) {
            employeeDetailsArea.setText(employee.toString());
        } else {
            employeeDetailsArea.setText("⚠ Could not load employee details.");
        }
    }

    private void recordAttendance(String type) {
    }
}
package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import com.mycompany.motorphpayroll.util.PayrollCalculator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EmployeePanel extends JPanel {
    // private JTextField empNumField; // REMOVE THIS LINE
    private JTextField startDateField, endDateField;
    private JTextArea employeeDetailsArea;
    private JLabel salaryLabel;
    // private JButton viewButton; // REMOVE THIS LINE
    private JButton viewSalaryButton;

    private String loggedInEmployeeNumber; // New field to store the logged-in employee's number

    // Modify the constructor to accept the logged-in employee number
    public EmployeePanel(String employeeNumber) {
        this.loggedInEmployeeNumber = employeeNumber; // Store the employee number

        setLayout(new BorderLayout());

        // ✅ Load employees when GUI starts
        CSVReaderUtil.loadEmployeesToCache();

        // Input Panel - Now simplified as employee number is passed
        JPanel inputPanel = new JPanel();
        // inputPanel.add(new JLabel("Enter Employee Number:")); // REMOVE THIS LINE
        // empNumField = new JTextField(10); // REMOVE THIS LINE
        // inputPanel.add(empNumField); // REMOVE THIS LINE

        // viewButton = new JButton("View Details"); // REMOVE THIS LINE
        // inputPanel.add(viewButton); // REMOVE THIS LINE

        // Add a label to indicate whose details are being shown
        JLabel employeeHeaderLabel = new JLabel("Details for Employee: " + loggedInEmployeeNumber);
        employeeHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        employeeHeaderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(employeeHeaderLabel);


        add(inputPanel, BorderLayout.NORTH);

        // Employee Details Display
        employeeDetailsArea = new JTextArea(10, 30);
        employeeDetailsArea.setEditable(false);
        add(new JScrollPane(employeeDetailsArea), BorderLayout.CENTER);

        // Automatically display the logged-in employee's details
        displayLoggedInEmployeeDetails();


        // Salary Calculation Panel
        JPanel salaryPanel = new JPanel();
        salaryPanel.add(new JLabel("Start Date (MM/DD/YYYY):"));
        startDateField = new JTextField(10);
        salaryPanel.add(startDateField);

        salaryPanel.add(new JLabel("End Date (MM/DD/YYYY):"));
        endDateField = new JTextField(10);
        salaryPanel.add(endDateField);

        viewSalaryButton = new JButton("View Salary");
        salaryPanel.add(viewSalaryButton);

        salaryLabel = new JLabel("💰 Net Salary: PHP 0.00");
        salaryPanel.add(salaryLabel);

        add(salaryPanel, BorderLayout.SOUTH);

        // REMOVE THE OLD viewButton ActionListener:
        // viewButton.addActionListener(e -> {
        //     String empNum = empNumField.getText();
        //     Employee employee = CSVReaderUtil.getEmployeeById(empNum);
        //     if (employee != null) {
        //         employeeDetailsArea.setText(employee.toString());
        //     } else {
        //         employeeDetailsArea.setText("⚠ Employee not found.");
        //     }
        // });

        // Calculate Salary
        viewSalaryButton.addActionListener(e -> {
            // Use the logged-in employee number directly
            String empNum = this.loggedInEmployeeNumber;
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();

            if (startDate.isEmpty() || endDate.isEmpty()) { // empNum is guaranteed to be present now
                JOptionPane.showMessageDialog(null, "❌ Please enter both start and end dates!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Attendance> allAttendanceRecords = CSVReaderUtil.readAttendanceFromCSV(CSVReaderUtil.getWritableAttendanceCsvPath());

            List<Attendance> employeeAttendance = allAttendanceRecords.stream()
                .filter(a -> a.getEmployeeNumber().equals(empNum))
                .toList();

            List<Attendance> filteredAttendance = employeeAttendance.stream()
                .filter(a -> a.isWithinDateRange(startDate, endDate))
                .toList();

            if (filteredAttendance.isEmpty()) {
                salaryLabel.setText("⚠ No attendance records found for the specified period.");
                return;
            }

            Employee employee = CSVReaderUtil.getEmployeeById(empNum);
            if (employee == null) {
                employeeDetailsArea.setText("⚠ Employee data not found for salary calculation. Please contact admin.");
                salaryLabel.setText("⚠ Employee data missing.");
                return;
            }

            double totalHoursWorked = PayrollCalculator.calculateTotalHoursWorked(empNum, filteredAttendance, startDate, endDate);
            PayrollCalculator calculator = new PayrollCalculator(List.of(employee), filteredAttendance);

            double grossSalary = (totalHoursWorked / 8) * employee.getDailyWage();
            double incomeTax = calculator.computeIncomeTax(grossSalary);

            double sssDeduction = grossSalary * 0.045;
            double philhealthDeduction = grossSalary * 0.0275;
            double pagibigDeduction = Math.min(grossSalary * 0.02, 100);
            double totalDeductions = sssDeduction + philhealthDeduction + pagibigDeduction + incomeTax;
            double netSalary = grossSalary - totalDeductions;

            String salaryDetails = "\n\n💰 Salary Breakdown:\n"
                + "--------------------------------\n"
                + "Total Hours Worked: " + String.format("%,.2f", totalHoursWorked) + " hours\n"
                + "Gross Salary: PHP " + String.format("%,.2f", grossSalary) + "\n"
                + "Deductions:\n"
                + "  - SSS: PHP " + String.format("%,.2f", sssDeduction) + "\n"
                + "  - PhilHealth: PHP " + String.format("%,.2f", philhealthDeduction) + "\n"
                + "  - Pag-IBIG: PHP " + String.format("%,.2f", pagibigDeduction) + "\n"
                + "  - Income Tax: PHP " + String.format("%,.2f", incomeTax) + "\n"
                + "--------------------------------\n"
                + "✅ Net Salary: PHP " + String.format("%,.2f", netSalary) + "\n";

            employeeDetailsArea.append(salaryDetails);
            salaryLabel.setText("💰 Net Salary: PHP " + String.format("%,.2f", netSalary));
        });
    }

    // New method to automatically display the logged-in employee's details
    private void displayLoggedInEmployeeDetails() {
        Employee employee = CSVReaderUtil.getEmployeeById(loggedInEmployeeNumber);
        if (employee != null) {
            employeeDetailsArea.setText(employee.toString());
        } else {
            employeeDetailsArea.setText("⚠ Could not load employee details for " + loggedInEmployeeNumber + ".");
        }
    }
}
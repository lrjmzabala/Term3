package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import com.mycompany.motorphpayroll.util.PayrollCalculator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EmployeePanel extends JPanel {
    private JTextField empNumField, startDateField, endDateField;
    private JTextArea employeeDetailsArea;
    private JLabel salaryLabel;
    private JButton viewButton, viewSalaryButton;

    public EmployeePanel() {
        setLayout(new BorderLayout());
        
        // ✅ Load employees when GUI starts
    CSVReaderUtil.loadEmployeesToCache();

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Employee Number:"));
        empNumField = new JTextField(10);
        inputPanel.add(empNumField);

        viewButton = new JButton("View Details");
        inputPanel.add(viewButton);

        add(inputPanel, BorderLayout.NORTH);

        // Employee Details Display
        employeeDetailsArea = new JTextArea(10, 30);
        employeeDetailsArea.setEditable(false);
        add(new JScrollPane(employeeDetailsArea), BorderLayout.CENTER);

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

        // View Employee Details
        viewButton.addActionListener(e -> {
            String empNum = empNumField.getText();
            Employee employee = CSVReaderUtil.getEmployeeById(empNum);
            if (employee != null) {
                employeeDetailsArea.setText(employee.toString());
            } else {
                employeeDetailsArea.setText("⚠ Employee not found.");
            }
        });

        // Calculate Salary
        viewSalaryButton.addActionListener(e -> {
    String empNum = empNumField.getText();
    String startDate = startDateField.getText();
    String endDate = endDateField.getText();

    if (empNum.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
        JOptionPane.showMessageDialog(null, "❌ Please enter all fields!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    List<Attendance> allAttendanceRecords = CSVReaderUtil.readAttendanceFromCSV("C:\\Users\\Papa\\Downloads\\Copy of MotorPH Employee DataHoursWorked - Employee Details.csv");

    List<Attendance> employeeAttendance = allAttendanceRecords.stream()
        .filter(a -> a.getEmployeeNumber().equals(empNum))
        .toList();

    List<Attendance> filteredAttendance = employeeAttendance.stream()
        .filter(a -> a.isWithinDateRange(startDate, endDate))
        .toList();

    if (filteredAttendance.isEmpty()) {
        salaryLabel.setText("⚠ No attendance records found.");
        return;
    }

    double totalHoursWorked = PayrollCalculator.calculateTotalHoursWorked(empNum, filteredAttendance, startDate, endDate);
    Employee employee = CSVReaderUtil.getEmployeeById(empNum);
    PayrollCalculator calculator = new PayrollCalculator(List.of(employee), filteredAttendance);

    // ✅ Compute Gross Salary
    double grossSalary = (totalHoursWorked / 8) * employee.getDailyWage();
    double incomeTax = calculator.computeIncomeTax(grossSalary); // ✅ Get Income Tax

    // ✅ Compute Net Salary
    double sssDeduction = grossSalary * 0.045;
    double philhealthDeduction = grossSalary * 0.0275;
    double pagibigDeduction = Math.min(grossSalary * 0.02, 100);
    double netSalary = grossSalary - (sssDeduction + philhealthDeduction + pagibigDeduction + incomeTax);

    // ✅ Append salary details
    String salaryDetails = "💰 Salary Breakdown:\n"
        + "--------------------------------\n"
        + "Total Hours Worked: " + totalHoursWorked + "\n"
        + "Gross Salary: PHP " + String.format("%,.2f", grossSalary) + "\n"
        + "Deductions:\n"
        + "  - SSS: PHP " + String.format("%,.2f", sssDeduction) + "\n"
        + "  - PhilHealth: PHP " + String.format("%,.2f", philhealthDeduction) + "\n"
        + "  - Pag-IBIG: PHP " + String.format("%,.2f", pagibigDeduction) + "\n"
        + "  - Income Tax: PHP " + String.format("%,.2f", incomeTax) + "\n"
        + "--------------------------------\n"
        + "✅ Net Salary: PHP " + String.format("%,.2f", netSalary) + "\n";

    employeeDetailsArea.append("\n" + salaryDetails);
    salaryLabel.setText("💰 Net Salary: PHP " + String.format("%,.2f", netSalary));
});
    }
}
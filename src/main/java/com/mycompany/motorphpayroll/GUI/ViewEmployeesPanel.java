package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance; 
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import com.mycompany.motorphpayroll.util.PayrollCalculator; 


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel; 
import java.awt.*;
import java.util.List;
import java.util.Vector; // For DefaultTableModel data
import java.util.Date;
import java.time.LocalDate; 
import java.time.format.DateTimeFormatter; 

public class ViewEmployeesPanel extends JPanel {

    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextArea employeeDetailsDisplayArea;
    
    // Labels for payroll details (can be expanded later)
    private JLabel selectedEmployeeNameLabel;
    private JLabel basicSalaryLabel;
    private JLabel grossSalaryLabel;
    private JLabel netSalaryLabel;
    private JLabel totalHoursWorkedLabel;
    
    // For admin to select a date range for payroll viewing for a selected employee
    private com.toedter.calendar.JDateChooser adminPayrollStartDateChooser;
    private com.toedter.calendar.JDateChooser adminPayrollEndDateChooser;
    private JButton calculatePayrollForSelectedButton;


    public ViewEmployeesPanel() {
        setLayout(new BorderLayout(10, 10)); 

        // --- Employee List/Table ---
        String[] columnNames = {"Employee #", "Last Name", "First Name", "Position", "Basic Salary"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one employee can be selected
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        
        // Add a listener to populate details when an employee is selected
        employeeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && employeeTable.getSelectedRow() != -1) {
                    displaySelectedEmployeeDetails();
                }
            }
        });

        add(tableScrollPane, BorderLayout.WEST); // Table on the left

        // --- Details and Payroll Display Panel ---
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Selected Employee Details & Payroll"));

        employeeDetailsDisplayArea = new JTextArea(15, 30);
        employeeDetailsDisplayArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(employeeDetailsDisplayArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.NORTH); 

        // Payroll Calculation and Display Sub-Panel
        JPanel payrollPanel = new JPanel(new GridBagLayout());
        payrollPanel.setBorder(BorderFactory.createTitledBorder("Payroll for Selected Employee"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        selectedEmployeeNameLabel = addLabelToPanel(payrollPanel, "Employee: N/A", gbc, row++, 2); 
        addLabelToPanel(payrollPanel, "Basic Salary:", basicSalaryLabel = new JLabel("PHP 0.00"), gbc, row++);
        addLabelToPanel(payrollPanel, "Total Hours Worked:", totalHoursWorkedLabel = new JLabel("0.00 hours"), gbc, row++);
        addLabelToPanel(payrollPanel, "Gross Salary:", grossSalaryLabel = new JLabel("PHP 0.00"), gbc, row++);
        addLabelToPanel(payrollPanel, "Net Salary:", netSalaryLabel = new JLabel("PHP 0.00"), gbc, row++);

        // Date pickers for admin payroll calculation
        gbc.gridx = 0;
        gbc.gridy = row;
        payrollPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        adminPayrollStartDateChooser = new com.toedter.calendar.JDateChooser();
        adminPayrollStartDateChooser.setDateFormatString("MM/dd/yyyy");
        adminPayrollStartDateChooser.setPreferredSize(new Dimension(120, 25));
        payrollPanel.add(adminPayrollStartDateChooser, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        payrollPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        adminPayrollEndDateChooser = new com.toedter.calendar.JDateChooser();
        adminPayrollEndDateChooser.setDateFormatString("MM/dd/yyyy");
        adminPayrollEndDateChooser.setPreferredSize(new Dimension(120, 25));
        payrollPanel.add(adminPayrollEndDateChooser, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2; // Span two columns
        calculatePayrollForSelectedButton = new JButton("Calculate Payroll");
        payrollPanel.add(calculatePayrollForSelectedButton, gbc);
        row++;

        detailsPanel.add(payrollPanel, BorderLayout.CENTER); // Payroll details below full employee details

        add(detailsPanel, BorderLayout.CENTER); // Main details panel on the right

        // --- Load Employees ---
        loadEmployeesIntoTable();

        // --- Action Listener for Calculate Payroll Button ---
        calculatePayrollForSelectedButton.addActionListener(e -> calculatePayrollForSelectedEmployee());
    }

    private JLabel addLabelToPanel(JPanel panel, String labelText, JLabel valueLabel, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1; 
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(valueLabel, gbc);
        return valueLabel; 
    }
    
    private JLabel addLabelToPanel(JPanel panel, String text, GridBagConstraints gbc, int row, int gridwidth) {
        JLabel label = new JLabel(text);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = gridwidth;
        panel.add(label, gbc);
        return label;
    }


    private void loadEmployeesIntoTable() {
        tableModel.setRowCount(0); // Clear existing data
        List<Employee> employees = CSVReaderUtil.readEmployeesFromCSV(CSVReaderUtil.getWritableEmployeeCsvPath());
        for (Employee emp : employees) {
            Object[] rowData = {emp.getEmployeeNumber(), emp.getLastName(), emp.getFirstName(), emp.getPosition(), String.format("%,.2f", emp.getBasicSalary())};
            tableModel.addRow(rowData);
        }
    }

    private void displaySelectedEmployeeDetails() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            String empNum = (String) employeeTable.getValueAt(selectedRow, 0);
            Employee employee = CSVReaderUtil.getEmployeeById(empNum);
            if (employee != null) {
                employeeDetailsDisplayArea.setText(employee.toString());
                selectedEmployeeNameLabel.setText("Employee: " + employee.getFirstName() + " " + employee.getLastName());
                basicSalaryLabel.setText("PHP " + String.format("%,.2f", employee.getBasicSalary()));
                
                // Clear previous payroll results
                totalHoursWorkedLabel.setText("0.00 hours");
                grossSalaryLabel.setText("PHP 0.00");
                netSalaryLabel.setText("PHP 0.00");

            } else {
                employeeDetailsDisplayArea.setText("Employee details not found.");
                selectedEmployeeNameLabel.setText("Employee: N/A");
                basicSalaryLabel.setText("PHP 0.00");
            }
        }
    }
    
    private void calculatePayrollForSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String empNum = (String) employeeTable.getValueAt(selectedRow, 0);
        Employee employee = CSVReaderUtil.getEmployeeById(empNum);
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Employee data not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date startDate = adminPayrollStartDateChooser.getDate();
        Date endDate = adminPayrollEndDateChooser.getDate();

        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates for payroll calculation.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String startDateStr = LocalDate.ofEpochDay(startDate.getTime() / (1000 * 60 * 60 * 24)).format(formatter);
        String endDateStr = LocalDate.ofEpochDay(endDate.getTime() / (1000 * 60 * 60 * 24)).format(formatter);
        
        List<Attendance> allAttendanceRecords = CSVReaderUtil.readAttendanceFromCSV(CSVReaderUtil.getWritableAttendanceCsvPath());

        List<Attendance> employeeAttendance = allAttendanceRecords.stream()
            .filter(a -> a.getEmployeeNumber().equals(empNum))
            .toList();

        List<Attendance> filteredAttendance = employeeAttendance.stream()
            .filter(a -> a.isWithinDateRange(startDateStr, endDateStr))
            .toList();

        if (filteredAttendance.isEmpty()) {
            totalHoursWorkedLabel.setText("0.00 hours");
            grossSalaryLabel.setText("PHP 0.00");
            netSalaryLabel.setText("PHP 0.00");
            JOptionPane.showMessageDialog(this, "No attendance records found for " + employee.getFirstName() + " " + employee.getLastName() + " for the specified period.", "No Attendance Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        double totalHoursWorked = PayrollCalculator.calculateTotalHoursWorked(empNum, filteredAttendance, startDateStr, endDateStr);
        PayrollCalculator calculator = new PayrollCalculator(List.of(employee), filteredAttendance);

        double grossSalary = (totalHoursWorked / 8) * employee.getDailyWage();
        double incomeTax = calculator.computeIncomeTax(grossSalary);

        double sssDeduction = grossSalary * 0.045; // Placeholder
        double philhealthDeduction = grossSalary * 0.0275; // Placeholder
        double pagibigDeduction = Math.min(grossSalary * 0.02, 100); 
        double totalDeductions = sssDeduction + philhealthDeduction + pagibigDeduction + incomeTax;
        double netSalary = grossSalary - totalDeductions;
        
        totalHoursWorkedLabel.setText(String.format("%,.2f hours", totalHoursWorked));
        grossSalaryLabel.setText("PHP " + String.format("%,.2f", grossSalary));
        netSalaryLabel.setText("PHP " + String.format("%,.2f", netSalary));
        
        // Optionally append full breakdown to employeeDetailsDisplayArea or a new text area
        String payrollSummary = "\n--- Payroll Summary for " + startDateStr + " - " + endDateStr + " ---\n"
                              + "Total Hours: " + String.format("%,.2f", totalHoursWorked) + "\n"
                              + "Gross: PHP " + String.format("%,.2f", grossSalary) + "\n"
                              + "SSS: PHP " + String.format("%,.2f", sssDeduction) + "\n"
                              + "PhilHealth: PHP " + String.format("%,.2f", philhealthDeduction) + "\n"
                              + "Pag-IBIG: PHP " + String.format("%,.2f", pagibigDeduction) + "\n"
                              + "Income Tax: PHP " + String.format("%,.2f", incomeTax) + "\n"
                              + "--------------------------------\n"
                              + "NET SALARY: PHP " + String.format("%,.2f", netSalary) + "\n";
        employeeDetailsDisplayArea.append(payrollSummary);
    }
}
package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import com.mycompany.motorphpayroll.util.PayrollCalculator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import com.toedter.calendar.JDateChooser; // Import JDateChooser
import java.util.Date; // For JDateChooser's getDate() method

public class EmployeePanel extends JPanel {
    private JDateChooser startDateChooser, endDateChooser; // Changed to JDateChooser
    private JTextArea employeeDetailsArea;
    private JLabel salaryLabel;
    private JButton viewSalaryButton;
    private JButton loginTimeButton;
    private JButton logoutTimeButton;
    private JLabel attendanceMessageLabel;

    private String loggedInEmployeeNumber;

    public EmployeePanel(String employeeNumber) {
        this.loggedInEmployeeNumber = employeeNumber;

        setLayout(new BorderLayout());

        CSVReaderUtil.loadEmployeesToCache();

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel employeeHeaderLabel = new JLabel("Details for Employee: " + loggedInEmployeeNumber);
        employeeHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        employeeHeaderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(employeeHeaderLabel);

        JPanel attendanceButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        loginTimeButton = new JButton("Log In");
        logoutTimeButton = new JButton("Log Out");
        attendanceButtonsPanel.add(loginTimeButton);
        attendanceButtonsPanel.add(logoutTimeButton);

        attendanceMessageLabel = new JLabel("");
        attendanceMessageLabel.setForeground(Color.BLUE);
        attendanceMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(attendanceButtonsPanel, BorderLayout.CENTER);
        topPanel.add(attendanceMessageLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        employeeDetailsArea = new JTextArea(10, 30);
        employeeDetailsArea.setEditable(false);
        add(new JScrollPane(employeeDetailsArea), BorderLayout.CENTER);

        displayLoggedInEmployeeDetails();

        // Salary Calculation Panel - now with JDateChooser
        JPanel salaryPanel = new JPanel();
        salaryPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Add some spacing

        salaryPanel.add(new JLabel("Start Date:"));
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("MM/dd/yyyy"); // Set desired date format
        startDateChooser.setPreferredSize(new Dimension(120, 25)); // Adjust size
        salaryPanel.add(startDateChooser);

        salaryPanel.add(new JLabel("End Date:"));
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("MM/dd/yyyy"); // Set desired date format
        endDateChooser.setPreferredSize(new Dimension(120, 25)); // Adjust size
        salaryPanel.add(endDateChooser);

        viewSalaryButton = new JButton("View Salary");
        salaryPanel.add(viewSalaryButton);

        salaryLabel = new JLabel("💰 Net Salary: PHP 0.00");
        salaryPanel.add(salaryLabel);

        add(salaryPanel, BorderLayout.SOUTH);

        // --- Action Listeners for Log In/Out ---
        loginTimeButton.addActionListener(e -> {
            recordAttendance("log_in");
        });

        logoutTimeButton.addActionListener(e -> {
            recordAttendance("log_out");
        });
        // --- End Log In/Out Action Listeners ---


        // Calculate Salary
        viewSalaryButton.addActionListener(e -> {
            String empNum = this.loggedInEmployeeNumber;
            
            // Get dates from JDateChoosers
            Date startDate = startDateChooser.getDate();
            Date endDate = endDateChooser.getDate();

            if (startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(null, "❌ Please select both start and end dates!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Format dates to MM/dd/yyyy string
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
                salaryLabel.setText("⚠ No attendance records found for the specified period.");
                employeeDetailsArea.setText("⚠ No attendance records found for salary calculation.");
                return;
            }

            Employee employee = CSVReaderUtil.getEmployeeById(empNum);
            if (employee == null) {
                employeeDetailsArea.setText("⚠ Employee data not found for salary calculation. Please contact admin.");
                salaryLabel.setText("⚠ Employee data missing.");
                return;
            }

            double totalHoursWorked = PayrollCalculator.calculateTotalHoursWorked(empNum, filteredAttendance, startDateStr, endDateStr);
            PayrollCalculator calculator = new PayrollCalculator(List.of(employee), filteredAttendance);

            double grossSalary = (totalHoursWorked / 8) * employee.getDailyWage();
            double incomeTax = calculator.computeIncomeTax(grossSalary);

            double sssDeduction = grossSalary * 0.045; // Placeholder, ideally from SSS Table
            double philhealthDeduction = grossSalary * 0.0275; // Placeholder
            double pagibigDeduction = Math.min(grossSalary * 0.02, 100); // Max 100
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

    private void displayLoggedInEmployeeDetails() {
        Employee employee = CSVReaderUtil.getEmployeeById(loggedInEmployeeNumber);
        if (employee != null) {
            employeeDetailsArea.setText(employee.toString());
        } else {
            employeeDetailsArea.setText("⚠ Could not load employee details for " + loggedInEmployeeNumber + ".");
        }
    }

    private void recordAttendance(String type) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");

        String currentDate = LocalDate.now().format(dateFormatter);
        String currentTime = LocalTime.now().format(timeFormatter);

        Employee employee = CSVReaderUtil.getEmployeeById(loggedInEmployeeNumber);
        if (employee == null) {
            attendanceMessageLabel.setText("Error: Employee data not found.");
            return;
        }

        List<Attendance> allAttendance = CSVReaderUtil.readAttendanceFromCSV(CSVReaderUtil.getWritableAttendanceCsvPath());

        Attendance todayAttendance = null;
        for (Attendance att : allAttendance) {
            if (att.getEmployeeNumber().equals(loggedInEmployeeNumber) && att.getDate().equals(currentDate)) {
                todayAttendance = att;
                break;
            }
        }

        try {
            if ("log_in".equals(type)) {
                if (todayAttendance != null && todayAttendance.getLogInTime() != null && !todayAttendance.getLogInTime().isEmpty()) {
                    attendanceMessageLabel.setText("You have already logged in today at " + todayAttendance.getLogInTime() + ".");
                } else {
                    if (todayAttendance == null) {
                        todayAttendance = new Attendance(
                            loggedInEmployeeNumber,
                            employee.getLastName(),
                            employee.getFirstName(),
                            currentDate,
                            currentTime,
                            ""
                        );
                        CSVReaderUtil.addAttendanceToCSV(todayAttendance);
                    } else {
                        todayAttendance.setLogInTime(currentTime);
                        CSVReaderUtil.updateAttendanceInCSV(todayAttendance);
                    }
                    attendanceMessageLabel.setText("✅ Logged in at " + currentTime);
                }
            } else if ("log_out".equals(type)) {
                if (todayAttendance == null || todayAttendance.getLogInTime() == null || todayAttendance.getLogInTime().isEmpty()) {
                    attendanceMessageLabel.setText("You must log in before logging out.");
                } else if (todayAttendance.getLogOutTime() != null && !todayAttendance.getLogOutTime().isEmpty()) {
                    attendanceMessageLabel.setText("You have already logged out today at " + todayAttendance.getLogOutTime() + ".");
                } else {
                    todayAttendance.setLogOutTime(currentTime);
                    CSVReaderUtil.updateAttendanceInCSV(todayAttendance);
                    attendanceMessageLabel.setText("✅ Logged out at " + currentTime);
                }
            }
        } catch (IOException ex) {
            attendanceMessageLabel.setText("Error recording attendance: " + ex.getMessage());
            System.err.println("Error recording attendance for " + loggedInEmployeeNumber + ": " + ex.getMessage());
        }
    }
}
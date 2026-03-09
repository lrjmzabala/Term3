package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.DAO.AttendanceDAO;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import com.mycompany.motorphpayroll.util.CSVWriterUtil;
import com.mycompany.motorphpayroll.util.PayrollCalculator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;

public class EmployeePanel extends JPanel {
    private JDateChooser startDateChooser, endDateChooser; 
    private JDateChooser leaveStartChooser, leaveEndChooser;
    private JTextField leaveReasonField;
    private JTextArea employeeDetailsArea;
    private JLabel salaryLabel;
    private JButton viewSalaryButton;
    private JButton loginTimeButton;
    private JButton logoutTimeButton;
    private JButton submitLeaveButton;
    private JLabel attendanceMessageLabel;

    private String loggedInEmployeeNumber;
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    public EmployeePanel(String employeeNumber) {
        this.loggedInEmployeeNumber = employeeNumber;
        setLayout(new BorderLayout());

        CSVReaderUtil.loadEmployeesToCache();

        // --- TOP PANEL: Attendance & Leave ---
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel employeeHeaderLabel = new JLabel("Details for Employee: " + loggedInEmployeeNumber);
        employeeHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputPanel.add(employeeHeaderLabel);

        JPanel attendanceButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        loginTimeButton = new JButton("Log In");
        logoutTimeButton = new JButton("Log Out");
        attendanceButtonsPanel.add(loginTimeButton);
        attendanceButtonsPanel.add(logoutTimeButton);

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
        employeeDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Best for alignment
        add(new JScrollPane(employeeDetailsArea), BorderLayout.CENTER);

        displayLoggedInEmployeeDetails();

        // --- SOUTH PANEL: Salary ---
        JPanel salaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        salaryPanel.add(new JLabel("Start:"));
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

        submitLeaveButton.addActionListener(e -> {
            Date sDate = leaveStartChooser.getDate();
            Date eDate = leaveEndChooser.getDate();
            String reason = leaveReasonField.getText().trim();
            if (sDate == null || eDate == null || reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Please fill in all leave details!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            CSVWriterUtil.appendLeaveRequest("leave_requests.csv", loggedInEmployeeNumber, sDate, eDate, reason);
            JOptionPane.showMessageDialog(this, "✅ Leave request submitted successfully!");
            leaveReasonField.setText("");
            leaveStartChooser.setDate(null);
            leaveEndChooser.setDate(null);
        });

        viewSalaryButton.addActionListener(e -> calculateAndDisplaySalary());
    }

    private void calculateAndDisplaySalary() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();

        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(null, "❌ Please select both dates!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String startDateStr = new SimpleDateFormat("MM/dd/yyyy").format(startDate);
        String endDateStr = new SimpleDateFormat("MM/dd/yyyy").format(endDate);
        
        List<Attendance> filteredAttendance = attendanceDAO.findByEmployeeId(loggedInEmployeeNumber).stream()
            .filter(a -> a.isWithinDateRange(startDateStr, endDateStr))
            .toList();

        if (filteredAttendance.isEmpty()) {
            salaryLabel.setText("⚠ No attendance records found.");
            return;
        }

        Employee employee = CSVReaderUtil.getEmployeeById(loggedInEmployeeNumber);
        if (employee == null) return;

        // 1. Calculations
        double totalHoursWorked = PayrollCalculator.calculateTotalHoursWorked(loggedInEmployeeNumber, filteredAttendance, startDateStr, endDateStr);
        double basicPay = (totalHoursWorked / 8) * employee.getDailyWage();
        
        // 2. Allowances
        double rice = employee.getRiceSubsidy();
        double phone = employee.getPhoneAllowance();
        double clothing = employee.getClothingAllowance();
        double totalAllowances = rice + phone + clothing;

        // 3. Deductions
        PayrollCalculator calculator = new PayrollCalculator(List.of(employee), filteredAttendance);
        double tax = calculator.computeIncomeTax(basicPay);
        double sss = basicPay * 0.045;
        double philhealth = basicPay * 0.0275;
        double pagibig = Math.min(basicPay * 0.02, 100); 
        double totalDeductions = sss + philhealth + pagibig + tax;

        double netSalary = (basicPay + totalAllowances) - totalDeductions;

        // 4. Update the Text Area with detailed breakdown
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n--------------------------------------------\n");
        sb.append(String.format("PAYROLL BREAKDOWN: %s - %s\n", startDateStr, endDateStr));
        sb.append("--------------------------------------------\n");
        sb.append(String.format("Hours Worked:     %15.2f\n", totalHoursWorked));
        sb.append(String.format("Basic Pay:        %15.2f\n", basicPay));
        sb.append("\nALLOWANCES:\n");
        sb.append(String.format("  Rice Subsidy:   %15.2f\n", rice));
        sb.append(String.format("  Phone:          %15.2f\n", phone));
        sb.append(String.format("  Clothing:       %15.2f\n", clothing));
        sb.append("\nDEDUCTIONS:\n");
        sb.append(String.format("  SSS:            %15.2f\n", sss));
        sb.append(String.format("  PhilHealth:     %15.2f\n", philhealth));
        sb.append(String.format("  Pag-IBIG:       %15.2f\n", pagibig));
        sb.append(String.format("  Withholding Tax:%15.2f\n", tax));
        sb.append("--------------------------------------------\n");
        sb.append(String.format("NET SALARY:       PHP %,.2f\n", netSalary));
        sb.append("--------------------------------------------\n");

        displayLoggedInEmployeeDetails(); // Refresh basic info
        employeeDetailsArea.append(sb.toString()); // Append the new breakdown
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
        // Implementation for logging attendance
    }
}
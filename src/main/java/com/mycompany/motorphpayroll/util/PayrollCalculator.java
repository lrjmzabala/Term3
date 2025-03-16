package com.mycompany.motorphpayroll.util;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

public class PayrollCalculator {

    /**
     * Calculates the total hours worked by an employee.
     */
    public static double calculateTotalHoursWorked(String empNum, List<Attendance> attendanceRecords, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<Attendance> filteredRecords = attendanceRecords.stream()
            .filter(a -> a.getEmployeeNumber().equals(empNum))
            .filter(a -> {
                LocalDate attendanceDate = LocalDate.parse(a.getDate(), formatter);
                return (attendanceDate.isEqual(start) || attendanceDate.isAfter(start)) &&
                       (attendanceDate.isEqual(end) || attendanceDate.isBefore(end));
            })
            .toList();

        if (filteredRecords.isEmpty()) {
            System.out.println("⚠ No attendance records found for Employee ID: " + empNum + " in the selected period.");
            return 0.0;
        }

        // Calculate total hours within the date range
        double totalHours = filteredRecords.stream()
            .mapToDouble(Attendance::getTotalWorkedHours)
            .sum();

        System.out.println("✅ Total hours worked for Employee " + empNum + " from " + startDate + " to " + endDate + ": " + totalHours);
        return totalHours;
    }

    public PayrollCalculator(List<Employee> employees, List<Attendance> attendanceRecords) {
    }

    /**
     * Computes salary for a given employee based on attendance records.
     */
    public double computeSalary(Employee employee, double totalHoursWorked) {
        double dailyWage = employee.getDailyWage();
        double grossSalary = (totalHoursWorked / 8) * dailyWage; // Gross pay before deductions

        // Deductions (Example Values - Adjust Based on Actual Rules)
        double sssDeduction = grossSalary * 0.045;   // 4.5% of salary
        double philhealthDeduction = grossSalary * 0.0275; // 2.75% of salary
        double pagibigDeduction = Math.min(grossSalary * 0.02, 100); // 2% but max of PHP 100

        double netSalary = grossSalary - (sssDeduction + philhealthDeduction + pagibigDeduction);

        // Print Salary Breakdown
        System.out.println("💰 Gross Salary: PHP " + grossSalary);
        System.out.println("📉 Deductions: SSS - " + sssDeduction + ", PhilHealth - " + philhealthDeduction + ", Pag-IBIG - " + pagibigDeduction);
        System.out.println("✅ Net Salary: PHP " + netSalary);

        return netSalary;
    }
} // ✅ Missing closing bracket added here

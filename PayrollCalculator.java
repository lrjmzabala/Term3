package com.mycompany.motorphpayroll.util;

import com.mycompany.motorphpayroll.Deduction.Deduction;
import com.mycompany.motorphpayroll.Deduction.IncomeTaxDeduction;
import com.mycompany.motorphpayroll.Deduction.PagIbigDeduction;
import com.mycompany.motorphpayroll.Deduction.PhilHealthDeduction;
import com.mycompany.motorphpayroll.Deduction.SSSDeduction;
import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;


public class PayrollCalculator {
    
    //private double lastIncomeTax = 0.0;

    private List<Deduction> deductions = List.of(
        new SSSDeduction(),
        new PhilHealthDeduction(),
        new PagIbigDeduction(),
        new IncomeTaxDeduction() // ✅ Optional if IncomeTax needs to store back the value
    );

    private double lastIncomeTax = 0.0; // ✅ Store last computed tax

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

        double totalHours = filteredRecords.stream()
            .mapToDouble(Attendance::getTotalWorkedHours)
            .sum();

        System.out.println("✅ Total hours worked for Employee " + empNum + " from " + startDate + " to " + endDate + ": " + totalHours);
        return totalHours;
    }

    public PayrollCalculator(List<Employee> employees, List<Attendance> attendanceRecords) {
    }

    public double computeSalary(Employee employee, double totalHoursWorked) {
    try {
        double grossSalary = calculateGrossSalary(employee, totalHoursWorked);
        double totalDeductions = calculateDeductions(grossSalary);
        double netSalary = grossSalary - totalDeductions;
        
        printSalaryBreakdown(employee, totalHoursWorked, grossSalary, totalDeductions, netSalary);
        return netSalary;
    } catch (Exception e) {
        System.err.println("❌ Error computing salary: " + e.getMessage());
        return 0.0;
    }
}

/**
 * Calculates gross salary based on hours worked.
 */
private double calculateGrossSalary(Employee employee, double totalHoursWorked) {
    return (totalHoursWorked / 8) * employee.getDailyWage();
}

/**
 * Computes all deductions including SSS, PhilHealth, Pag-IBIG, and Income Tax.
 */
private double calculateDeductions(double grossSalary) {
    double total = 0.0;
    for (Deduction d : deductions) {  // Using the instance variable 'deductions'
        double value = d.compute(grossSalary);
        if (d instanceof IncomeTaxDeduction) {
            lastIncomeTax = value;
        }
        total += value;
    }
    return total;
}

/**
 * Prints a detailed salary breakdown.
 */
private void printSalaryBreakdown(Employee employee, double totalHoursWorked, double grossSalary, double totalDeductions, double netSalary) {
    System.out.println("--------------------------------");
    System.out.printf("💼 Total Hours Worked: %.2f\n", totalHoursWorked);
    System.out.println("📉 Deductions:");
    System.out.printf("  - SSS: PHP %.2f\n", grossSalary * 0.045);
    System.out.printf("  - PhilHealth: PHP %.2f\n", grossSalary * 0.0275);
    System.out.printf("  - Pag-IBIG: PHP %.2f\n", Math.min(grossSalary * 0.02, 100));
    System.out.printf("  - Income Tax: PHP %.2f\n", lastIncomeTax);
    System.out.println("--------------------------------");
    System.out.printf("✅ Net Salary: PHP %.2f\n", netSalary);
    }

    public double getIncomeTax() {
        return lastIncomeTax; // ✅ Returns last computed income tax
    }

    public double computeIncomeTax(double grossSalary) {
        double tax = 0.0;

        if (grossSalary <= 20832) {
            tax = 0.0; // No withholding tax
        } else if (grossSalary <= 33333) {
            tax = 0.20 * (grossSalary - 20833);
        } else if (grossSalary <= 66667) {
            tax = 2500 + 0.25 * (grossSalary - 33333);
        } else if (grossSalary <= 166667) {
            tax = 10833 + 0.30 * (grossSalary - 66667);
        } else if (grossSalary <= 666667) {
            tax = 40833.33 + 0.32 * (grossSalary - 166667);
        } else {
            tax = 200833.33 + 0.35 * (grossSalary - 666667);
        }

        return tax;
    }

} 
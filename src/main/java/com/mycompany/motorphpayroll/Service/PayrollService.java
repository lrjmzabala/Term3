package com.mycompany.motorphpayroll.service;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.util.PayrollCalculator;
import java.util.List;

public class PayrollService {

    public PayrollSummary getPayrollDetails(Employee emp, List<Attendance> attendance, String start, String end) {
        // 1. Calculate hours
        double hours = PayrollCalculator.calculateTotalHoursWorked(emp.getEmployeeNumber(), attendance, start, end);
        
        // 2. Calculate Earnings
        double basicPay = (hours / 8) * emp.getDailyWage();
        double rice = emp.getRiceSubsidy();
        double phone = emp.getPhoneAllowance();
        double clothing = emp.getClothingAllowance();
        double totalAllowances = rice + phone + clothing;
        
        // 3. Calculate Deductions (Business Rules)
        double sss = basicPay * 0.045; 
        double phil = basicPay * 0.0275; 
        double pagibig = Math.min(basicPay * 0.02, 100);
        
        PayrollCalculator calc = new PayrollCalculator(List.of(emp), attendance);
        double tax = calc.computeIncomeTax(basicPay);
        
        double totalDeductions = sss + phil + pagibig + tax;
        double net = (basicPay + totalAllowances) - totalDeductions;

        // 4. Return as DTO including allowances for the UI breakdown
        return new PayrollSummary(hours, basicPay, totalAllowances, sss, phil, pagibig, tax, net);
    }

    // Updated DTO to include 'allowances' so the GUI can display the breakdown
    public record PayrollSummary(double hours, double basic, double allowances, double sss, double phil, double pag, double tax, double net) {}
}
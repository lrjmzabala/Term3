package com.mycompany.motorphpayroll.service;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.util.PayrollCalculator;
import java.util.List;

public class PayrollService {
    public double computeNetSalary(Employee emp, List<Attendance> attendance, String start, String end) {
        double hours = PayrollCalculator.calculateTotalHoursWorked(emp.getEmployeeNumber(), attendance, start, end);
        PayrollCalculator calc = new PayrollCalculator(List.of(emp), attendance);
        return calc.computeSalary(emp, hours);
    }
}
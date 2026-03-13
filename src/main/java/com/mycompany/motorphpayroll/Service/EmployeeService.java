package com.mycompany.motorphpayroll.service;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;

public class EmployeeService {
    public boolean registerEmployee(Employee emp) {
        // Add business validation here (e.g., check if ID exists)
        if (emp.getBasicSalary() < 0) return false;
        return CSVReaderUtil.addEmployeeToCSV(emp);
    }
    
    public void processLeaveApproval(String empId, String date) {
        // Logic for supervisors to approve leaves
        Employee emp = CSVReaderUtil.getEmployeeById(empId);
        if (emp != null && emp.canAccessModule("Leave")) {
            // Proceed to update CSV
        }
    }
}
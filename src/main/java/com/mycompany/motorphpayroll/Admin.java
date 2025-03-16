package com.mycompany.motorphpayroll;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;

/**
 * Admin class responsible for managing employee records.
 */
public class Admin {
    private static final String EMPLOYEE_CSV = "C:\\Users\\Papa\\Downloads\\Copy of MotorPH Employee Data - Employee Details.csv";

    public Admin() {
        System.out.println("Admin panel initialized.");
    }

    /**
     * Adds an employee record to the CSV file.
     *
     * @param empNum Employee ID
     * @param firstName First name
     * @param lastName Last name
     * @param birthday Birthdate
     * @param address Address
     * @param phoneNumber Phone Number
     * @param sssNumber SSS Number
     * @param philHealth PhilHealth Number
     * @param tin Tax Identification Number
     * @param pagIbig Pag-IBIG Number
     * @param status Employment Status
     * @param position Job Position
     * @param supervisor Supervisor Name
     * @param basicSalary Basic Monthly Salary
     * @param riceSubsidy Rice Subsidy Allowance
     * @param phoneAllowance Phone Allowance
     * @param clothingAllowance Clothing Allowance
     * @param grossSemiMonthly Gross Semi-Monthly Pay
     * @param hourlyRate Hourly Wage
     */
    public void addEmployee(String empNum, String firstName, String lastName, 
                        String birthday, String address, String phoneNumber, 
                        String sssNumber, String philHealth, String tin, 
                        String pagibig, String status, String position, String supervisor, // 🔥 Added these
                        double basicSalary, double riceSubsidy, 
                        double phoneAllowance, double clothingAllowance, 
                        double grossSemiMonthly, double hourlyRate) {

        // ✅ Correct way: Assign values directly to the Employee object
        Employee employee = new Employee(empNum, lastName, firstName, birthday, address, phoneNumber, 
                                         sssNumber, philHealth, tin, pagibig, status, 
                                         position, supervisor, basicSalary, riceSubsidy,
                                         phoneAllowance, clothingAllowance, grossSemiMonthly, hourlyRate);

        // Call CSVReaderUtil to handle writing to the CSV file
        CSVReaderUtil.addEmployeeToCSV(employee);
        System.out.println("✅ Employee added successfully: " + firstName + " " + lastName);
    }
    
    /**
 * Retrieves employee details based on Employee Number.
 * @param empNum The Employee Number to search for.
 * @return Employee details as a formatted string, or null if not found.
 */
public String getEmployeeDetails(String empNum) {
    for (Employee emp : CSVReaderUtil.readEmployeesFromCSV(EMPLOYEE_CSV)) {
        if (emp.getEmployeeNumber().equals(empNum)) {
            return emp.toString(); // Ensure Employee has a proper toString() method
        }
    }
    return null; // Employee not found
}

    
    
}

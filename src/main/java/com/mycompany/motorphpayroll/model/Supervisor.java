package com.mycompany.motorphpayroll.model;

import java.util.List;

// Supervisor inherits from Employee
public class Supervisor extends Employee {
    private List<String> subordinateIds;

    public Supervisor(String employeeNumber, String lastName, String firstName, String birthday, String address, 
                      String phoneNumber, String sssNumber, String philhealthNumber, String tinNumber, 
                      String pagibigNumber, String status, String position, String supervisor, 
                      double basicSalary, double riceSubsidy, double phoneAllowance, 
                      double clothingAllowance, double grossSemiMonthlyRate, double hourlyRate) {
        
        // 'super' calls the constructor of the Employee class
        super(employeeNumber, lastName, firstName, birthday, address, phoneNumber, sssNumber, 
              philhealthNumber, tinNumber, pagibigNumber, status, position, supervisor, 
              basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, 
              grossSemiMonthlyRate, hourlyRate);
    }

    public void approveLeave(String employeeId, String startDate) {
        System.out.println("Supervisor " + getFullName() + " approved leave for Employee #" + employeeId);
        // Logic to update CSV status from PENDING to APPROVED would go here
    }
}
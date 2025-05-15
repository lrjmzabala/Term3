package com.mycompany.motorphpayroll;

import com.mycompany.motorphpayroll.model.Person;
import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;

/**
 * Admin class responsible for managing employee records.
 */
public class Admin extends Person {
    private static final String EMPLOYEE_CSV = "C:\\Users\\Papa\\Downloads\\Copy of MotorPH Employee Data - Employee Details.csv";

    public Admin(String firstName, String lastName, String phoneNumber) {
        super(firstName, lastName, phoneNumber);
        System.out.println("Admin panel initialized.");
    }

    @Override
    public String getRoleDescription() {
        return "Admin: " + getFullName();
    }

    public void addEmployee(String empNum, String firstName, String lastName, 
                        String birthday, String address, String phoneNumber, 
                        String sssNumber, String philHealth, String tin, 
                        String pagibig, String status, String position, String supervisor, 
                        double basicSalary, double riceSubsidy, 
                        double phoneAllowance, double clothingAllowance, 
                        double grossSemiMonthly, double hourlyRate) {

        Employee employee = new Employee(empNum, lastName, firstName, birthday, address, phoneNumber, 
                                         sssNumber, philHealth, tin, pagibig, status, 
                                         position, supervisor, basicSalary, riceSubsidy,
                                         phoneAllowance, clothingAllowance, grossSemiMonthly, hourlyRate);

        CSVReaderUtil.addEmployeeToCSV(employee);
        System.out.println("✅ Employee added successfully: " + employee.getFullName());
    }

    public String getEmployeeDetails(String empNum) {
        for (Employee emp : CSVReaderUtil.readEmployeesFromCSV(EMPLOYEE_CSV)) {
            if (emp.getEmployeeNumber().equals(empNum)) {
                return emp.toString();
            }
        }
        return null;
    }
}
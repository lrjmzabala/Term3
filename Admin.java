package com.mycompany.motorphpayroll;

import com.mycompany.motorphpayroll.model.Person;
import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import java.util.List; // Import List for reading all employees

/**
 * Admin class responsible for managing employee records.
 */
public class Admin extends Person {
    // EMPLOYEE_CSV constant should ideally be in CSVReaderUtil and accessed from there.
    // private static final String EMPLOYEE_CSV = "C:\\Users\\Papa\\Downloads\\Copy of MotorPH Employee Data - Employee Details.csv";

    public Admin(String firstName, String lastName, String phoneNumber) {
        super(firstName, lastName, phoneNumber);
        System.out.println("Admin panel initialized.");
        // Ensure cache is loaded when Admin starts or before first operation
        CSVReaderUtil.loadEmployeesToCache();
    }

    @Override
    public String getRoleDescription() {
        return "Admin: " + getFullName();
    }

    /**
     * Adds a new employee record to the CSV file.
     * @param empNum Employee Number
     * @param firstName First Name
     * @param lastName Last Name
     * @param birthday Birthday
     * @param address Address
     * @param phoneNumber Phone Number
     * @param sssNumber SSS Number
     * @param philHealth Philhealth Number
     * @param tin TIN Number
     * @param pagibig Pag-IBIG Number
     * @param status Status
     * @param position Position
     * @param supervisor Supervisor
     * @param basicSalary Basic Salary
     * @param riceSubsidy Rice Subsidy
     * @param phoneAllowance Phone Allowance
     * @param clothingAllowance Clothing Allowance
     * @param grossSemiMonthly Gross Semi-Monthly Rate
     * @param hourlyRate Hourly Rate
     */
    public void addEmployee(String empNum, String firstName, String lastName,
                            String birthday, String address, String phoneNumber,
                            String sssNumber, String philHealth, String tin,
                            String pagibig, String status, String position, String supervisor,
                            double basicSalary, double riceSubsidy,
                            double phoneAllowance, double clothingAllowance,
                            double grossSemiMonthly, double hourlyRate) {

        // Check if employee number already exists
        if (CSVReaderUtil.getEmployeeById(empNum) != null) {
            System.out.println("❌ Error: Employee with ID " + empNum + " already exists. Cannot add.");
            return;
        }

        Employee employee = new Employee(empNum, lastName, firstName, birthday, address, phoneNumber,
                                         sssNumber, philHealth, tin, pagibig, status,
                                         position, supervisor, basicSalary, riceSubsidy,
                                         phoneAllowance, clothingAllowance, grossSemiMonthly, hourlyRate);

        CSVReaderUtil.addEmployeeToCSV(employee);
    }

    /**
     * Retrieves the details of a specific employee as a formatted string.
     * @param empNum The employee number.
     * @return A formatted string of employee details, or null if not found.
     */
    public String getEmployeeDetails(String empNum) {
        // Use the getEmployeeById from CSVReaderUtil for better performance (uses cache)
        Employee emp = CSVReaderUtil.getEmployeeById(empNum);
        if (emp != null) {
            return emp.toString();
        }
        return null; // Employee not found
    }

    // --- Add the new method here ---
    /**
     * Retrieves a specific Employee object by employee number.
     * This is useful for populating GUI fields.
     * @param empNum The employee number.
     * @return The Employee object if found, or null otherwise.
     */
    public Employee getEmployeeDetailsObject(String empNum) {
        return CSVReaderUtil.getEmployeeById(empNum);
    }
    // --- End of new method ---

    /**
     * Updates an existing employee's record.
     * @param empNum Employee Number of the employee to update
     * @param firstName New First Name
     * @param lastName New Last Name
     * @param birthday New Birthday
     * @param address New Address
     * @param phoneNumber New Phone Number
     * @param sssNumber New SSS Number
     * @param philHealth New Philhealth Number
     * @param tin New TIN Number
     * @param pagibig New Pag-IBIG Number
     * @param status New Status
     * @param position New Position
     * @param supervisor New Supervisor
     * @param basicSalary New Basic Salary
     * @param riceSubsidy New Rice Subsidy
     * @param phoneAllowance New Phone Allowance
     * @param clothingAllowance New Clothing Allowance
     * @param grossSemiMonthly New Gross Semi-Monthly Rate
     * @param hourlyRate New Hourly Rate
     * @return true if updated successfully, false otherwise (e.g., employee not found).
     */
    public boolean updateEmployee(String empNum, String firstName, String lastName,
                                  String birthday, String address, String phoneNumber,
                                  String sssNumber, String philHealth, String tin,
                                  String pagibig, String status, String position, String supervisor,
                                  double basicSalary, double riceSubsidy,
                                  double phoneAllowance, double clothingAllowance,
                                  double grossSemiMonthly, double hourlyRate) {
        // First, check if the employee exists
        Employee existingEmployee = CSVReaderUtil.getEmployeeById(empNum);
        if (existingEmployee == null) {
            System.out.println("❌ Error: Employee with ID " + empNum + " not found for update.");
            return false;
        }

        // Create a new Employee object with the updated details
        Employee updatedEmployee = new Employee(empNum, lastName, firstName, birthday, address, phoneNumber,
                                                 sssNumber, philHealth, tin, pagibig, status,
                                                 position, supervisor, basicSalary, riceSubsidy,
                                                 phoneAllowance, clothingAllowance, grossSemiMonthly, hourlyRate);

        return CSVReaderUtil.updateEmployeeInCSV(updatedEmployee);
    }

    /**
     * Deletes an employee record by their employee number.
     * @param empNum The employee number of the employee to delete.
     * @return true if deleted successfully, false otherwise.
     */
    public boolean deleteEmployee(String empNum) {
        return CSVReaderUtil.deleteEmployeeFromCSV(empNum);
    }

    // Optional: Method to get all employees if needed for display purposes in Admin panel
    public List<Employee> getAllEmployees() {
        return CSVReaderUtil.readEmployeesFromCSV(CSVReaderUtil.EMPLOYEE_CSV);
    }
}
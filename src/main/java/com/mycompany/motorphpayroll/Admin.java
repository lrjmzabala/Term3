package com.mycompany.motorphpayroll;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.DAO.EmployeeDAO;
import java.util.Map;

public class Admin {
    private boolean isAdmin = false;
    private EmployeeDAO employeeDAO;

    public Admin(Employee employee) {
        this.employeeDAO = new EmployeeDAO();
        if (employee != null && "HR".equalsIgnoreCase(employee.getPosition())) {
            this.isAdmin = true;
        }
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public void updateEmployeeDetails(String employeeNumber, Employee updatedInfo) throws IllegalAccessException {
        if (!isAdmin) {
            throw new IllegalAccessException("Access Denied.");
        }

        Map<String, Employee> employees = employeeDAO.getAllEmployees();
        if (employees.containsKey(employeeNumber)) {
            employeeDAO.updateEmployee(updatedInfo);
            System.out.println("✅ Employee updated via DAO.");
        }
    }
}
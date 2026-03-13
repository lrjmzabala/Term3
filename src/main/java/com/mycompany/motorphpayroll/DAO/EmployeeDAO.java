package com.mycompany.motorphpayroll.DAO;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {

    public Employee getEmployeeById(String employeeId) {
        return CSVReaderUtil.getEmployeeById(employeeId);
    }

    public List<Employee> getAllEmployeesList() {
        return CSVReaderUtil.getAllEmployeesList();
    }
    
    public Map<String, Employee> getAllEmployees() {
        return CSVReaderUtil.getAllEmployees();
    }
    
    public void updateEmployee(Employee employee) {
        CSVReaderUtil.updateEmployeeInCSV(employee);
    }

    public void addEmployee(Employee employee) throws Exception {
        CSVReaderUtil.addEmployeeToCSV(employee);
    }
}
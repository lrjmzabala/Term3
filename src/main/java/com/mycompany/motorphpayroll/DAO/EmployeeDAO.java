package com.mycompany.motorphpayroll.DAO;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {

    public Employee getEmployeeById(String employeeId) {
        return CSVReaderUtil.getEmployeeById(employeeId);
    }

    // Used by MotorPhPayroll
    public List<Employee> getAllEmployeesList() {
        return new ArrayList<>(CSVReaderUtil.getAllEmployees().values());
    }
    
    // Used by Admin
    public Map<String, Employee> getAllEmployees() {
        return CSVReaderUtil.getAllEmployees();
    }
    
    public void updateEmployee(Employee employee) {
        CSVReaderUtil.updateEmployeeInCSV(employee);
    }
}
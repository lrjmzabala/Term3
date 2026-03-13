package com.mycompany.motorphpayroll.DAO;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Supervisor;
import com.mycompany.motorphpayroll.model.RegularEmployee;
import com.mycompany.motorphpayroll.model.Admin; // Add this import
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {

    // 1. The Factory Method: Creates the specific subclass based on position
    public Employee getEmployeeById(String employeeId) {
        // Ensure this method name matches what is in your CSVReaderUtil
        String[] data = CSVReaderUtil.getRawDataById(employeeId); 
        if (data == null) return null;

        String position = data[11]; // Assuming index 11 is 'Position'

        // Instantiate based on role
        if ("HR Manager".equalsIgnoreCase(position)) {
            return new Admin(data);
        } else if ("Supervisor".equalsIgnoreCase(position)) {
            return new Supervisor(data);
        } else {
            return new RegularEmployee(data);
        }
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
    
    private String[] convertEmployeeToArray(Employee e) {
        return new String[] {
            e.getEmployeeNumber(), e.getLastName(), e.getFirstName(), e.getBirthday(),
            e.getAddress(), e.getPhoneNumber(), e.getSssNumber(), e.getPhilhealthNumber(),
            e.getTinNumber(), e.getPagibigNumber(), e.getStatus(), e.getPosition(),
            e.getSupervisor(), String.valueOf(e.getBasicSalary()), String.valueOf(e.getRiceSubsidy()),
            String.valueOf(e.getPhoneAllowance()), String.valueOf(e.getClothingAllowance()),
            String.valueOf(e.getGrossSemiMonthlyRate()), String.valueOf(e.getHourlyRate())
        };
    }
}
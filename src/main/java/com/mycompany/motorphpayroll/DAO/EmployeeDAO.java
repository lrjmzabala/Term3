package com.mycompany.motorphpayroll.DAO;

import com.mycompany.motorphpayroll.model.*;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {

    // 1. The Factory Method: Creates the specific subclass based on position
    public Employee getEmployeeById(String employeeId) {
    String[] data = CSVReaderUtil.getRawDataById(employeeId); 
    if (data == null) return null;

    String pos = data[11];

    if ("HR Manager".equalsIgnoreCase(pos)) return new Admin(data);
    if ("Supervisor".equalsIgnoreCase(pos)) return new Supervisor(data);
    if ("IT".equalsIgnoreCase(pos)) return new IT(data);
    if ("Finance".equalsIgnoreCase(pos)) return new Finance(data);
    
    return new RegularEmployee(data); // Default
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
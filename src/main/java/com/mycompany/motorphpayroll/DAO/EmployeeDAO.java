package com.mycompany.motorphpayroll.DAO;

import com.mycompany.motorphpayroll.model.*;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import com.mycompany.motorphpayroll.util.CSVWriterUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {

    public Employee getEmployeeById(String employeeId) {
        String[] data = CSVReaderUtil.getRawDataById(employeeId); 
        if (data == null) return null;

        String pos = data[11];
        if ("HR Manager".equalsIgnoreCase(pos)) return new Admin(data);
        if ("Supervisor".equalsIgnoreCase(pos)) return new Supervisor(data);
        if ("IT".equalsIgnoreCase(pos)) return new IT(data);
        if ("Finance".equalsIgnoreCase(pos)) return new Finance(data);
        
        return new RegularEmployee(data);
    }

    public List<Employee> getAllEmployeesList() {
        return CSVReaderUtil.getAllEmployeesList();
    }
    
    public Map<String, Employee> getAllEmployees() {
        return CSVReaderUtil.getAllEmployees();
    }
    
    public void updateEmployee(Employee employee) throws IOException {
        CSVReaderUtil.updateEmployeeInCSV(employee);
        // Fix: Convert the employee object to array before passing to writer
        String[] data = convertEmployeeToArray(employee);
        CSVWriterUtil.writeToCSV("employee_details.csv", data);
    }

    public void addEmployee(Employee employee) throws Exception {
        // Fix: Convert to array AND write to CSV
        String[] data = convertEmployeeToArray(employee);
        CSVWriterUtil.writeToCSV("employee_details.csv", data);
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
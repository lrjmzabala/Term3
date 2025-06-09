package com.mycompany.motorphpayroll.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.util.CSVReaderUtil; // Ensure this import is correct

public class ViewEmployeesPanel extends JPanel {
    private JTable employeeTable;

    public ViewEmployeesPanel() {
        setLayout(new BorderLayout());

        String[] columns = {
            "Employee Number", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
            "SSS Number", "Philhealth Number", "TIN Number", "PagIbig Number", "Status",
            "Position", "Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
            "Clothing Allowance", "Daily Rate"
        };

        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        employeeTable = new JTable(tableModel);

        // --- CORRECTED LINE BELOW ---
        // Instead of "EMPLOYEE_CSV" (a string literal), use CSVReaderUtil.EMPLOYEE_CSV
        List<Employee> employees = CSVReaderUtil.readEmployeesFromCSV(CSVReaderUtil.EMPLOYEE_CSV);

        if (employees.isEmpty()) {
            System.out.println("No employee data loaded. Check CSV file path and content.");
        } else {
            for (Employee emp : employees) {
                tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getBirthday(),
                    emp.getAddress(),
                    emp.getPhoneNumber(),
                    emp.getSssNumber(),
                    emp.getPhilhealthNumber(),
                    emp.getTinNumber(),
                    emp.getPagibigNumber(),
                    emp.getStatus(),
                    emp.getPosition(),
                    emp.getSupervisor(),
                    emp.getBasicSalary(),
                    emp.getRiceSubsidy(),
                    emp.getPhoneAllowance(),
                    emp.getClothingAllowance(),
                    emp.getDailyRate()
                });
            }
        }

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);
    }
}
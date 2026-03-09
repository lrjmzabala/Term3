package com.mycompany.motorphpayroll;

import com.mycompany.motorphpayroll.DAO.EmployeeDAO;
import com.mycompany.motorphpayroll.service.PayrollService;
import com.mycompany.motorphpayroll.service.SecurityService;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import com.mycompany.motorphpayroll.model.*;
import com.mycompany.motorphpayroll.GUI.SupervisorPanel;
import javax.swing.*;
import java.util.*;

public class MotorPhPayroll {
    private static final EmployeeDAO employeeDAO = new EmployeeDAO();
    private static final PayrollService payrollService = new PayrollService();
    private static final SecurityService securityService = new SecurityService();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CSVReaderUtil.loadEmployeesToCache();
        
        while (true) {
            System.out.println("\n--- MotorPH Payroll System ---");
            System.out.println("1. Admin Panel | 2. Employee Portal | 3. Exit | 4. Supervisor Portal");
            System.out.print("Choice: ");
            int choice = scanner.nextInt(); scanner.nextLine();

            switch (choice) {
                case 1 -> adminMenu(scanner);
                case 2 -> employeeMenu(scanner);
                case 4 -> {
                    System.out.print("Enter Supervisor ID to access Portal: ");
                    String supId = scanner.nextLine();
                    Employee supervisor = employeeDAO.getEmployeeById(supId);
                    List<Employee> allEmployees = new ArrayList<>(employeeDAO.getAllEmployees().values());
                    
                    if (supervisor != null && securityService.isSupervisor(supervisor, allEmployees)) {
                        launchSupervisorGUI(supervisor, allEmployees);
                    } else {
                        System.out.println("❌ Access Denied: You are not listed as a supervisor.");
                    }
                }
                case 3 -> System.exit(0);
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void adminMenu(Scanner scanner) {
        System.out.print("Enter ID: ");
        String empId = scanner.nextLine();
        Employee emp = employeeDAO.getEmployeeById(empId);
        
        if (emp == null) {
            System.out.println("⚠ Employee not found.");
            return;
        }

        if (!securityService.canAccessAdmin(emp)) {
            System.out.println("❌ Access Denied: HR/Admin Only.");
            return;
        }
        System.out.println("✅ Welcome, HR Admin.");
    }

    private static void handleSalaryCalculation(Scanner scanner, Employee employee) {
        List<Attendance> attendance = CSVReaderUtil.readAttendanceFromCSV("src/main/resources/MotorPH Attendance Data.csv");
        
        System.out.print("Enter Start Date (MM/DD/YYYY): ");
        String start = scanner.nextLine();
        System.out.print("Enter End Date (MM/DD/YYYY): ");
        String end = scanner.nextLine();

        // FIX: Call the updated service method that returns the PayrollSummary record
        PayrollService.PayrollSummary summary = payrollService.getPayrollDetails(employee, attendance, start, end);
        
        // Display the results using the new structure
        System.out.println("\n--- PAYROLL SUMMARY ---");
        System.out.println("Hours Worked:     " + String.format("%.2f", summary.hours()));
        System.out.println("Basic Pay:        PHP " + String.format("%,.2f", summary.basic()));
        System.out.println("Total Allowances: PHP " + String.format("%,.2f", summary.allowances()));
        System.out.println("Deductions:       PHP " + String.format("%,.2f", (summary.sss() + summary.phil() + summary.pag() + summary.tax())));
        System.out.println("-----------------------");
        System.out.println("💰 Net Salary:    PHP " + String.format("%,.2f", summary.net()));
    }

    // UPDATED: Now accepts the required parameters for SupervisorPanel
    private static void launchSupervisorGUI(Employee supervisor, List<Employee> allEmployees) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MotorPH Supervisor Portal - " + supervisor.getLastName());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // Passing the required data to the constructor
            frame.add(new SupervisorPanel(supervisor, allEmployees));
            frame.pack();
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void employeeMenu(Scanner scanner) {
        System.out.print("\nEnter your Employee Number: ");
        String empNum = scanner.nextLine();
        Employee employee = employeeDAO.getEmployeeById(empNum);
        if (employee == null) {
            System.out.println("⚠ Employee not found.");
            return;
        }
        System.out.println("✅ Welcome, " + employee.getFirstName());
    }
}
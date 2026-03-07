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
            int choice = scanner.nextInt(); scanner.nextLine();

            switch (choice) {
                case 1 -> adminMenu(scanner);
                case 2 -> employeeMenu(scanner);
                case 4 -> launchSupervisorGUI();
                default -> System.exit(0);
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

        double salary = payrollService.computeNetSalary(employee, attendance, start, end);
        
        System.out.println("\n💰 Net Salary: PHP " + String.format("%,.2f", salary));
    }

    private static void launchSupervisorGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MotorPH Supervisor Portal");
            frame.add(new SupervisorPanel());
            frame.pack();
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
    }
}
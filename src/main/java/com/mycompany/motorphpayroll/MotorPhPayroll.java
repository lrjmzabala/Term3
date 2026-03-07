package com.mycompany.motorphpayroll;

import com.mycompany.motorphpayroll.DAO.EmployeeDAO;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import com.mycompany.motorphpayroll.util.PayrollCalculator;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.GUI.SupervisorPanel;
import javax.swing.*;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MotorPhPayroll {
    // Inject the DAO here
    private static final EmployeeDAO employeeDAO = new EmployeeDAO();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Ensure cache is loaded
        CSVReaderUtil.loadEmployeesToCache();
        
        while (true) {
            System.out.println("\n--- MotorPH Payroll System ---");
            System.out.println("1. Admin Panel (HR Only)");
            System.out.println("2. Employee Portal");
            System.out.println("3. Exit");
            System.out.println("4. Supervisor Portal (GUI)");
            System.out.print("Enter choice: ");
            
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                continue;
            }
            
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1 -> adminMenu(scanner);
                case 2 -> employeeMenu(scanner);
                case 3 -> {
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                }
                case 4 -> {
                    SwingUtilities.invokeLater(() -> {
                        JFrame frame = new JFrame("MotorPH Supervisor Portal");
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame.add(new SupervisorPanel());
                        frame.pack();
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);
                    });
                    System.out.println("🚀 Supervisor GUI launched.");
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void adminMenu(Scanner scanner) {
        System.out.print("Enter Admin/HR Employee ID to verify: ");
        String adminId = scanner.nextLine();
        
        // USE DAO
        Employee potentialAdmin = employeeDAO.getEmployeeById(adminId);

        Admin admin = new Admin(potentialAdmin);

        if (!admin.isAdmin()) {
            System.out.println("❌ Access Denied: Only HR staff can access the Admin Panel.");
            return;
        }

        System.out.println("✅ Welcome, HR Administrator: " + potentialAdmin.getFirstName());

        while (true) {
            System.out.println("\nAdmin Panel:");
            System.out.println("1. Add Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter choice: ");
            int adminChoice = scanner.nextInt();
            scanner.nextLine(); 
            
            if (adminChoice == 1) {
                System.out.println("Feature: Add Employee (Input logic goes here)");
            } else if (adminChoice == 2) {
                displayEmployees();
            } else if (adminChoice == 3) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void displayEmployees() {
        // USE DAO
        List<Employee> employees = employeeDAO.getAllEmployeesList(); 
        System.out.println("\n--- All Employee Details ---");
        for (Employee emp : employees) {
            System.out.println(emp);
            System.out.println("----------------------");
        }
    }

    private static void employeeMenu(Scanner scanner) {
        System.out.print("\nEnter your Employee Number: ");
        String empNum = scanner.nextLine();

        // USE DAO
        Employee employee = employeeDAO.getEmployeeById(empNum);
        if (employee == null) {
            System.out.println("⚠ Employee not found.");
            return;
        }

        while (true) {
            System.out.println("\nEmployee Menu:");
            System.out.println("1. View My Details");
            System.out.println("2. View My Salary");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter choice: ");
            int empChoice = scanner.nextInt();
            scanner.nextLine(); 

            if (empChoice == 1) {
                System.out.println("\n👤 Your Details:\n" + employee);
            } else if (empChoice == 2) {
                handleSalaryCalculation(scanner, employee, empNum);
            } else if (empChoice == 3) {
                return;
            }
        }
    }

    private static void handleSalaryCalculation(Scanner scanner, Employee employee, String empNum) {
        String attendanceFile = "src/main/resources/MotorPH Attendance Data.csv"; 
        List<Attendance> allAttendanceRecords = CSVReaderUtil.readAttendanceFromCSV(attendanceFile);
        
        List<Attendance> employeeAttendance = allAttendanceRecords.stream()
            .filter(a -> a.getEmployeeNumber().equals(empNum))
            .collect(Collectors.toList());

        System.out.print("Enter Start Date (MM/DD/YYYY): ");
        String startDate = scanner.nextLine();
        System.out.print("Enter End Date (MM/DD/YYYY): ");
        String endDate = scanner.nextLine();

        List<Attendance> filteredAttendance = employeeAttendance.stream()
            .filter(a -> a.isWithinDateRange(startDate, endDate))
            .collect(Collectors.toList());

        if (filteredAttendance.isEmpty()) {
            System.out.println("⚠ No records found for this period.");
            return;
        }

        double totalHoursWorked = PayrollCalculator.calculateTotalHoursWorked(empNum, filteredAttendance, startDate, endDate);
        PayrollCalculator calculator = new PayrollCalculator(List.of(employee), filteredAttendance);
        double salary = calculator.computeSalary(employee, totalHoursWorked);

        System.out.println("\n💰 Net Salary for " + startDate + " to " + endDate + ": PHP " + String.format("%,.2f", salary));
    }
}
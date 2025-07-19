package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.User; // Import the User class if not already
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import javax.swing.*;
import java.awt.*;

public class mainframe extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel adminPanel;
    private JPanel viewAllEmployeesPanel;
    private JPanel employeeSelfServicePanel; // To be used for individual employee view

    public mainframe() {
        setTitle("MotorPH Payroll System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600); // Adjust size as needed
        setLocationRelativeTo(null); // Center the frame

        // Initialize CSV paths and load initial data before any login attempts
        CSVReaderUtil.initializeWritableCsvPaths();
        CSVReaderUtil.loadEmployeesToCache(); // Ensure employee data is loaded if needed globally

        // Create and show the Login Dialog first
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);

        // After the dialog closes, check if login was successful
        if (loginDialog.isLoggedIn()) {
            String role = loginDialog.getUserRole();
            // ⭐ CORRECTED LINE: Use the getter method
            String loggedInUsername = loginDialog.getUsername(); 

            System.out.println("User logged in: " + loggedInUsername + " with role: " + role); // For debugging

            // Initialize main UI components
            tabbedPane = new JTabbedPane();
            add(tabbedPane, BorderLayout.CENTER);

            // Initialize panels
            adminPanel = new AdminPanel(); // Assuming AdminPanel exists
            viewAllEmployeesPanel = new ViewEmployeesPanel(); // Assuming ViewEmployeesPanel exists and shows all employees

            // Add panels based on role
            if ("Admin".equalsIgnoreCase(role)) { // Compare role (case-insensitive)
                tabbedPane.addTab("Admin Panel", adminPanel);
                tabbedPane.addTab("View All Employees", viewAllEmployeesPanel);
                // Add any other admin-specific panels
            } else if ("Employee".equalsIgnoreCase(role)) { // For Employee role
                // Create the EmployeePanel for the logged-in employee
                // The username from LoginDialog is typically the employee number for employees
                employeeSelfServicePanel = new EmployeePanel(loggedInUsername); // Pass the logged-in employee number
                tabbedPane.addTab("My Details & Salary", employeeSelfServicePanel);
            }
            // You can add more roles/conditions as needed

            // Make the mainframe visible ONLY after successful login and UI setup
            setVisible(true);
        } else {
            // Login failed or was cancelled, exit the application
            JOptionPane.showMessageDialog(this, "Login failed or cancelled. Exiting application.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new mainframe();
        });
    }
}
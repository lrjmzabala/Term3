package com.mycompany.motorphpayroll.GUI;

import javax.swing.*;
import java.awt.*; // Import for card layout

/**
 * Main application frame handling login and panel display.
 */
public class mainframe extends JFrame {

    private JPanel cardPanel; // Panel to hold different views (login, admin, employee)
    private CardLayout cardLayout; // Layout manager for cardPanel

    public mainframe() {
        setTitle("MotorPH Payroll System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Initialize CardLayout and its panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel); // Add the card panel to the mainframe

        // This is more of a placeholder, as the JDialog handles the initial login.
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Please log in to continue.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 24));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        cardPanel.add(welcomePanel, "Welcome");


        // Show the login dialog first
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true); // This call blocks until dialog is closed

        // After the dialog closes, check if login was successful
        if (loginDialog.isLoggedIn()) {
            String role = loginDialog.getUserRole();
            if (role.equals("Admin")) {
                setupAdminView(); // Method to set up Admin specific tabs
                cardLayout.show(cardPanel, "MainTabs"); // Show the main application view
            } else if (role.equals("Employee")) {
                setupEmployeeView(); // Method to set up Employee specific tabs
                cardLayout.show(cardPanel, "MainTabs"); // Show the main application view
            } else {
                // Should not happen if roles are correctly handled
                JOptionPane.showMessageDialog(this, "Unknown role. Exiting.", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } else {
            // User cancelled login or failed to log in
            JOptionPane.showMessageDialog(this, "Login cancelled or failed. Exiting application.", "Exiting", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    private void setupAdminView() {
        JTabbedPane adminTabbedPane = new JTabbedPane();
        adminTabbedPane.addTab("Admin Panel", new AdminPanel()); 
        adminTabbedPane.addTab("View All Employees", new ViewEmployeesPanel()); // General view for admin
      
        cardPanel.add(adminTabbedPane, "MainTabs"); // Add to card panel
    }

    private void setupEmployeeView() {
        JTabbedPane employeeTabbedPane = new JTabbedPane();
        employeeTabbedPane.addTab("Employee Dashboard", new EmployeePanel());
       
        cardPanel.add(employeeTabbedPane, "MainTabs"); // Add to card panel
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            mainframe frame = new mainframe();
            frame.setVisible(true);
        });
    }
}
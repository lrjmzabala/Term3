package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.User;
import com.mycompany.motorphpayroll.util.CSVReaderUtil; // Keep this import
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class mainframe extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel adminPanel;
    private JPanel viewAllEmployeesPanel;
    private JPanel employeeSelfServicePanel;

    public mainframe() {
        setTitle("MotorPH Payroll System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        CSVReaderUtil.loadAllDataToCache();

        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);

        if (loginDialog.isLoggedIn()) {
            String role = loginDialog.getUserRole();
            String loggedInUsername = loginDialog.getUsername();

            System.out.println("User logged in: " + loggedInUsername + " with role: " + role);

            setupMenuBar(); // Call the new method to set up the menu bar

            tabbedPane = new JTabbedPane();
            add(tabbedPane, BorderLayout.CENTER);

            adminPanel = new AdminPanel();
            viewAllEmployeesPanel = new ViewEmployeesPanel();

            if ("Admin".equalsIgnoreCase(role)) {
                tabbedPane.addTab("Admin Panel", adminPanel);
                tabbedPane.addTab("View All Employees", viewAllEmployeesPanel);
            } else if ("Employee".equalsIgnoreCase(role)) {
                employeeSelfServicePanel = new EmployeePanel(loggedInUsername);
                tabbedPane.addTab("My Details & Salary", employeeSelfServicePanel);
            }

            setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Login failed or cancelled. Exiting application.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // CHANGE THIS LINE: Change "File" to "Menu" or "Options"
        JMenu appMenu = new JMenu("Menu"); // Changed from "File" to "Menu"

        // Sign Out Menu Item
        JMenuItem signOutMenuItem = new JMenuItem("Sign Out");
        signOutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignOut();
            }
        });
        appMenu.add(signOutMenuItem); // Add Sign Out to the new menu

        // Exit Menu Item
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit the application
            }
        });
        appMenu.add(exitMenuItem); // Add Exit to the new menu

        menuBar.add(appMenu); // Add the new menu to the menu bar
        setJMenuBar(menuBar); // Set this menu bar to the JFrame
    }

    private void handleSignOut() {
        this.dispose();

        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new mainframe();
        });
    }
}
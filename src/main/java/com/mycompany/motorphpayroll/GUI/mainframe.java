package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.User;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class mainframe extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel adminPanel;
    private JPanel viewAllEmployeesPanel;
    private JPanel employeeSelfServicePanel;
    private JPanel supervisorPanel; // ✅ New panel for Supervisor

    public mainframe() {
        setTitle("MotorPH Payroll System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        Color backgroundColor = new Color(135, 206, 235); // Sky Blue
        getContentPane().setBackground(backgroundColor);

        // Ensure data is loaded
        CSVReaderUtil.loadEmployeesToCache();

        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);

        if (loginDialog.isLoggedIn()) {
            String role = loginDialog.getUserRole();
            String loggedInUsername = loginDialog.getUsername();

            setupMenuBar();

            tabbedPane = new JTabbedPane();
            add(tabbedPane, BorderLayout.CENTER);

            // --- ROLE-BASED TAB CONFIGURATION ---
            
            // 1. ADMIN ROLE: Full access to management tools
            if ("Admin".equalsIgnoreCase(role)) {
                adminPanel = new AdminPanel();
                viewAllEmployeesPanel = new ViewEmployeesPanel();
                tabbedPane.addTab("Admin Panel", adminPanel);
                tabbedPane.addTab("View All Employees", viewAllEmployeesPanel);
            } 
            
            // 2. SUPERVISOR ROLE: Self-service + Leave Approval Portal
            else if ("Supervisor".equalsIgnoreCase(role)) {
                employeeSelfServicePanel = new EmployeePanel(loggedInUsername);
                supervisorPanel = new SupervisorPanel(); // ✅ The panel with "Approve Leave"
                
                tabbedPane.addTab("My Details & Salary", employeeSelfServicePanel);
                tabbedPane.addTab("Supervisor Portal (Leave Approvals)", supervisorPanel);
            } 
            
            // 3. REGULAR EMPLOYEE: Only their own details
            else if ("Employee".equalsIgnoreCase(role)) {
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
        JMenu appMenu = new JMenu("Menu");

        JMenuItem signOutMenuItem = new JMenuItem("Sign Out");
        signOutMenuItem.addActionListener(e -> handleSignOut());
        
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));

        appMenu.add(signOutMenuItem);
        appMenu.addSeparator(); // Visually cleaner
        appMenu.add(exitMenuItem);

        menuBar.add(appMenu);
        setJMenuBar(menuBar);
    }

    private void handleSignOut() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new mainframe(); // Re-launch the mainframe which triggers LoginDialog
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(mainframe::new);
    }
}
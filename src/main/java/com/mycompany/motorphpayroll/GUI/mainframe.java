package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.DAO.EmployeeDAO; 
import com.mycompany.motorphpayroll.model.Employee; 
import com.mycompany.motorphpayroll.service.SecurityService;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class mainframe extends JFrame {

    private final SecurityService securityService = new SecurityService();
    private final EmployeeDAO employeeDAO = new EmployeeDAO(); 
    private JTabbedPane tabbedPane;

    public mainframe() {
        setTitle("MotorPH Payroll System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(135, 206, 235));

        CSVReaderUtil.loadEmployeesToCache();

        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);

        if (loginDialog.isLoggedIn()) {
            String username = loginDialog.getUsername();
            Employee loggedInUser = employeeDAO.getEmployeeById(username);
            List<Employee> allEmployees = new ArrayList<>(employeeDAO.getAllEmployees().values());

            setupMenuBar();
            tabbedPane = new JTabbedPane();
            
            // Add Self-Service for all
            tabbedPane.addTab("My Details & Salary", new EmployeePanel(username));

            // Role-Based Tabs using the Employee object
            if (securityService.canAccessAdmin(loggedInUser)) {
                tabbedPane.addTab("Admin Panel", new AdminPanel());
                tabbedPane.addTab("View All Employees", new ViewEmployeesPanel());
            }

            // UPDATED: Supervisor check passes the required data to the constructor
            if (securityService.isSupervisor(loggedInUser, allEmployees)) {
                tabbedPane.addTab("Supervisor Portal", new SupervisorPanel(loggedInUser, allEmployees));
            }

            add(tabbedPane, BorderLayout.CENTER);
            setVisible(true);
        } else {
            System.exit(0);
        }
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu appMenu = new JMenu("Menu");
        JMenuItem signOut = new JMenuItem("Sign Out");
        signOut.addActionListener(e -> { dispose(); SwingUtilities.invokeLater(mainframe::new); });
        appMenu.add(signOut);
        menuBar.add(appMenu);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(mainframe::new);
    }
}
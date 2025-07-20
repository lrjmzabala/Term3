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

    public mainframe() {
        setTitle("MotorPH Payroll System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

       
        Color backgroundColor = new Color(135, 206, 235); // Sky Blue (example)
        
        // Set the background color of the JFrame's content pane
        getContentPane().setBackground(backgroundColor);
        // --- END OF BACKGROUND COLOR ADDITION ---

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
        JMenu appMenu = new JMenu("Menu");

        JMenuItem signOutMenuItem = new JMenuItem("Sign Out");
        signOutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignOut();
            }
        });
        appMenu.add(signOutMenuItem);

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        appMenu.add(exitMenuItem);

        menuBar.add(appMenu);
        setJMenuBar(menuBar);
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
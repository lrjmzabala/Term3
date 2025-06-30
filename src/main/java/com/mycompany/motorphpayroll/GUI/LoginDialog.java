package com.mycompany.motorphpayroll.GUI; // Or a new package like com.mycompany.motorphpayroll.Login

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    private boolean loggedIn = false;
    private String userRole = null; // "Admin" or "Employee"

    public LoginDialog(JFrame parent) {
        super(parent, "Login to MotorPH Payroll", true); // true makes it modal
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Close only the dialog, not the whole app

        setLayout(new BorderLayout(10, 10)); // Padding for better layout

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // 3 rows, 2 columns, with spacing
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Padding around the panel

        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        inputPanel.add(usernameField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        inputPanel.add(passwordField);

        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED); // To show error messages
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the message
        inputPanel.add(messageLabel); // Add message label spanning two columns
        // Set messageLabel to span 2 columns by setting gridbagconstraints if using GridBagLayout
        // For GridLayout, it will just take the next available cell.

        add(inputPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginButton = new JButton("Login");
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listener ---
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        // Allow pressing Enter key to login from password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        // Set focus to username field when dialog appears
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                usernameField.requestFocusInWindow();
            }
        });
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword()); // Get password as String

        // --- Simple Hardcoded Authentication (REPLACE WITH REAL LOGIC) ---
        if (username.equals("admin") && password.equals("adminpass")) {
            loggedIn = true;
            userRole = "Admin";
            dispose(); // Close the dialog
        } else if (username.equals("employee") && password.equals("emppass")) {
            loggedIn = true;
            userRole = "Employee";
            dispose(); // Close the dialog
        } else {
            messageLabel.setText("Invalid username or password.");
            passwordField.setText(""); // Clear password field on failure
        }
        // --- End of Simple Authentication ---

        // In a real application, you'd check against a database or CSV for credentials.
        // Example: if (AuthenticationService.authenticate(username, password)) { ... }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUserRole() {
        return userRole;
    }

    // You might want to clear fields if the dialog is reopened
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText("");
        loggedIn = false;
        userRole = null;
    }
}
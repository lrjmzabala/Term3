package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.User;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional; 

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    private boolean loggedIn = false;
    private String userRole = null;
    private String loggedInUsername = null; 


    public LoginDialog(JFrame parent) {
        super(parent, "Login to MotorPH Payroll", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        inputPanel.add(usernameField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        inputPanel.add(passwordField);

        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(messageLabel);

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginButton = new JButton("Login");
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                usernameField.requestFocusInWindow();
                // Ensure data is loaded when the login dialog opens,
                // in case it's reopened after sign-out.
                // This call is idempotent, so it's safe to call multiple times.
                CSVReaderUtil.loadAllDataToCache();
            }
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // If the user closes the login dialog, and they are not logged in, exit the application.
                if (!loggedIn) {
                    System.exit(0);
                }
            }
        });

        
    }

    

    private void attemptLogin() {
        String enteredUsername = usernameField.getText().trim();
        String enteredPassword = new String(passwordField.getPassword());

        messageLabel.setText("");

        // FIX: Wrap the result in Optional.ofNullable()
        // This assumes CSVReaderUtil.getUserByUsername(username) returns a User object or null
        Optional<User> userOptional = Optional.ofNullable(CSVReaderUtil.getUserByUsername(enteredUsername));

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(enteredPassword)) {
                loggedIn = true;
                loggedInUsername = enteredUsername;
                userRole = user.getRole();
                dispose();
            } else {
                messageLabel.setText("Invalid password.");
                passwordField.setText("");
            }
        } else {
            messageLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUserRole() {
        return userRole;
    }

    // Changed to return the stored loggedInUsername
    public String getUsername() {
        return loggedInUsername;
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText("");
        loggedIn = false;
        userRole = null;
        loggedInUsername = null;
    }
}
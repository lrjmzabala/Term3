package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.User; // Import the User class
import com.mycompany.motorphpayroll.util.CSVReaderUtil; // Import the CSVReaderUtil
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException; // Required for handling file reading errors
import java.util.Map; // Required for Map to store users

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    private boolean loggedIn = false;
    private String userRole = null; // "Admin" or "Employee"

    // ⭐ NEW: Map to store user credentials loaded from CSV
    private Map<String, User> userCredentials;

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

        // ⭐ NEW: Load user credentials when the dialog is initialized
        loadUserCredentials();
    }

    /**
     * ⭐ NEW METHOD: Loads user credentials from the Login Credentials.csv file.
     */
    private void loadUserCredentials() {
        try {
            userCredentials = CSVReaderUtil.readUsersFromLoginCSV();
            // Optional: For debugging, print how many users were loaded
            System.out.println("Loaded " + userCredentials.size() + " user credentials from CSV.");
        } catch (IOException e) {
            // Handle the error if the CSV file cannot be read
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading login credentials. Please ensure 'Login Credentials.csv' is in 'src/main/resources' and formatted correctly.",
                "Login Data Error",
                JOptionPane.ERROR_MESSAGE);
            // Optionally disable login button or exit if critical
            loginButton.setEnabled(false);
            messageLabel.setText("No login data available.");
        }
    }


    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        messageLabel.setText(""); // Clear previous messages

        // ⭐ MODIFIED: Check against loaded user credentials
        if (userCredentials == null || userCredentials.isEmpty()) {
            messageLabel.setText("No login data available. Please contact admin.");
            return;
        }

        if (userCredentials.containsKey(username)) {
            User user = userCredentials.get(username);
            if (user.getPassword().equals(password)) {
                loggedIn = true;
                userRole = user.getRole(); // Set the role from the User object
                dispose(); // Close the dialog on successful login
            } else {
                messageLabel.setText("Invalid password.");
                passwordField.setText(""); // Clear password field on failure
            }
        } else {
            messageLabel.setText("Invalid username or password.");
            passwordField.setText(""); // Clear password field on failure
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUserRole() {
        return userRole;
    }

    // ⭐ ADD THIS NEW METHOD ⭐
    public String getUsername() {
        return usernameField.getText();
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
package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.model.User;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import java.io.IOException; // No longer needed as file reading is internal to CSVReaderUtil cache
import java.util.Optional; // ADD THIS IMPORT for Optional
// import java.util.Map; // REMOVE THIS IMPORT unless you use Map elsewhere in this class

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    private boolean loggedIn = false;
    private String userRole = null;
    private String loggedInUsername = null; // Store the successfully logged in username

    // private Map<String, User> userCredentials; // REMOVE THIS FIELD, we'll use CSVReaderUtil directly

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

        // The loadUserCredentials() method is no longer needed here
        // as CSVReaderUtil.loadAllDataToCache() is called in the mainframe
        // and also when the LoginDialog window opens.
        // remove: loadUserCredentials();
    }

    // REMOVE THIS METHOD entirely as it's no longer needed
    /*
    private void loadUserCredentials() {
        try {
            userCredentials = CSVReaderUtil.readUsersFromLoginCSV();
            System.out.println("Loaded " + userCredentials.size() + " user credentials from CSV.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading login credentials. Please ensure 'Login Credentials.csv' is in 'src/main/resources' and formatted correctly.",
                "Login Data Error",
                JOptionPane.ERROR_MESSAGE);
            loginButton.setEnabled(false);
            messageLabel.setText("No login data available.");
        }
    }
    */

    private void attemptLogin() {
        String enteredUsername = usernameField.getText().trim(); // Use a new variable to avoid confusion
        String enteredPassword = new String(passwordField.getPassword()); // Use a new variable

        messageLabel.setText(""); // Clear previous messages

        // Use CSVReaderUtil.getUserByUsername from the cache
        Optional<User> userOptional = CSVReaderUtil.getUserByUsername(enteredUsername);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(enteredPassword)) {
                loggedIn = true;
                loggedInUsername = enteredUsername; // Store the actual logged in username
                userRole = user.getRole();
                dispose(); // Close the login dialog
            } else {
                messageLabel.setText("Invalid password.");
                passwordField.setText(""); // Clear password field for security
            }
        } else {
            messageLabel.setText("Invalid username or password.");
            passwordField.setText(""); // Clear password field for security
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
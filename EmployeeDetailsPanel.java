package com.mycompany.motorphpayroll.GUI;

import javax.swing.*;
import java.awt.*;

public class EmployeeDetailsPanel extends JPanel {

    private JTextArea employeeDetailsArea;

    public EmployeeDetailsPanel() {
        setLayout(new BorderLayout());
        employeeDetailsArea = new JTextArea(10, 30);
        employeeDetailsArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(employeeDetailsArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateEmployeeDetails(String details) {
        employeeDetailsArea.setText(details);
    }
}

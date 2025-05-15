/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphpayroll.GUI;

import com.mycompany.motorphpayroll.Admin;
import javax.swing.*;

/**
 *
 * @author Papa
 */
public class mainframe extends JFrame {
    public mainframe() {
        setTitle("MotorPH Payroll System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Admin",new AdminPanel());
        tabbedPane.addTab("Employee", new EmployeePanel());
        
        add(tabbedPane);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new mainframe().setVisible(true));
    }
    
}
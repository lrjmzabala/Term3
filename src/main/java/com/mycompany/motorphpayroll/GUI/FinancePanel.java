package com.mycompany.motorphpayroll.GUI;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public class FinancePanel extends JPanel {
    public FinancePanel() {
        setLayout(new BorderLayout());
        // Pass 'false' to hide username/password fields
        add(new AdminPanel(false), BorderLayout.CENTER);
    }
}
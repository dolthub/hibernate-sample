package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GuiCommitDialog  extends JDialog {
    private JTextField authorField;
    private JTextField messageField;

    public GuiCommitDialog(JFrame parent, DatabaseInterface db) {
        super(parent, "Commit Changes", true);
        setLayout(new BorderLayout());

        // Create components
        authorField = new JTextField();
        authorField.setPreferredSize(new Dimension(200, 20));
        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(200, 60));

        // Create labels
        JLabel label1 = new JLabel("Author:");
        JLabel label2 = new JLabel("Message:");

        // Create OK and Cancel buttons
        JButton okButton = new JButton("Commit!");
        JButton cancelButton = new JButton("Cancel");

        // Add action listeners to buttons
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle OK button click
                String input1 = authorField.getText();
                String input2 = messageField.getText();

                db.commit(input1, input2);

                dispose(); // Close the dialog
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Cancel button click
                dispose(); // Close the dialog
            }
        });

        // Create panels to organize components
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(label1);
        inputPanel.add(authorField);
        inputPanel.add(label2);
        inputPanel.add(messageField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add panels to the dialog
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
}
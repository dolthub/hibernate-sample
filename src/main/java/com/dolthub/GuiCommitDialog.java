package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiCommitDialog  extends JDialog {
    private JTextField authorField;
    private JTextArea messageArea;

    public GuiCommitDialog(JFrame parent, DatabaseInterface db) {
        super(parent, "Commit Changes", true);
        setLayout(new BorderLayout());

        // Create components
        authorField = new JTextField();
        messageArea = new JTextArea();

        // Create OK and Cancel buttons
        JButton okButton = new JButton("Commit!");
        JButton cancelButton = new JButton("Cancel");

        // Add action listeners to buttons
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle OK button click
                String author = authorField.getText();
                String message = messageArea.getText();

                author = author.trim();
                String mungedAuthor = mungeAuthor(author);
                if (mungedAuthor.equals(author)) {
                    // Should work
                    db.commit(author, message);

                    dispose(); // Close the dialog
                } else {
                    authorField.setText(mungedAuthor);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Create panels to organize components
        JPanel inputPanel = new JPanel(new GridLayout(2,1));
        inputPanel.add(new Author());
        inputPanel.add(new Message());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add panels to the dialog
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    class Author extends JPanel {
        Author() {
            // setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setLayout(new FlowLayout());

            add(new JLabel("Author:"));

            authorField.setPreferredSize(new Dimension(200, 20));

            add(authorField);
        }
    }

    class Message extends JPanel {
        Message() {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            add(new JLabel(("Message:")));

            authorField.setPreferredSize(new Dimension(200, 60));

            add(messageArea);
        }
    }

    private String mungeAuthor(String providedText) {

        String pattern = "^[^<]+ <[^>]+@[^>]+>$";

        // Create a Pattern object
        Pattern regex = Pattern.compile(pattern);

        // Create a Matcher object
        Matcher matcher = regex.matcher(providedText);

        // Check if the input string matches the pattern
        if (matcher.matches()) {
            return providedText;
        }

        System.out.println("Munging your input. Dolt requires an author which looks like 'Name <foo@bar.com>'");
        return providedText + " <name@email.com>";
    }

}
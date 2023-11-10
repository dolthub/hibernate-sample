package com.dolthub;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.*;

public class GuiReseed extends JPanel {

    private final JTextField inputField = new JTextField();

    private final DatabaseInterface db;

    private final GameState state;

    GuiReseed(GameState state, DatabaseInterface db) {
        this.db = db;
        this.state = state;

        setLayout(new GridLayout(2,1));

        JButton resetBoard = new JButton("Reseed Petri Dish");
        add(resetBoard);

        add(new SeedInput(Long.toString(state.getSeed())));

        resetBoard.addActionListener(e -> reseed());
    }

    private void reseed() {
        long newSeed = 0;
        try {
            newSeed = Long.parseLong(inputField.getText());
            db.persistSeed(newSeed);

            state.randomize(newSeed);
            db.checkout(null);
        } catch (Exception ee){
            System.out.println("Invalid Seed: " + ee);
        }
    }

    class SeedInput extends JPanel {
        SeedInput(String seed) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            JLabel label = new JLabel("Seed:");
            add(label);
            inputField.setText(seed);
            inputField.addActionListener( e -> reseed()) ;
            add(inputField);
        }
    }
}

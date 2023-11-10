package com.dolthub;

import javax.swing.*;
import java.awt.*;

public class GuiScoreAndSeed extends JPanel {

    private JLabel scoreValue;


    GuiScoreAndSeed(DatabaseInterface db) {
        setLayout(new FlowLayout());

        JLabel scoreLabel = new JLabel("Score: ");
        this.add(scoreLabel);

        this.scoreValue = new JLabel("0");
        this.add(scoreValue);

        JButton resetBoard = new JButton("Reset Board ");
        resetBoard.addActionListener(e -> {
            db.checkout(null);
        });
        add(resetBoard);


    }

    public void updateScore(long score) {
        this.scoreValue.setText("" + score);
    }


}

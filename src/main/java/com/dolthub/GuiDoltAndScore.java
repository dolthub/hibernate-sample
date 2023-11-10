package com.dolthub;

import javax.swing.*;

public class GuiDoltAndScore extends JPanel {

    private final GuiScoreAndSeed score;

    GuiDoltAndScore(DatabaseInterface db) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(new GuiDoltOperations(db));

        this.score = new GuiScoreAndSeed(db);

        add(this.score);
    }


    public void updateScore(long score) {
        this.score.updateScore(score);
    }




}

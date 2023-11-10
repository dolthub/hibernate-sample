package com.dolthub;

import javax.swing.*;

public class GuiDoltReseed extends JPanel {

    private final GuiReseed score;

    GuiDoltReseed(GameState state, DatabaseInterface db) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(new GuiDoltOperations(db));

        this.score = new GuiReseed(state, db);

        add(this.score);
    }
}

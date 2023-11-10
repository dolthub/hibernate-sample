package com.dolthub;

import javax.swing.*;

public class GuiDoltAndReseedControls extends JPanel {

    private final GuiDoltOperations doltOps;

    private final GuiReseed seedOps;

    GuiDoltAndReseedControls(GameState state, DatabaseInterface db) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.doltOps = new GuiDoltOperations(db);
        add(this.doltOps);

        this.seedOps = new GuiReseed(state, db);
        add(this.seedOps);
    }

    public void refreshUncommitedMessage() {
        doltOps.refreshUncommitedMessage();
    }

}

package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;

public class GuiMainWindow {


    private Timer timer;

    private JFrame mainFrame;

    private GuiPetriDish dish;

    private GameState gameState;

    final private DatabaseInterface db;

    public GuiMainWindow(GameState gameState, DatabaseInterface db) {
        this.db = db;
        this.gameState = gameState;
        this.timer = new Timer();

        this.mainFrame = new JFrame("Dolt Life");
        this.mainFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.mainFrame.setSize(500, 720);
        this.mainFrame.setResizable(false);

        newGameState(gameState);
    }

     public void newGameState(GameState state) {
        if (this.dish != null) {
            mainFrame.getContentPane().removeAll();
        }

        this.gameState = state;

        this.dish = new GuiPetriDish();
        this.dish.renderWith(gameState);
        this.mainFrame.add(dish);

        GuiControlPanel ctl = new GuiControlPanel(gameState.getSpecies(), db);
        ctl.setActionListener((ActionEvent ae) -> {
            if (ae.getActionCommand().equals("Tick")) {
                this.renderTicks(1);
            } else if (ae.getActionCommand().equals("100 Ticks")) {
                this.renderTicks(100);
            } else if (ae.getActionCommand().equals("Run")) {
                ctl.setRunning(true);
                timer.scheduleAtFixedRate(new InternalTimerTask(), 0, 20);
            } else if (ae.getActionCommand().equals("Stop")) {
                ctl.setRunning(false);
                timer.cancel();
                this.renderTicks(1);
                timer = new Timer();
            }
        });
        mainFrame.add(ctl);

        mainFrame.add(new GuiDoltOperations(db));

        mainFrame.setVisible(true);
        mainFrame.repaint();
        mainFrame.revalidate();
    }

    private void renderTicks(int tickCount) {
        for (int tick = 0; tick < tickCount; tick++) {
            gameState.tick();
        }

        dish.renderWith(gameState);
        mainFrame.getContentPane().repaint();
    }

    class InternalTimerTask extends TimerTask {
        @Override
        public void run() {
            renderTicks(1);
        }
    }
}

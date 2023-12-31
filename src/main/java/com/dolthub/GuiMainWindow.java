package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Primary GUI class which controls all user interactions with data. I do not know how to
 * write UIs, so don't judge me. -- neil@dolthb.com
 */
public class GuiMainWindow {

    private Timer timer;

    private JFrame mainFrame;

    private GuiPetriDish dish;

    private GuiDoltAndReseedControls branchSelect;

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

         this.branchSelect = new GuiDoltAndReseedControls(gameState, db);

         GuiControlPanel ctl = new GuiControlPanel(gameState.getSpecies(), db);

         ctl.setActionListener((ActionEvent ae) -> {
            if (ae.getActionCommand().equals("Tick")) {
                this.renderTicks(1);
                gameState.persist();
                branchSelect.refreshUncommitedMessage();
            } else if (ae.getActionCommand().equals("100 Ticks")) {
                this.renderTicks(100);
                gameState.persist();
                branchSelect.refreshUncommitedMessage();
            } else if (ae.getActionCommand().equals("Run")) {
                ctl.setRunning(true);
                timer.scheduleAtFixedRate(new InternalTimerTask(), 0, 20);
            } else if (ae.getActionCommand().equals("Stop")) {
                ctl.setRunning(false);
                timer.cancel();
                this.renderTicks(1);
                timer = new Timer();
                gameState.persist();
                branchSelect.refreshUncommitedMessage();
            }

        });
        mainFrame.add(ctl);


        mainFrame.add(branchSelect);

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

    public void refreshUncommitedMessage() {
        branchSelect.refreshUncommitedMessage();
    }
}

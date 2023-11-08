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

    public GuiMainWindow(GameState gameState, Persister persister) {
        this.gameState = gameState;
        this.timer = new Timer();

        this.mainFrame = new JFrame("Dolt Life");
        this.mainFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.mainFrame.setSize(500,620);
        this.mainFrame.setResizable(false);

        this.dish = new GuiPetriDish();
        this.dish.renderWith(gameState);
        this.dish.repaint();
        this.mainFrame.add(dish);

        GuiControlPanel ctl = new GuiControlPanel(gameState.getSpecies(), persister);
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
        mainFrame.setVisible(true);
        mainFrame.repaint();
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

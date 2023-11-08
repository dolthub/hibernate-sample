package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class GuiControlPanel extends JPanel {

    JButton oneTick;

    JButton centTick;

    JButton runButton;

    Persister persister;

    public GuiControlPanel(List<Species> speciesList, Persister persister){
        this.persister = persister;

        this.setPreferredSize(new Dimension(500,100));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.add(new GuiSpeciesConfig(speciesList, persister));

        Insets insets = new Insets(1, 5, 1, 5);
        JPanel runButtons = new JPanel();
        runButtons.setLayout(new BoxLayout(runButtons, BoxLayout.Y_AXIS));
        runButton = new JButton("Run");
        runButton.setPreferredSize(new Dimension(100,20));
        runButton.setMargin(insets);
        runButtons.add(runButton);

        centTick = new JButton("100 Ticks");
        centTick.setPreferredSize(new Dimension(100,20));
        centTick.setMargin(insets);
        runButtons.add(centTick);

        oneTick = new JButton("Tick");
        oneTick.setPreferredSize(new Dimension(100,20));
        oneTick.setMargin(insets);
        runButtons.add(oneTick);
        this.add(runButtons);
    }

    public void setActionListener(ActionListener listener) {
        oneTick.addActionListener(listener);
        centTick.addActionListener(listener);
        runButton.addActionListener(listener);
    }

    public void setRunning(boolean running) {
        if (running) {
            runButton.setText("Stop");
        } else {
            runButton.setText("Run");
        }
    }
}

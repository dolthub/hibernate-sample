package com.dolthub;

import javax.swing.*;
import java.awt.*;

public class GuiCell extends JPanel {

    private boolean isLiving;
    private Color color;

    private double health;

    public GuiCell() {
        super();
        this.setPreferredSize(new Dimension(10, 10));
    }


    public void setState(boolean alive, Color color, double health) {
        this.isLiving = alive;
        this.color = color;
        this.health = health;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.isLiving) {
            g.setColor(this.calcColor());
            g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
            g.setColor(Color.BLACK);
        } else {
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    private Color calcColor() {
        if (this.health > 0.5) {
            return this.color;
        }

        if (this.health > 0.25) {
            return this.color.darker();
        }
        if (this.health > 0.12) {
            return this.color.darker().darker();
        }

        return this.color.darker().darker().darker();

    }

}

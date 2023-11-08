package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GuiPetriDish  extends JPanel {

    private Map<Point, GuiCell> cells = new HashMap<>();
    public GuiPetriDish() {
        super(new GridLayout(GameState.HEIGHT, GameState.WIDTH, 2, 2));

        for (int x = 0; x < GameState.HEIGHT; x++) {
            for (int y = 0; y < GameState.WIDTH; y++) {
                GuiCell cell = new GuiCell();
                this.add(cell);
                cells.put(new Point(x,y), cell);
            }
        }
    }

    public void renderWith(GameState state) {
        for (int x = 0; x < GameState.HEIGHT; x++) {
            for (int y = 0; y < GameState.WIDTH; y++) {
                Point pnt = new Point(x,y);
                GuiCell cell = cells.get(pnt);

                Organism org = state.getCell(new Point(x,y));
                if (org == null) {
                    cell.setState(false,Color.BLACK, 1.0);
                } else {
                    cell.setState(true, org.getSpecies().getColor(), org.getStrength());
                }
            }
        }
    }
}

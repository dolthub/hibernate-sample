package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GuiPetriDish  extends JPanel {

    private Map<DaoPetriDishPrimaryKey, GuiCell> cells = new HashMap<>();
    public GuiPetriDish() {
        super(new GridLayout(GameState.HEIGHT, GameState.WIDTH, 2, 2));

        for (int x = 0; x < GameState.HEIGHT; x++) {
            for (int y = 0; y < GameState.WIDTH; y++) {
                GuiCell cell = new GuiCell();
                this.add(cell);
                cells.put(new DaoPetriDishPrimaryKey(x,y), cell);
            }
        }
    }

    public void renderWith(GameState state) {
        for (int x = 0; x < GameState.HEIGHT; x++) {
            for (int y = 0; y < GameState.WIDTH; y++) {
                DaoPetriDishPrimaryKey key = new DaoPetriDishPrimaryKey(x,y);
                GuiCell guiCell = cells.get(key);

                DaoPetriDishCell org = state.getCell(new DaoPetriDishPrimaryKey(x,y));
                if (org == null) {
                    guiCell.setState(false,Color.BLACK, 1.0);
                } else {
                    guiCell.setState(true, org.getSpecies().getColor(), org.getStrength());
                }
            }
        }
    }
}

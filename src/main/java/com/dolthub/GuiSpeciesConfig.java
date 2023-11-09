package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GuiSpeciesConfig extends JPanel {

    private final List<Species> species;

    private Species selected;

    private DatabaseInterface persister;

    class LeftSelector extends JPanel {
        LeftSelector() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(100,80));

            JComboBox<Species> selector = new JComboBox<Species>(species.toArray(new Species[0]));
            selector.setPreferredSize(new Dimension(100,20));
            selector.setRenderer(new ColorCellRenderer());
            add(selector);
            selector.addActionListener(e -> {
                int selectIdx = selector.getSelectedIndex();

                Species newSelected = species.get(selectIdx);
                if (newSelected != selected) {
                    selected = newSelected;
                    GuiSpeciesConfig.this.update();
                }
            });

            add(Box.createHorizontalStrut(75));

            add(new JLabel("Aging Factor"));
            JTextField decayText = new JTextField(4);
            decayText.setText("" + selected.getTickHealthImpact());
            add(decayText);

            decayText.addActionListener(e -> {
                String newText = decayText.getText();
                System.out.println(newText);

                double newVal = selected.getTickHealthImpact();

                try {
                    newVal = Double.parseDouble(newText);
                } catch (Exception ee) {
                    // Ignore.
                }

                if (newVal != selected.getTickHealthImpact()) {
                    selected.setTickHealthImpact(newVal);
                    persister.speciesUpdated(selected);
                }
            });
        }

        class ColorCellRenderer extends JLabel implements ListCellRenderer<Species> {
            @Override
            public void setBackground(Color bg) {
                return; // no op.
            }
            @Override
            public Component getListCellRendererComponent(JList<? extends Species> list, Species value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                if (index == -1) {
                    value = selected;
                }

                setOpaque(true);
                if (value != null) {
                    super.setBackground(value.getColor());
                }

                setText(" ");
                setPreferredSize(new Dimension(20, 15));

                return this;
            }
        }
    }

    class RightDetails extends JPanel {
        RightDetails() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            setPreferredSize(new Dimension(100,80));

            JLabel header = new JLabel();
            header.setText("Damage Dealt:");
            add(header);
            add(Box.createHorizontalStrut(100));

            add(new DamageGrid());

        }
    }

    class SingleDamage extends JPanel {
        SingleDamage(Species victim) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            GuiCell cell = new GuiCell();
            cell.setState(true, victim.getColor(), 1.0);
            add(cell);
            JTextField dmg = new JTextField("" + selected.getDamage(victim));
            add(dmg);
            // add(Box.createHorizontalStrut(100));

            dmg.addActionListener(e -> {
                // TODO - handle bad input
                double newDmg = Double.parseDouble(dmg.getText());
                selected.setDamage(victim, newDmg);
                persister.speciesUpdated(selected);
            });
        }
    }

    class DamageGrid extends JPanel {

        DamageGrid() {
            setLayout(new GridLayout(2,3));
            for (Species s : species) {
                if (s != selected) {
                    add(new SingleDamage(s));
                }
            }
        }
    }



    public GuiSpeciesConfig(List<Species> speciesList, DatabaseInterface persister) {
        this.persister = persister;
        this.species = speciesList;
        // TODO Handle empty list;
        this.selected = speciesList.get(0);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setVisible(true);
        this.update();
    }

    private void update() {
        this.removeAll();

        this.add(new LeftSelector());
        this.add(new RightDetails());

        this.repaint();
        this.revalidate();
    }
}

package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

public class GuiSpeciesConfig extends JPanel {

    private final List<DaoSpecies> species;

    private DaoSpecies selected;

    private DatabaseInterface persister;

    private JTextField decayText;

    class LeftSelector extends JPanel {
        LeftSelector() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(100,80));

            JComboBox<DaoSpecies> selector = new JComboBox<DaoSpecies>(species.toArray(new DaoSpecies[0]));
            selector.setPreferredSize(new Dimension(100,20));
            selector.setRenderer(new ColorCellRenderer());
            add(selector);
            selector.addActionListener(e -> {
                int selectIdx = selector.getSelectedIndex();

                DaoSpecies newSelected = species.get(selectIdx);
                if (newSelected != selected) {
                    selected = newSelected;
                    GuiSpeciesConfig.this.update();
                }
            });

            add(Box.createHorizontalStrut(75));

            add(new JLabel("Aging Factor"));
            decayText = new JTextField(4);
            decayText.setText("" + selected.getTickHealthImpact());
            add(decayText);

            decayText.addActionListener(e -> updateAging() );
            decayText.addFocusListener(new DecayFocus());
        }

        class DecayFocus implements FocusListener {
            @Override
            public void focusLost(FocusEvent e) {
                updateAging();
            }

            @Override
            public void focusGained(FocusEvent e) { }
        }

        private void updateAging() {
            String newText = decayText.getText();
            double newVal = safeDoubleParse(newText, selected.getTickHealthImpact());
            if (newVal != selected.getTickHealthImpact()) {
                selected.setTickHealthImpact(newVal);
                persister.speciesUpdated(selected);
            }
        }


        class ColorCellRenderer extends JLabel implements ListCellRenderer<DaoSpecies> {
            @Override
            public void setBackground(Color bg) {
                return; // no op.
            }
            @Override
            public Component getListCellRendererComponent(JList<? extends DaoSpecies> list, DaoSpecies value, int index,
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
        DaoSpecies victim;

        JTextField dmg;

        SingleDamage(DaoSpecies victim) {
            this.victim = victim;
            setLayout(new FlowLayout(FlowLayout.LEFT));
            GuiCell cell = new GuiCell();
            cell.setState(true, victim.getColor(), 1.0);
            add(cell);
            dmg = new JTextField("" + selected.getDamage(victim));
            add(dmg);
            // add(Box.createHorizontalStrut(100));

            dmg.addActionListener(e -> updateDamage());
            dmg.addFocusListener(new DamageFocus());
        }

        class DamageFocus implements FocusListener {
            @Override
            public void focusLost(FocusEvent e) {
                updateDamage();
            }

            @Override
            public void focusGained(FocusEvent e) { }
        }

        private void updateDamage() {
            // TODO - handle bad input
            double newDmg = safeDoubleParse(dmg.getText(), selected.getDamage(victim));
            if (newDmg > 0.1 || newDmg < 0.0 ) {
                System.err.println("Damage must be between 0.0 and 0.1 (inclusive)");
            } else if (newDmg != selected.getDamage(victim)) {
                selected.setDamage(victim, newDmg);
                persister.speciesUpdated(selected);
            }
        }
    }

    class DamageGrid extends JPanel {

        DamageGrid() {
            setLayout(new GridLayout(2,3));
            for (DaoSpecies s : species) {
                if (s != selected) {
                    add(new SingleDamage(s));
                }
            }
        }
    }

    public GuiSpeciesConfig(List<DaoSpecies> speciesList, DatabaseInterface persister) {
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

    private double safeDoubleParse(String input, double defaultVal) {
        try {
            defaultVal = Double.parseDouble(input);
        } catch (Exception e) {
            // ignore;
        }
        return defaultVal;
    }
}

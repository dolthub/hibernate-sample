package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiDoltOperations extends JPanel {

    private final DatabaseInterface db;

    GuiDoltOperations(DatabaseInterface db) {
        this.db = db;

        this.setLayout(new FlowLayout());

        this.add(new BranchChooser());

        /*
        boolean dirty = db.dirtyWorkspace();
        JLabel dirtyLabel = new JLabel("Dirty: "+dirty);
        this.add(dirtyLabel);

         */
    }


    class BranchChooser extends JPanel {
        BranchChooser() {
            setLayout(new FlowLayout());

            add(new JLabel("Current Branch: "));

            JComboBox<String> branchSelect = new JComboBox<String>(branchNames());
            branchSelect.setSelectedItem(db.activeBranch());

            branchSelect.addActionListener(e -> {
                db.checkout((String) branchSelect.getSelectedItem());
            });

            add(branchSelect);
        }


        private String[] branchNames() {
            List<Branch> allBranches = db.branches();
            List<String> tmp = new ArrayList<>(allBranches.size());
            for( Branch b : allBranches) {
                tmp.add(b.getName());
            }
            return tmp.toArray(new String[0]);
        }

    }

}

package com.dolthub;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiDoltOperations extends JPanel {

    private final DatabaseInterface db;

    GuiDoltOperations(DatabaseInterface db) {
        this.db = db;

        this.setLayout(new GridLayout(2,1));

        this.add(new BranchChooser());

        this.add(new Commiter());

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

    class Commiter extends JPanel {
        Commiter() {
            setLayout(new FlowLayout());

            String currentBranch = db.activeBranch();

            if (db.dirtyWorkspace()) {
                JLabel msg = new JLabel("Current branch has uncommited changes.");
                add(msg);
                JButton commit = new JButton("Commit!");

                commit.addActionListener(e -> {
                    db.commit("asdfasdf");

                    removeAll();
                    revalidate();
                });

                add(commit);

            } else {
                JLabel msg = new JLabel(currentBranch + " has no uncommitted changes.");
                add(msg);
            }
        }
    }


}

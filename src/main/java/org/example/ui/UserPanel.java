package org.example.ui;

import org.example.User;
import org.example.UserRepository;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Panel displaying users in a table with Add/Delete controls.
 */
public class UserPanel extends JPanel {
    private final UserRepository repo;
    private final UserTableModel model;
    private final JTable table;

    public UserPanel(UserRepository repo) {
        super(new BorderLayout());
        this.repo = repo;
        this.model = new UserTableModel(repo);
        this.table = new JTable(model);

        // Table setup: sorters, selection
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowSorter(new TableRowSorter<>(model));
        table.setFillsViewportHeight(true);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.NORTH);
    }

    private JComponent buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton add = new JButton("Add");
        JButton del = new JButton("Delete");

        add.addActionListener(e -> onAdd());
        del.addActionListener(e -> onDelete());

        bar.add(add);
        bar.add(del);
        return bar;
    }

    private void onAdd() {
        String name = JOptionPane.showInputDialog(this, "Enter name:", "Add User", JOptionPane.QUESTION_MESSAGE);
        if (name == null) return; // cancelled
        name = name.trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ageStr = JOptionPane.showInputDialog(this, "Enter age (>= 0):", "Add User", JOptionPane.QUESTION_MESSAGE);
        if (ageStr == null) return; // cancelled
        ageStr = ageStr.trim();
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be an integer.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (age < 0) {
            JOptionPane.showMessageDialog(this, "Age must be >= 0.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        repo.create(new User(name, age));
        model.refresh();
    }

    private void onDelete() {
        int[] viewRows = table.getSelectedRows();
        if (viewRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select one or more rows to delete.", "Delete", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected user(s)?", "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) return;

        // Delete in reverse model order to avoid any oddities
        for (int i = viewRows.length - 1; i >= 0; i--) {
            int modelRow = table.convertRowIndexToModel(viewRows[i]);
            long id = model.getIdAt(modelRow);
            repo.delete(id);
        }
        model.refresh();
    }
}


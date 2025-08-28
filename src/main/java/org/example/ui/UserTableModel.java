package org.example.ui;

import org.example.User;
import org.example.UserRepository;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TableModel showing Users as rows with three columns: ID, Name, Age.
 */
public class UserTableModel extends AbstractTableModel {
    private static final String[] COLS = {"ID", "Name", "Age"};

    private final UserRepository repo;
    private List<UserRepository.Entry> rows = new ArrayList<>();

    public UserTableModel(UserRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo");
        refresh();
    }

    public void refresh() {
        this.rows = new ArrayList<>(repo.entries());
        fireTableDataChanged();
    }

    public long getIdAt(int modelRow) {
        return rows.get(modelRow).getId();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Long.class;
            case 1: return String.class;
            case 2: return Integer.class;
            default: return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        UserRepository.Entry e = rows.get(rowIndex);
        User u = e.getUser();
        switch (columnIndex) {
            case 0: return e.getId();
            case 1: return u.getName();
            case 2: return u.getAge();
            default: return null;
        }
    }
}


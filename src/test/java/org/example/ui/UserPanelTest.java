package org.example.ui;

import org.example.InMemoryUserRepository;
import org.example.User;
import org.example.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class UserPanelTest {
    
    private UserRepository repo;
    private UserPanel panel;
    
    @BeforeEach
    void setUp() {
        repo = new InMemoryUserRepository();
        panel = new UserPanel(repo);
    }
    
    @Test
    void constructorInitializesComponents() {
        assertNotNull(panel);
        assertTrue(panel instanceof JPanel);
        assertEquals(BorderLayout.class, panel.getLayout().getClass());
    }
    
    @Test
    void constructorWithNullRepositoryThrows() {
        assertThrows(NullPointerException.class, () -> new UserPanel(null));
    }
    
    @Test
    void panelContainsScrollPaneAndToolbar() {
        Component[] components = panel.getComponents();
        assertEquals(2, components.length);
        
        // Should have a toolbar (JPanel) and a JScrollPane
        boolean hasScrollPane = false;
        boolean hasToolbar = false;
        
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                hasScrollPane = true;
            } else if (comp instanceof JPanel) {
                hasToolbar = true;
            }
        }
        
        assertTrue(hasScrollPane, "Panel should contain a JScrollPane");
        assertTrue(hasToolbar, "Panel should contain a toolbar");
    }
    
    @Test
    void toolbarContainsAddAndDeleteButtons() {
        Component[] components = panel.getComponents();
        JPanel toolbar = null;
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != panel) {
                toolbar = (JPanel) comp;
                break;
            }
        }
        
        assertNotNull(toolbar, "Toolbar should exist");
        
        Component[] toolbarComponents = toolbar.getComponents();
        assertTrue(toolbarComponents.length >= 2, "Toolbar should have at least 2 buttons");
        
        boolean hasAddButton = false;
        boolean hasDeleteButton = false;
        
        for (Component comp : toolbarComponents) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String text = button.getText();
                if ("Add".equals(text)) {
                    hasAddButton = true;
                } else if ("Delete".equals(text)) {
                    hasDeleteButton = true;
                }
            }
        }
        
        assertTrue(hasAddButton, "Toolbar should have an Add button");
        assertTrue(hasDeleteButton, "Toolbar should have a Delete button");
    }
    
    @Test
    void tableIsConfiguredCorrectly() {
        Component[] components = panel.getComponents();
        JScrollPane scrollPane = null;
        
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        
        assertNotNull(scrollPane, "ScrollPane should exist");
        
        Component viewport = scrollPane.getViewport().getView();
        assertTrue(viewport instanceof JTable, "ScrollPane should contain a JTable");
        
        JTable table = (JTable) viewport;
        assertNotNull(table.getRowSorter(), "Table should have a row sorter");
        assertEquals(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, 
                    table.getSelectionModel().getSelectionMode(),
                    "Table should allow multiple selection");
        assertTrue(table.getFillsViewportHeight(), "Table should fill viewport height");
    }
    
    @Test
    void tableModelIsConnectedToRepository() {
        // Add some users to the repository
        repo.create(new User("Alice", 30));
        repo.create(new User("Bob", 25));
        
        // Create a new panel to see the data
        UserPanel newPanel = new UserPanel(repo);
        
        Component[] components = newPanel.getComponents();
        JScrollPane scrollPane = null;
        
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        
        JTable table = (JTable) scrollPane.getViewport().getView();
        assertEquals(2, table.getRowCount(), "Table should show 2 users");
        assertEquals(3, table.getColumnCount(), "Table should have 3 columns (ID, Name, Age)");
        
        // Check column names
        assertEquals("ID", table.getColumnName(0));
        assertEquals("Name", table.getColumnName(1));
        assertEquals("Age", table.getColumnName(2));
    }
    
    @Test
    void tableModelReflectsRepositoryChanges() {
        Component[] components = panel.getComponents();
        JScrollPane scrollPane = null;
        
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        
        JTable table = (JTable) scrollPane.getViewport().getView();
        UserTableModel model = (UserTableModel) table.getModel();
        
        // Initially no users
        assertEquals(0, table.getRowCount());
        
        // Add a user through repository
        repo.create(new User("Charlie", 35));
        model.refresh(); // Simulate what happens in the real panel
        
        assertEquals(1, table.getRowCount(), "Table should show 1 user after adding");
        
        // Verify the data in the table
        assertEquals("Charlie", table.getValueAt(0, 1));
        assertEquals(35, table.getValueAt(0, 2));
    }
    
    @Test
    void tableHasCorrectColumnTypes() {
        Component[] components = panel.getComponents();
        JScrollPane scrollPane = null;
        
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        
        JTable table = (JTable) scrollPane.getViewport().getView();
        
        assertEquals(Long.class, table.getColumnClass(0), "ID column should be Long");
        assertEquals(String.class, table.getColumnClass(1), "Name column should be String");
        assertEquals(Integer.class, table.getColumnClass(2), "Age column should be Integer");
    }
    
    @Test
    void panelIntegrationWithEmptyRepository() {
        // Test with empty repository
        UserRepository emptyRepo = new InMemoryUserRepository();
        UserPanel emptyPanel = new UserPanel(emptyRepo);
        
        Component[] components = emptyPanel.getComponents();
        JScrollPane scrollPane = null;
        
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        
        JTable table = (JTable) scrollPane.getViewport().getView();
        assertEquals(0, table.getRowCount(), "Empty repository should result in empty table");
    }
    
    @Test
    void panelIntegrationWithPopulatedRepository() {
        // Test with pre-populated repository
        UserRepository populatedRepo = new InMemoryUserRepository();
        populatedRepo.create(new User("Alice", 30));
        populatedRepo.create(new User("Bob", 25));
        populatedRepo.create(new User("Charlie", 35));
        
        UserPanel populatedPanel = new UserPanel(populatedRepo);
        
        Component[] components = populatedPanel.getComponents();
        JScrollPane scrollPane = null;
        
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        
        JTable table = (JTable) scrollPane.getViewport().getView();
        assertEquals(3, table.getRowCount(), "Populated repository should result in table with 3 rows");
        
        // Verify that we can get data from the table
        assertNotNull(table.getValueAt(0, 0)); // ID should not be null
        assertNotNull(table.getValueAt(0, 1)); // Name should not be null
        assertNotNull(table.getValueAt(0, 2)); // Age should not be null
    }
}
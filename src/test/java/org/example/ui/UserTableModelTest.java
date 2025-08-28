package org.example.ui;

import org.example.InMemoryUserRepository;
import org.example.User;
import org.example.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTableModelTest {
    
    private UserRepository repo;
    private UserTableModel model;
    
    @BeforeEach
    void setUp() {
        repo = new InMemoryUserRepository();
        model = new UserTableModel(repo);
    }
    
    @Test
    void constructorWithNullRepositoryThrows() {
        assertThrows(NullPointerException.class, () -> new UserTableModel(null));
    }
    
    @Test
    void emptyModelHasCorrectStructure() {
        assertEquals(0, model.getRowCount());
        assertEquals(3, model.getColumnCount());
        
        assertEquals("ID", model.getColumnName(0));
        assertEquals("Name", model.getColumnName(1));
        assertEquals("Age", model.getColumnName(2));
        
        assertEquals(Long.class, model.getColumnClass(0));
        assertEquals(String.class, model.getColumnClass(1));
        assertEquals(Integer.class, model.getColumnClass(2));
        assertEquals(Object.class, model.getColumnClass(3)); // default for invalid index
    }
    
    @Test
    void modelReflectsRepositoryData() {
        // Add users to repository
        long id1 = repo.create(new User("Alice", 30));
        long id2 = repo.create(new User("Bob", 25));
        
        model.refresh();
        
        assertEquals(2, model.getRowCount());
        
        // Check that data is displayed correctly
        // Note: the order might vary since repository doesn't guarantee order
        boolean foundAlice = false;
        boolean foundBob = false;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String name = (String) model.getValueAt(i, 1);
            Integer age = (Integer) model.getValueAt(i, 2);
            Long id = (Long) model.getValueAt(i, 0);
            
            if ("Alice".equals(name) && age == 30) {
                foundAlice = true;
                assertEquals(id1, id.longValue());
            } else if ("Bob".equals(name) && age == 25) {
                foundBob = true;
                assertEquals(id2, id.longValue());
            }
        }
        
        assertTrue(foundAlice, "Alice should be in the table");
        assertTrue(foundBob, "Bob should be in the table");
    }
    
    @Test
    void getIdAtReturnsCorrectId() {
        long id = repo.create(new User("Test", 20));
        model.refresh();
        
        assertEquals(1, model.getRowCount());
        assertEquals(id, model.getIdAt(0));
    }
    
    @Test
    void getIdAtWithInvalidIndexThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> model.getIdAt(0));
        
        repo.create(new User("Test", 20));
        model.refresh();
        
        assertThrows(IndexOutOfBoundsException.class, () -> model.getIdAt(1));
        assertThrows(IndexOutOfBoundsException.class, () -> model.getIdAt(-1));
    }
    
    @Test
    void getValueAtWithInvalidColumnReturnsNull() {
        repo.create(new User("Test", 20));
        model.refresh();
        
        assertNull(model.getValueAt(0, 3));
        assertNull(model.getValueAt(0, -1));
        assertNull(model.getValueAt(0, 10));
    }
    
    @Test
    void getValueAtWithInvalidRowThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> model.getValueAt(0, 0));
        
        repo.create(new User("Test", 20));
        model.refresh();
        
        assertThrows(IndexOutOfBoundsException.class, () -> model.getValueAt(1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> model.getValueAt(-1, 0));
    }
    
    @Test
    void refreshUpdatesModel() {
        assertEquals(0, model.getRowCount());
        
        // Add user but don't refresh yet
        repo.create(new User("Test", 20));
        assertEquals(0, model.getRowCount()); // Should still be 0
        
        // Refresh and check
        model.refresh();
        assertEquals(1, model.getRowCount());
        
        // Add another user and refresh
        repo.create(new User("Test2", 25));
        model.refresh();
        assertEquals(2, model.getRowCount());
    }
    
    @Test
    void modelHandlesRepositoryDeletion() {
        long id1 = repo.create(new User("Alice", 30));
        long id2 = repo.create(new User("Bob", 25));
        model.refresh();
        
        assertEquals(2, model.getRowCount());
        
        // Delete one user
        repo.delete(id1);
        model.refresh();
        
        assertEquals(1, model.getRowCount());
        
        // Verify remaining user is Bob
        assertEquals("Bob", model.getValueAt(0, 1));
        assertEquals(25, model.getValueAt(0, 2));
        assertEquals(id2, model.getIdAt(0));
    }
    
    @Test
    void modelHandlesRepositoryUpdate() {
        long id = repo.create(new User("Alice", 30));
        model.refresh();
        
        assertEquals(1, model.getRowCount());
        assertEquals("Alice", model.getValueAt(0, 1));
        assertEquals(30, model.getValueAt(0, 2));
        
        // Update the user
        repo.update(id, new User("Alice Smith", 31));
        model.refresh();
        
        assertEquals(1, model.getRowCount());
        assertEquals("Alice Smith", model.getValueAt(0, 1));
        assertEquals(31, model.getValueAt(0, 2));
        assertEquals(id, model.getIdAt(0));
    }
}
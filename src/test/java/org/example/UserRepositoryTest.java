package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private final UserRepository repo = new InMemoryUserRepository();

    @Test
    void createThenRead() {
        long id = repo.create(new User("Alice", 25));
        Optional<User> got = repo.read(id);
        assertTrue(got.isPresent());
        assertEquals(new User("Alice", 25), got.get());
    }

    @Test
    void listUsers() {
        long a = repo.create(new User("A", 1));
        long b = repo.create(new User("B", 2));
        List<User> all = repo.list();
        assertEquals(2, all.size());
        assertTrue(all.contains(new User("A", 1)));
        assertTrue(all.contains(new User("B", 2)));
    }

    @Test
    void updateExisting() {
        long id = repo.create(new User("Old", 10));
        boolean updated = repo.update(id, new User("New", 20));
        assertTrue(updated);
        assertEquals(new User("New", 20), repo.read(id).orElseThrow());
    }

    @Test
    void updateMissingReturnsFalse() {
        assertFalse(repo.update(999, new User("X", 1)));
    }

    @Test
    void deleteExisting() {
        long id = repo.create(new User("Gone", 5));
        assertTrue(repo.delete(id));
        assertTrue(repo.read(id).isEmpty());
    }

    @Test
    void deleteMissingReturnsFalse() {
        assertFalse(repo.delete(12345));
    }

    @Test
    void createNullDisallowed() {
        assertThrows(NullPointerException.class, () -> repo.create(null));
    }

    @Test
    void updateNullDisallowed() {
        long id = repo.create(new User("Ok", 3));
        assertThrows(NullPointerException.class, () -> repo.update(id, null));
    }
}


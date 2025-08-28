package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void createAndAccessFields() {
        User u = new User("Alice", 30);
        assertEquals("Alice", u.getName());
        assertEquals(30, u.getAge());
    }

    @Test
    void negativeAgeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new User("Bob", -1));
    }

    @Test
    void equalityAndHash() {
        User a = new User("X", 10);
        User b = new User("X", 10);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}


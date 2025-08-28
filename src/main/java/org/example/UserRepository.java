package org.example;

import java.util.List;
import java.util.Optional;

/**
 * Repository contract exposing CRUD for User instances.
 * Uses a generated long ID as the primary key.
 */
public interface UserRepository {
    // Simple immutable entry representing a stored user and its id.
    final class Entry {
        private final long id;
        private final User user;
        public Entry(long id, User user) {
            this.id = id;
            this.user = user;
        }
        public long getId() { return id; }
        public User getUser() { return user; }
    }

    /**
     * Create a new user and return its generated ID.
     * @throws NullPointerException if user is null
     */
    long create(User user);

    /**
     * Read a user by ID.
     */
    Optional<User> read(long id);

    /**
     * List all users in unspecified order.
     */
    List<User> list();

    /**
     * Return a snapshot of all entries (id + user) in unspecified order.
     */
    List<Entry> entries();

    /**
     * Replace the user stored at ID with the provided user.
     * @return true if updated, false if id doesn't exist
     * @throws NullPointerException if user is null
     */
    boolean update(long id, User user);

    /**
     * Delete a user by ID.
     * @return true if deleted, false if id doesn't exist
     */
    boolean delete(long id);
}

package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe in-memory implementation of UserRepository.
 */
public class InMemoryUserRepository implements UserRepository {
    private final AtomicLong idSeq = new AtomicLong(0);
    private final Map<Long, User> store = new ConcurrentHashMap<>();

    @Override
    public long create(User user) {
        if (user == null) throw new NullPointerException("user");
        long id = idSeq.incrementAndGet();
        store.put(id, user);
        return id;
    }

    @Override
    public Optional<User> read(long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<User> list() {
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }

    @Override
    public List<UserRepository.Entry> entries() {
        List<UserRepository.Entry> out = new ArrayList<>(store.size());
        for (Map.Entry<Long, User> e : store.entrySet()) {
            out.add(new UserRepository.Entry(e.getKey(), e.getValue()));
        }
        return Collections.unmodifiableList(out);
    }

    @Override
    public boolean update(long id, User user) {
        if (user == null) throw new NullPointerException("user");
        return store.replace(id, user) != null;
    }

    //delete the user by id
    @Override
    public boolean delete(long id) {
        return store.remove(id) != null;
    }

}

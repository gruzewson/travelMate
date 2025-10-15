package org.travelmate.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.travelmate.model.User;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class UserRepository implements Repository<User, UUID> {
    private final Map<UUID, User> users = new HashMap<>();

    private void fillWithSampleData() {
        UUID id = UUID.randomUUID();
        User sample = new User(id, "john_doe", LocalDate.of(1990, 5, 15), null);
        users.put(id, sample);
        id = UUID.randomUUID();
        sample = new User(id, "andrew", LocalDate.of(2000, 6, 16), null);
        users.put(id, sample);
        id = UUID.randomUUID();
        sample = new User(id, "mariusz", LocalDate.of(2010, 7, 17), null);
        users.put(id, sample);
        id = UUID.randomUUID();
        sample = new User(id, "marcin", LocalDate.of(2020, 8, 18), null);
        users.put(id, sample);
    }

    public UserRepository() {
        fillWithSampleData();
    }

    @Override
    public Optional<User> find(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void create(User entity) {
        users.put(entity.getId(), entity);
    }

    @Override
    public void delete(User entity) {
        users.remove(entity.getId());
    }

    @Override
    public void update(User entity) {
        users.put(entity.getId(), entity);
    }
}
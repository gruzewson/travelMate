package org.travelmate.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.travelmate.model.User;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class UserRepository implements Repository<User, UUID> {
    private final Map<UUID, User> users = new HashMap<>();

    public UserRepository() {
        UUID id = UUID.randomUUID();
        User sample = new User(id, "john_doe", "password123", LocalDate.of(1990, 5, 15), null);
        users.put(id, sample);
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
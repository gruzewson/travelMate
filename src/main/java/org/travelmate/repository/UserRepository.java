package org.travelmate.repository;

import org.travelmate.model.User;
import java.time.LocalDate;
import java.util.*;

public class UserRepository {
    private final Map<UUID, User> users = new HashMap<>();

    private void fillWithSampleData() {
        User user1 = new User("john", LocalDate.of(1990, 5, 15));
        users.put(user1.getId(), user1);

        User user2 = new User("andrew", LocalDate.of(2000, 6, 16));
        users.put(user2.getId(), user2);

        User user3 = new User( "mariusz", LocalDate.of(2010, 7, 17));
        users.put(user3.getId(), user3);

        User user4 = new User("marcin", LocalDate.of(2020, 8, 18));
        users.put(user4.getId(), user4);
    }

    public UserRepository() {
        fillWithSampleData();
    }

    public Optional<User> find(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void create(User entity) {
        users.put(entity.getId(), entity);
    }

    public void delete(User entity) {
        users.remove(entity.getId());
    }

    public void update(User entity) {
        users.put(entity.getId(), entity);
    }
}
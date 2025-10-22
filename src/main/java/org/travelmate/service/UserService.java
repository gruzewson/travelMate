package org.travelmate.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.travelmate.model.User;
import org.travelmate.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    public Optional<User> find(UUID id) {
        return userRepository.find(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void create(User entity) {
        userRepository.create(entity);
    }

    public void delete(User entity) {
        userRepository.delete(entity);
    }

    public void update(User entity) {
        userRepository.update(entity);
    }
}
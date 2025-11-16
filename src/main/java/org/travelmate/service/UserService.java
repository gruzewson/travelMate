package org.travelmate.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.travelmate.model.User;
import org.travelmate.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class UserService {

    @Inject
    private UserRepository userRepository;

    public Optional<User> find(UUID id) {
        return userRepository.find(id);
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void create(User entity) {
        userRepository.create(entity);
    }

    public void delete(UUID id) {
        userRepository.delete(id);
    }

    public void update(User entity) {
        userRepository.update(entity);
    }
}
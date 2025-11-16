package org.travelmate.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.travelmate.repository.AvatarRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Optional;

@Stateless
public class AvatarService {

    @Inject
    private AvatarRepository avatarRepository;

    public Optional<Path> getAvatar(String filename) {
        return avatarRepository.findAvatar(filename);
    }

    public void deleteAvatar(String filename) throws IOException {
        avatarRepository.deleteAvatar(filename);
    }

    public void uploadAvatar(String filename, InputStream data) throws IOException {
        avatarRepository.uploadAvatar(filename, data);
    }
}

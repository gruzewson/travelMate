package org.travelmate.service;

import org.travelmate.repository.AvatarRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Optional;

public class AvatarService {

    private final AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

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

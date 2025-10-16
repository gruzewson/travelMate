package org.travelmate.service;

import org.travelmate.repository.AvatarRepository;

import java.io.IOException;
import java.nio.file.*;

public class AvatarService {

    private final AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    public Path getAvatar(String filename) throws IOException {
        return avatarRepository.findAvatar(filename);
    }

    public void deleteAvatar(String filename) throws IOException {
        avatarRepository.deleteAvatar(filename);
    }

    public void uploadAvatar(String filename, java.io.InputStream data) throws IOException {
        avatarRepository.uploadAvatar(filename, data);
    }
}

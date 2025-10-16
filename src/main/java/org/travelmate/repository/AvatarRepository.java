package org.travelmate.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Optional;

public class AvatarRepository {

    private final Path avatarsDir;

    public AvatarRepository(String avatarsDir) {
        this.avatarsDir = Paths.get(avatarsDir);
    }

    public Optional<Path> findAvatar(String filename) {
        Path filePath = avatarsDir.resolve(filename);
        return Files.exists(filePath) ? Optional.of(filePath) : Optional.empty();
    }

    public void deleteAvatar(String filename) throws IOException {
        Path filePath = avatarsDir.resolve(filename);
        Files.deleteIfExists(filePath);
    }

    public void uploadAvatar(String filename, InputStream data) throws IOException {
        Path filePath = avatarsDir.resolve(filename);
        Files.copy(data, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}

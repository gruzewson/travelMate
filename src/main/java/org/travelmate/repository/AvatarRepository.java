package org.travelmate.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class AvatarRepository {

    private final Path baseDir;

    public AvatarRepository(String baseDir) {
        this.baseDir = Paths.get(baseDir);
    }

    public Path findAvatar(String filename) {
        Path filePath = baseDir.resolve(filename);
        return Files.exists(filePath) ? filePath : null;
    }

    public void deleteAvatar(String filename) throws IOException {
        Path filePath = baseDir.resolve(filename);
        Files.deleteIfExists(filePath);
    }

    public void uploadAvatar(String filename, InputStream data) throws IOException {
        Path filePath = baseDir.resolve(filename);
        Files.copy(data, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}

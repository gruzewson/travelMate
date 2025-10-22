package org.travelmate.repository;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Optional;

@ApplicationScoped
public class AvatarRepository {

    @Inject
    private ServletContext context;

    private Path avatarsDir;

    @PostConstruct
    public void initialize() {
        String avatarsDirParam = context.getInitParameter("avatars.dir");
        if (avatarsDirParam == null) {
            throw new IllegalStateException("avatars.dir context parameter is not set in web.xml");
        }

        this.avatarsDir = Paths.get(avatarsDirParam);

        if (Files.notExists(this.avatarsDir)) {
            try {
                Files.createDirectories(this.avatarsDir);
            } catch (IOException e) {
                throw new RuntimeException("Cannot create avatars directory", e);
            }
        }
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

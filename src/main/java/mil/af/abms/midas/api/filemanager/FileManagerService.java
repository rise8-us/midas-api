package mil.af.abms.midas.api.filemanager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.clients.S3Client;

@Slf4j
@Service
public class FileManagerService {
    private final Path root = Paths.get("uploads");

    private final S3Client s3Client;

    public FileManagerService(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    public void init() {
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not init");
        }
    }

    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(Objects.requireNonNull(file.getOriginalFilename())));
        } catch (Exception e) {
            throw new RuntimeException("Could not save file. Error: " + e.getMessage());
        }
    }

    public Stream<Path> loadAllFiles() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root));
        } catch (IOException e) {
            throw new RuntimeException("Load failed");
        }
    }
}

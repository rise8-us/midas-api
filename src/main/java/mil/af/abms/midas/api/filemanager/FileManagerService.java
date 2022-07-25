package mil.af.abms.midas.api.filemanager;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.clients.S3Client;

@Slf4j
@Service
public class FileManagerService {

    private static final String FILE_DIR = "files";

    private final S3Client s3Client;

    public FileManagerService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

//    public void init() {
//        try {
//            Files.createDirectory(root);
//        } catch (IOException e) {
//            throw new RuntimeException("Could not init");
//        }
//    }

    public void save(MultipartFile file) {
        var actualName = String.format("%s/%s.gz", FILE_DIR, file.getName() + "-extra");
        s3Client.sendFileToBucketAsGzip(actualName, file);
    }

//    public Stream<Path> loadAllFiles() {
//        try {
//            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root));
//        } catch (IOException e) {
//            throw new RuntimeException("Load failed");
//        }
//    }
}

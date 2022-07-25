package mil.af.abms.midas.api.filemanager;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.clients.S3Client;
import mil.af.abms.midas.exception.S3IOException;

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

    public void saveFile(MultipartFile file) {
        var actualName = String.format("%s/%s", FILE_DIR, file.getOriginalFilename());
        s3Client.sendFileToBucket(actualName, file);
    }

    public ByteArrayResource getFile(String fileName) throws S3IOException {
        try {
            S3ObjectInputStream s3ObjectStream = s3Client.getFileFromBucket(fileName);
            ByteArrayResource s3ObjectBytes = new ByteArrayResource(IOUtils.toByteArray(s3ObjectStream));
            s3ObjectStream.close();
            return s3ObjectBytes;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new S3IOException("failed to retrieve file from s3");
        }
    }

//    public Stream<Path> loadAllFiles() {
//        try {
//            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root));
//        } catch (IOException e) {
//            throw new RuntimeException("Load failed");
//        }
//    }
}

package mil.af.abms.midas.api.filemanager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final String FILE_DIR = "uploads";

    private final S3Client s3Client;

    public FileManagerService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void saveFile(String portfolioName, String productName, MultipartFile file) {
        String portfolio = portfolioName.replace("/", " ");
        String product = productName.replace("/", " ");
        var actualName = String.format("%s/%s/%s/%s", FILE_DIR, portfolio, product, file.getOriginalFilename());
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

    public List<String> getAllFileNamesByPath(String pathStr) {
        List<String> allFileNames = s3Client.getFileNamesFromBucket();
        List<String> filteredFileNames = allFileNames.stream().filter(path -> path.contains("uploads/" + pathStr)).collect(Collectors.toList());
        return filteredFileNames;
    }
}

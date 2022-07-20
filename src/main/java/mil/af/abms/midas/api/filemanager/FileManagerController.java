package mil.af.abms.midas.api.filemanager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/filemanager")
public class FileManagerController {

    private final FileManagerService service;

    @Autowired
    public FileManagerController(FileManagerService service) {
        this.service = service;
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> getFilesList() throws IOException {
        List<String> fileNames = service.loadAllFiles().map(path -> {
            String fileName = path.getFileName().toString();
            return fileName;
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(fileNames);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> save(@RequestParam("file")MultipartFile file) {
        String message;
        System.out.println("UPLOAD API HIT");
        System.out.println(file);
        try {
            service.save(file);
            message = "success. File: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "fail. File: " + file.getOriginalFilename() + "," + file.getName();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }
}

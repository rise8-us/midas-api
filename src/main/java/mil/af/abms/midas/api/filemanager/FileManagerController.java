package mil.af.abms.midas.api.filemanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import mil.af.abms.midas.api.filemanager.dto.FileManagerDTO;

@RestController
@RequestMapping("/api/filemanager")
public class FileManagerController {

    private final FileManagerService service;

    @Autowired
    public FileManagerController(FileManagerService service) {
        this.service = service;
    }

//    @GetMapping("/files")
//    public ResponseEntity<List<String>> getFilesList() throws IOException {
//        List<String> fileNames = service.loadAllFiles().map(path -> {
//            String fileName = path.getFileName().toString();
//            return fileName;
//        }).collect(Collectors.toList());
//        return ResponseEntity.status(HttpStatus.OK).body(fileNames);
//    }

    @PostMapping("/upload")
    public ResponseEntity<String> save(@RequestParam("file") MultipartFile file) {
        service.saveFile(file);

        return ResponseEntity.status(HttpStatus.OK).body("success. File: " + file.getName());
    }

    @PostMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFromS3(@RequestBody FileManagerDTO dto) {
        ByteArrayResource data = service.getFile(dto.getFileName());

        return ResponseEntity
                .ok()
                .contentLength(data.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-disposition", "attachment; filename=\"" + dto.getFileName() + "\"")
                .body(data);
    }
}

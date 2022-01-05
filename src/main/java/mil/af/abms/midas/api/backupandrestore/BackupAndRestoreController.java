package mil.af.abms.midas.api.backupandrestore;

import java.util.List;
import java.util.Set;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.backupandrestore.dto.BackupRestoreDTO;
import mil.af.abms.midas.config.security.annotations.IsAdmin;

@RestController
@RequestMapping("/api/dbActions")
public class BackupAndRestoreController {

    private final BackupAndRestoreService service;

    public BackupAndRestoreController(BackupAndRestoreService service) {
        this.service = service;
    }

    @IsAdmin
    @GetMapping("tableNames")
    public Set<String> getTableNames() {
        return service.getTableNames();
    }

    @IsAdmin
    @GetMapping("fileNames")
    public List<String> getFileNames() { return service.getBackupFileNames(); }

    @IsAdmin
    @PostMapping("backup")
    public void backupToS3(@RequestBody BackupRestoreDTO dto) { service.backupToS3(dto.getFileName()); }

    @IsAdmin
    @PostMapping("restore")
    public void restoreFromS3(@RequestBody BackupRestoreDTO dto) { service.restore(dto); }

    @IsAdmin
    @PostMapping("download")
    public ResponseEntity<ByteArrayResource> downloadFromS3(@RequestBody BackupRestoreDTO dto) {
        var data = service.getFile(dto.getFileName());

        return ResponseEntity
                .ok()
                .contentLength(data.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-disposition", "attachment; filename=\"" + dto.getFileName() + "\"")
                .body(data);
    }
}

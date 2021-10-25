package mil.af.abms.midas.api.backupandrestore;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.backupandrestore.dto.BackupDTO;
import mil.af.abms.midas.api.backupandrestore.dto.RestoreDTO;
import mil.af.abms.midas.clients.MySQLClient;
import mil.af.abms.midas.config.security.annotations.IsAdmin;

@RestController
@RequestMapping("/api/dbActions")
public class BackupAndRestoreController {

    private final MySQLClient mySQLClient;
    private final BackupAndRestoreService service;

    public BackupAndRestoreController(MySQLClient mySQLClient, BackupAndRestoreService service) {
        this.mySQLClient = mySQLClient;
        this.service = service;
    }

    @IsAdmin
    @GetMapping("tableNames")
    public Set<String> getTableNames() {
        return mySQLClient.getTableNames();
    }

    @IsAdmin
    @GetMapping("backupJSON")
    public BackupDTO getBackupJSON() {
        return new BackupDTO(mySQLClient.exportToSql());
    }

    @IsAdmin
    @GetMapping("backupString")
    public String getBackupString() {
        return mySQLClient.exportToSql();
    }

    @IsAdmin
    @PostMapping("restoreJSON")
    public boolean doRestore(@RequestBody BackupDTO dto) {
        return mySQLClient.restore(dto.getMysqlDump());
    }

    @IsAdmin
    @PostMapping("restoreString")
    public boolean doRestoreString(@RequestBody String mysqldump) {
        return mySQLClient.restore(mysqldump);
    }

    @GetMapping("backup")
    public void backupToS3() { service.backupToS3(); }

    @GetMapping("fileNames")
    public List<String> getFileNames() { return service.getBackupFileNames(); }

    @PostMapping("restore")
    public void restoreFromS3(@RequestBody RestoreDTO dto) { service.restore(dto.getFileName()); }

}

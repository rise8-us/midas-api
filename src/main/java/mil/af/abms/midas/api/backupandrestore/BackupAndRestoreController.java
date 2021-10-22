package mil.af.abms.midas.api.backupandrestore;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.backupandrestore.dto.BackupDTO;
import mil.af.abms.midas.clients.MySQLClient;
import mil.af.abms.midas.clients.S3Client;
import mil.af.abms.midas.config.security.annotations.IsAdmin;

@RestController
@RequestMapping("/api/dbActions")
public class BackupAndRestoreController {

    private final MySQLClient mySQLClient;
    private final S3Client s3Client;

    public BackupAndRestoreController(MySQLClient mySQLClient, S3Client s3Client) {
        this.mySQLClient = mySQLClient;
        this.s3Client = s3Client;
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

    @GetMapping("backupGzip")
    public void backupToS3() { s3Client.compressStringAndSendToBucket("backup/10-22backup.sql.gz", mySQLClient.exportToSql()); }

}

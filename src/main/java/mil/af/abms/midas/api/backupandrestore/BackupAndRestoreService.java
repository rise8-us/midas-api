package mil.af.abms.midas.api.backupandrestore;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.helper.GzipHelper;
import mil.af.abms.midas.clients.MySQLClient;
import mil.af.abms.midas.clients.S3Client;
import mil.af.abms.midas.exception.AbstractRuntimeException;

@Slf4j
@Service
public class BackupAndRestoreService {

    private static final String BACKUP_DIR = "backups";

    private final MySQLClient mySQLClient;
    private final S3Client s3Client;

    public BackupAndRestoreService(MySQLClient mySQLClient, S3Client s3Client) {
        this.mySQLClient = mySQLClient;
        this.s3Client = s3Client;
    }

    public List<String> getBackupFileNames() {
        return s3Client.getFileNamesFromBucket().stream().filter(f -> f.startsWith(BACKUP_DIR)).collect(Collectors.toList());
    }

    public void backupToS3() {
        var dump = mySQLClient.exportToSql();
        var dumpDate = LocalDateTime.now();
        var flywayVersion = mySQLClient.getLatestFlywayVersion();

        var fileName = String.format("backups/%s/%s.sql.gz", flywayVersion, dumpDate);
        s3Client.sendToBucketAsGzip(fileName, dump);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledBackup() {
        this.backupToS3();
    }

    public void restore(String fileName) throws AbstractRuntimeException {
        try (var backupFile = s3Client.getFileFromBucket(fileName)) {
            var backupString = GzipHelper.decompressInputStreamToString(backupFile);
            mySQLClient.restore(backupString);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new AbstractRuntimeException("Unable to restore. See logs for more information.");
        }
    }

}

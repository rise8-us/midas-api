package mil.af.abms.midas.api.backupandrestore;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.MidasApi;
import mil.af.abms.midas.api.backupandrestore.dto.BackupRestoreDTO;
import mil.af.abms.midas.api.helper.GzipHelper;
import mil.af.abms.midas.clients.MySQLClient;
import mil.af.abms.midas.clients.S3Client;
import mil.af.abms.midas.exception.AbstractRuntimeException;
import mil.af.abms.midas.exception.S3IOException;

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

    public Set<String> getTableNames() {
        return mySQLClient.getTableNames();
    }

    public List<String> getBackupFileNames() {
        return s3Client.getFileNamesFromBucket().stream().filter(f -> f.startsWith(BACKUP_DIR)).collect(Collectors.toList());
    }
    
    public ByteArrayResource getFile(String fileName) throws S3IOException {
        try {
            var s3ObjectStream = s3Client.getFileFromBucket(fileName);
            var s3ObjectBytes = new ByteArrayResource(IOUtils.toByteArray(s3ObjectStream));
            s3ObjectStream.close();
            return s3ObjectBytes;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new S3IOException("failed to retrieve file from s3");
        }
    }

    public void backupToS3(String fileName) {
        var dump = mySQLClient.exportToSql();
        var dumpDate = LocalDateTime.now();
        var flywayVersion = mySQLClient.getLatestFlywayVersion();

        var actualName = fileName != null ? String.format("backups/%s/%s.sql.gz", flywayVersion, fileName) : String.format("backups/%s/%s.sql.gz", flywayVersion, dumpDate);
        s3Client.sendToBucketAsGzip(actualName, dump);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledBackup() {
        this.backupToS3(null);
    }

    public void restore(BackupRestoreDTO dto) throws AbstractRuntimeException {
        try (var backupFile = s3Client.getFileFromBucket(dto.getFileName())) {
            var backupString = GzipHelper.decompressInputStreamToString(backupFile);
            backupString = clearTokens(backupString, dto.isClearTokens());
            mySQLClient.restore(backupString);
            restart(dto.isRestart());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new AbstractRuntimeException("Unable to restore. See logs for more information.");
        }
    }

    protected void restart(boolean isRestart) {
        if (isRestart) {
            MidasApi.restart();
        }
    }

    private String clearTokens(String backupString, boolean isClearTokens) {
        return isClearTokens ? String.format("%s%nUPDATE source_control SET token = NULL", backupString) : backupString;
    }


}

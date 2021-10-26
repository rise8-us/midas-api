package mil.af.abms.midas.api.backupandrestore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.GzipHelper;
import mil.af.abms.midas.clients.MySQLClient;
import mil.af.abms.midas.clients.S3Client;

@ExtendWith(SpringExtension.class)
class BackupAndRestoreServiceTests {

    @SpyBean
    BackupAndRestoreService service;

    @MockBean
    MySQLClient mySQLClient;

    @MockBean
    S3Client s3Client;

    @Captor
    ArgumentCaptor<String> stringCaptor;

    private static final String DATA = "backup data";
    private static final String VERSION = "1.0.0";
    private static final String FILE_NAME = "Test File";

    S3Object object = Builder.build(S3Object.class)
            .with(o -> o.setBucketName("test bucket"))
            .with(o -> o.setKey("test key"))
            .with(o -> o.setObjectContent(GzipHelper.compressStringToInputStream("string")))
            .get();

    @Test
    void should_backup_to_s3() {
        when(mySQLClient.exportToSql()).thenReturn(DATA);
        when(mySQLClient.getLatestFlywayVersion()).thenReturn(VERSION);
        doNothing().when(s3Client).sendToBucketAsGzip(anyString(), anyString());

        service.backupToS3();

        verify(s3Client, times(1)).sendToBucketAsGzip(stringCaptor.capture(), stringCaptor.capture());
        var arg1 = stringCaptor.getAllValues().get(0);
        var arg2 = stringCaptor.getAllValues().get(1);

        assertThat(arg1).contains(VERSION);
        assertThat(arg2).isEqualTo(DATA);
    }

    @Test
    void should_get_files_names_from_s3() {
        var files = List.of("backups/foo/1", "backups/bar/2", "imgs/foobar/3");
        when(s3Client.getFileNamesFromBucket()).thenReturn(files);

        assertThat(service.getBackupFileNames()).isEqualTo(files.subList(0, 2));
    }

    @Test
    void should_run_scheduled_backup() {
        doNothing().when(service).backupToS3();

        service.runScheduledBackup();

        verify(service, times(1)).backupToS3();
    }

    @Test
    void should_restore_db_from_file() {
        var compress = GzipHelper.compressStringToInputStream(DATA);
        var s3IS = new S3ObjectInputStream(compress, new HttpGet());

        when(s3Client.getFileFromBucket(FILE_NAME)).thenReturn(s3IS);

        service.restore(FILE_NAME);
        verify(mySQLClient, times(1)).restore(stringCaptor.capture());

        assertThat(stringCaptor.getValue()).isEqualTo(DATA);
    }

    @Test
    void should_get_file() throws Exception {

        var expectedStream = new ByteArrayResource(IOUtils.toByteArray(object.getObjectContent()));
        object.getObjectContent().reset();
        when(s3Client.getFileFromBucket(object.getKey())).thenReturn(object.getObjectContent());

        var stream = service.getFile(object.getKey());

        assertEquals(expectedStream, stream);
    }
}

package mil.af.abms.midas.api.backupandrestore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.backupandrestore.dto.BackupRestoreDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.GzipHelper;
import mil.af.abms.midas.clients.MySQLClient;
import mil.af.abms.midas.clients.S3Client;
import mil.af.abms.midas.exception.AbstractRuntimeException;
import mil.af.abms.midas.exception.S3IOException;

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
    private static final String CLEAR_TOKEN_DATA = DATA + "\nUPDATE source_control SET token = NULL";
    private static final String VERSION = "1.0.0";
    private static final String FILE_NAME = "Test File";

    S3Object object = Builder.build(S3Object.class)
            .with(o -> o.setBucketName("test bucket"))
            .with(o -> o.setKey("test key"))
            .with(o -> o.setObjectContent(GzipHelper.compressStringToInputStream("string")))
            .get();

    @ParameterizedTest
    @CsvSource(value = {"foo.bar", "null" }, nullValues = {"null"})
    void should_backup_to_s3(String fileName) {
        when(mySQLClient.exportToSql()).thenReturn(DATA);
        when(mySQLClient.getLatestFlywayVersion()).thenReturn(VERSION);
        doNothing().when(s3Client).sendStringToBucketAsGzip(anyString(), anyString());

        service.backupToS3(fileName);

        verify(s3Client, times(1)).sendStringToBucketAsGzip(stringCaptor.capture(), stringCaptor.capture());
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
        doNothing().when(service).backupToS3(anyString());

        service.runScheduledBackup();

        verify(service, times(1)).backupToS3(null);
    }

    @Test
    void should_backup_with_given_name() {
        doNothing().when(service).backupToS3(anyString());

        service.backupToS3("foo");

        verify(service, times(1)).backupToS3("foo");
    }

    @ParameterizedTest
    @CsvSource(value = { "false: false", "true: true" }, delimiter = ':')
    void should_restore_db_from_file(boolean restart, boolean clearTokens) throws Exception {
        var compress = GzipHelper.compressStringToInputStream(DATA);
        var s3IS = new S3ObjectInputStream(compress, new HttpGet());
        var dto = new BackupRestoreDTO(FILE_NAME, restart, clearTokens);
        var finalData = clearTokens ? CLEAR_TOKEN_DATA : DATA;

        when(s3Client.getFileFromBucket(FILE_NAME)).thenReturn(s3IS);
        doNothing().when(service).restart(restart);

        service.restore(dto);

        verify(mySQLClient, times(1)).restore(stringCaptor.capture());
        verify(service, times(1)).restart(restart);

        assertThat(stringCaptor.getValue()).isEqualTo(finalData);

    }

    @Test
    void should_throw_io_exception_on_restore_db_from_file() throws Exception {
        when(s3Client.getFileFromBucket(FILE_NAME)).thenThrow(new IOException());
        var dto = new BackupRestoreDTO(FILE_NAME, true, true);

        assertThrows(AbstractRuntimeException.class, () -> service.restore(dto));

    }

    @Test
    void should_get_file() throws Exception {

        var expectedStream = new ByteArrayResource(IOUtils.toByteArray(object.getObjectContent()));
        object.getObjectContent().reset();
        when(s3Client.getFileFromBucket(object.getKey())).thenReturn(object.getObjectContent());

        var stream = service.getFile(object.getKey());

        assertEquals(expectedStream, stream);
    }

    @Test
    void should_throw_when_get_file() throws Exception {
        when(s3Client.getFileFromBucket(object.getKey())).thenThrow(new IOException());

        assertThrows(S3IOException.class, () -> service.getFile(object.getKey()));
    }
}

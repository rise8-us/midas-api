package mil.af.abms.midas.clients;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.GzipHelper;
import mil.af.abms.midas.config.S3Properties;
import mil.af.abms.midas.exception.S3IOException;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(S3Properties.class)
@TestPropertySource(locations = "classpath:s3.properties")
@Import(S3Client.class)
public class S3ClientTests {

    @SpyBean
    S3Client s3Client;

    ListObjectsV2Result result = Builder.build(ListObjectsV2Result.class)
            .with(r -> r.setBucketName("test bucket"))
            .get();
    S3ObjectSummary summary1 = Builder.build(S3ObjectSummary.class)
            .with(s -> s.setBucketName("test bucket"))
            .with(s -> s.setKey("foobar.txt"))
            .get();
    S3ObjectSummary summary2 = Builder.build(S3ObjectSummary.class)
            .with(s -> s.setBucketName("test bucket"))
            .with(s -> s.setKey("foo.txt"))
            .get();
    S3ObjectSummary summary3 = Builder.build(S3ObjectSummary.class)
            .with(s -> s.setBucketName("test bucket"))
            .with(s -> s.setKey("bar.txt"))
            .get();
    S3Object object = Builder.build(S3Object.class)
            .with(o -> o.setBucketName("test bucket"))
            .with(o -> o.setKey("test key"))
            .with(o -> o.setObjectContent(GzipHelper.compressStringToInputStream("string")))
            .get();

    private static final String FILE_NAME = "Test File";
    private static final String DATA = "Test Data";

    @Test
    void should_return_list_of_file_names_from_bucket() {
        result.getObjectSummaries().addAll(List.of(summary1, summary2, summary3));
        var fileNames = List.of("foobar.txt", "foo.txt", "bar.txt");
        doReturn(result).when(s3Client).makeRequest(any());

        assertThat(s3Client.getFileNamesFromBucket()).isEqualTo(fileNames);
    }

    @Test
    void should_send_gzip_to_bucket() {
        doReturn("foo").when(s3Client).makeRequest(any());

        s3Client.sendToBucketAsGzip(FILE_NAME, DATA);

        verify(s3Client, times(1)).makeRequest(any());
    }

    @Test
    void should_fail_send_gzip_to_bucket_with_S3IOException() {
        doThrow(new S3IOException("fail")).when(s3Client).makeRequest(any());

        assertThrows(S3IOException.class, () -> s3Client.sendToBucketAsGzip(FILE_NAME, DATA));
    }

    @Test
    void should_get_file_from_bucket() {
        doReturn(object).when(s3Client).makeRequest(any());
        s3Client.getFileFromBucket(object.getKey());
    }

    @Test
    void should_make_request() {
        assertThat(s3Client.makeRequest(() -> "foo")).isEqualTo("foo");
    }

    @Test
    void should_throw_on_make_request() {
        assertThrows(S3IOException.class, () -> s3Client.makeRequest(() -> { throw new IOException("foo"); }));
    }

}

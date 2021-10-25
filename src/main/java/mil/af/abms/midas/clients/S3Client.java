package mil.af.abms.midas.clients;

import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.helper.GzipHelper;
import mil.af.abms.midas.api.helper.IOHelper;
import mil.af.abms.midas.config.S3Properties;
import mil.af.abms.midas.exception.S3IOException;

@Slf4j
@Component
public class S3Client {

    private final AmazonS3 s3;
    private final String bucketName;
    private final S3Properties property;

    public S3Client(S3Properties property) {
        this.property = property;
        this.bucketName = this.property.getMinioBucketName();
        var credentials = new BasicAWSCredentials(this.property.getMinioAccessKey(), this.property.getMinioSecretKey());
        var client = new AwsClientBuilder.EndpointConfiguration(this.property.getMinioHost(), this.property.getAwsDefaultRegion());

        this.s3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(client)
                .withPathStyleAccessEnabled(true)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public void sendToBucketAsGzip(String fileName, String data) {
        try (var compressedStream = GzipHelper.compressStringToInputStream(data)) {
            var length = IOHelper.getInputStreamSize(compressedStream);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            metadata.addUserMetadata("title", fileName);
            metadata.setContentEncoding("gzip");
            metadata.setContentLength(length);

            var request = new PutObjectRequest(this.bucketName, fileName, compressedStream, metadata);

            makeRequest(() -> s3.putObject(request));
        } catch (IOException | S3IOException e) {
            log.error(e.getMessage());
            throw new S3IOException("Unable to send gzip to bucket.");
        }
    }

    public List<String> getFileNamesFromBucket() {
        var result = (ListObjectsV2Result) makeRequest(() -> s3.listObjectsV2(this.bucketName));
        var objects = result.getObjectSummaries();
        return objects.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }

    public S3ObjectInputStream getFileFromBucket(String fileName) {
        var s3Object = (S3Object) makeRequest(() -> s3.getObject(this.bucketName, fileName));
        return s3Object.getObjectContent();
    }

    @FunctionalInterface
    protected interface S3ClientThunk<T> {
        T call() throws IOException;
    }

    protected Object makeRequest(S3Client.S3ClientThunk<?> request) {
        try {
            return request.call();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new S3IOException("Failed to make request to S3. Contact an admin.");
        }
    }

}

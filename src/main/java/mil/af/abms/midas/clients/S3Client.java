package mil.af.abms.midas.clients;

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
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;

import mil.af.abms.midas.api.helper.GzipHelper;
import mil.af.abms.midas.config.S3Properties;
import mil.af.abms.midas.exception.S3ClientException;

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

    public boolean compressStringAndSendToBucket(String fileName, String data) {
        var compressedStream = GzipHelper.compressStringToInputStream(data);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(ContentType.TEXT_PLAIN.toString());
        metadata.addUserMetadata("title", fileName);
        metadata.setContentEncoding("gzip");

        var request = new PutObjectRequest(this.bucketName, fileName, compressedStream, metadata);

        return makeRequestReturnSuccess(() -> s3.putObject(request));
    }

    public List<String> getFileNamesFromBucket() {
        var result = (ListObjectsV2Result) makeRequest(() -> s3.listObjectsV2(this.bucketName));
        var objects = result.getObjectSummaries();
        return objects.stream().map(o -> o.getKey()).collect(Collectors.toList());
    }

    public String getFileContentFromBucket(String key) {
        var s3Object = (S3Object) makeRequest(() -> s3.getObject(this.bucketName, key));
        var s3ObjectStream = s3Object.getObjectContent();
        return GzipHelper.decompressInputStreamToString(s3ObjectStream);
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
            throw new S3ClientException(e.getLocalizedMessage());
        }
    }

    protected boolean makeRequestReturnSuccess(S3Client.S3ClientThunk<?> request) {
        try {
            request.call();
            return true;
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }
}

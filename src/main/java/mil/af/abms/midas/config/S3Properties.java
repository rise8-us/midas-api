package mil.af.abms.midas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;

@Getter
@ConfigurationProperties("s3")
@ConstructorBinding
public class S3Properties {

    private final String minioAccessKey;
    private final String minioSecretKey;
    private final String minioHost;
    private final String minioBucketName;
    private final String awsDefaultRegion;

    public S3Properties(
            String minioAccessKey,
            String minioSecretKey,
            String minioHost,
            String minioBucketName,
            String awsDefaultRegion
    ) {
        this.minioAccessKey = minioAccessKey;
        this.minioSecretKey = minioSecretKey;
        this.minioHost = minioHost;
        this.minioBucketName = minioBucketName;
        this.awsDefaultRegion = awsDefaultRegion;
    }

}

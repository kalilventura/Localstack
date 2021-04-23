package br.com.github.kalilventura.file.aws.configuration;

import br.com.github.kalilventura.file.aws.configuration.properties.S3Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
public class AmazonS3Configuration {

    @Bean
    public S3Client s3Client(S3Properties properties) {
        S3ClientBuilder builder = S3Client
                .builder()
                .region(Region.of(properties.getRegion()));

        if (properties.getEndpoint() != null)
            builder.endpointOverride(properties.getEndpoint());

        return builder.build();
    }
}

package br.com.github.kalilventura.file.aws.configuration;

import br.com.github.kalilventura.file.aws.configuration.properties.DynamoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@Configuration
@EnableConfigurationProperties(DynamoProperties.class)
public class AmazonDynamoConfiguration {

    @Bean
    public DynamoDbClient getDynamoDbClient(DynamoProperties properties) {
        DynamoDbClientBuilder builder = DynamoDbClient
                .builder()
                .region(Region.of(properties.getRegion()));

        if (properties.getEndpoint() != null)
            builder.endpointOverride(properties.getEndpoint());

        return builder.build();
    }
}

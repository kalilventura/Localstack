package br.com.github.kalilventura.file.aws.configuration;

import br.com.github.kalilventura.file.aws.configuration.properties.SqsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

@Configuration
@EnableConfigurationProperties(SqsProperties.class)
public class AmazonSqsConfiguration {

    @Bean
    public SqsClient getSqsClient(SqsProperties properties) {
        SqsClientBuilder builder = SqsClient
                .builder();

        if (properties.getEndpoint() != null)
            builder.endpointOverride(properties.getEndpoint());

        return builder.build();
    }
}

package br.com.github.kalilventura.file.aws.configuration;

import br.com.github.kalilventura.file.aws.configuration.properties.LambdaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.LambdaClientBuilder;

@Configuration
@EnableConfigurationProperties(LambdaProperties.class)
public class AmazonLambdaConfiguration {

    @Bean
    public LambdaClient getLambdaClient(LambdaProperties properties) {
        LambdaClientBuilder builder = LambdaClient.builder();

        if (properties.getEndpoint() != null)
            builder.endpointOverride(properties.getEndpoint());

        return builder.build();
    }
}

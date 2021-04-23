package br.com.github.kalilventura.eventos.aws.configuration;

import br.com.github.kalilventura.eventos.aws.configuration.properties.SnsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.SnsClientBuilder;

@Configuration
@EnableConfigurationProperties(SnsProperties.class)
public class AmazonSnsConfiguration {

    @Bean
    public SnsClient getSnsClient(SnsProperties properties) {
        SnsClientBuilder builder = SnsClient.builder();

        if (properties.getEndpoint() != null)
            builder.endpointOverride(properties.getEndpoint());

        return builder.build();
    }
}

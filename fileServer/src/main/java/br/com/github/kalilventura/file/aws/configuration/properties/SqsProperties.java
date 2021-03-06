package br.com.github.kalilventura.file.aws.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@Data
@ConfigurationProperties("sqs")
public class SqsProperties {
    private URI endpoint;
    private String region;
}

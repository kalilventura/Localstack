package br.com.github.kalilventura.eventos.aws.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@Data
@ConfigurationProperties("s3")
public class S3Properties {
    private URI endpoint;
    private String region;
}

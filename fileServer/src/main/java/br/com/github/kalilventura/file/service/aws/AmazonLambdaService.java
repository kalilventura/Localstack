package br.com.github.kalilventura.file.service.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@Service
@RequiredArgsConstructor
public class AmazonLambdaService {
    private final LambdaClient lambdaClient;

    @Value("${lambda.function}")
    private String functionName;

    public String sendMessage(String message) {
        return invokeFunction(functionName, message);
    }

    private String invokeFunction(String functionName, String message) {
        try {
            SdkBytes payload = SdkBytes.fromUtf8String(message);

            InvokeRequest request = InvokeRequest
                    .builder()
                    .functionName(functionName)
                    .invocationType(InvocationType.REQUEST_RESPONSE)
                    .payload(payload)
                    .build();

            InvokeResponse response = lambdaClient.invoke(request);
            return response.payload().asUtf8String();
        } catch (Exception e) {
            throw e;
        }
    }

}
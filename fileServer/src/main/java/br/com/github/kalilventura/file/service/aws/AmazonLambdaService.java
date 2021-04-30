package br.com.github.kalilventura.file.service.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@Service
public class AmazonLambdaService {
    @Autowired
    private LambdaClient lambdaClient;

    @Value("${lambda.function}")
    private String functionName;

    public String sendMessage(String message) {
        return invokeFunction("LocalstackCloudformation", message);
    }

    private String invokeFunction(String functionName, String message) {
        try {
            SdkBytes payload = SdkBytes.fromUtf8String(message);

            InvokeRequest request = InvokeRequest
                    .builder()
                    .functionName(functionName)
                    //.invocationType(InvocationType.REQUEST_RESPONSE)
                    .payload(payload)
                    .build();

            InvokeResponse response = lambdaClient.invoke(request);

            System.out.println("Response: " + response.payload().asUtf8String());

            return response.payload().asUtf8String();
        } catch (Exception e) {
            throw e;
        }
    }

}
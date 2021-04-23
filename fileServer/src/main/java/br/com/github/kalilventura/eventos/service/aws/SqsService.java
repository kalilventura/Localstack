package br.com.github.kalilventura.eventos.service.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
public class SqsService {
    @Autowired
    private SqsClient sqsClient;

    @Value("${sqs.queuename}")
    private String queueName;

    public String publish(String message) {
        try {
            SendMessageRequest request = SendMessageRequest
                    .builder()
                    .queueUrl(getQueueUrl())
                    .messageBody(message)
                    .delaySeconds(5)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(request);

            System.out.println("Message sent, id: " + response.messageId());
            return response.messageId();
        } catch (Exception e) {
            throw e;
        }
    }

    private String getQueueUrl() {
        GetQueueUrlRequest request = GetQueueUrlRequest
                .builder()
                .queueName(queueName)
                .build();

        return sqsClient
                .getQueueUrl(request)
                .queueUrl();
    }
}

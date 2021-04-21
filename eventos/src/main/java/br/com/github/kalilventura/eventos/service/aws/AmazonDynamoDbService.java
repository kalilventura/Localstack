package br.com.github.kalilventura.eventos.service.aws;

import br.com.github.kalilventura.eventos.domain.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Service
public class AmazonDynamoDbService {
    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Value("${dynamo.table}")
    private String tableName;

    public boolean putItem(File file) {
        try {

            HashMap<String, AttributeValue> itemValues = new HashMap<String, AttributeValue>();
            itemValues.put("id", AttributeValue.builder().s(file.getId()).build());
            itemValues.put("name", AttributeValue.builder().s(file.getName()).build());
            itemValues.put("extension", AttributeValue.builder().s(file.getExtension()).build());
            itemValues.put("size", AttributeValue.builder().s(file.getSize()).build());
            itemValues.put("version", AttributeValue.builder().s(file.getVersion()).build());
            itemValues.put("numberOfDownloads", AttributeValue.builder().s(file.getNumberOfDownloads()).build());


            PutItemRequest request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(itemValues)
                    .build();

            PutItemResponse response = dynamoDbClient.putItem(request);
            System.out.println(tableName + " was successfully updated");

            return response.sdkHttpResponse().isSuccessful();
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            throw e;
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    public File getFile(String key, String keyVal) {
        try {
            HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();

            keyToGet.put(key, AttributeValue.builder()
                    .s(keyVal).build());

            GetItemRequest request = GetItemRequest.builder()
                    .key(keyToGet)
                    .tableName(tableName)
                    .build();

            Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();

            Set<String> keys = returnedItem.keySet();

            for (String currentKey : keys) {
                System.out.format("%s: %s\n", currentKey, returnedItem.get(currentKey).toString());
            }

            //TODO: RETURN A VALID FILE
            return null;

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    public boolean deleteFile(String key, String keyVal) {
        try {
            HashMap<String, AttributeValue> keyToGet =
                    new HashMap<String, AttributeValue>();

            keyToGet.put(key, AttributeValue.builder()
                    .s(keyVal)
                    .build());

            DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                    .tableName(tableName)
                    .key(keyToGet)
                    .build();

            DeleteItemResponse response = dynamoDbClient.deleteItem(deleteReq);

            return response.sdkHttpResponse().isSuccessful();

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
}

package br.com.github.kalilventura.eventos.service.aws;

import br.com.github.kalilventura.eventos.domain.Archive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AmazonDynamoDbService {
    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Value("${dynamo.table}")
    private String tableName;

    public boolean putItem(Archive archive) {
        try {

            HashMap<String, AttributeValue> itemValues = new HashMap<String, AttributeValue>();
            itemValues.put("id", AttributeValue.builder().s(String.valueOf(archive.getId())).build());
            itemValues.put("name", AttributeValue.builder().s(archive.getName()).build());
            itemValues.put("extension", AttributeValue.builder().s(archive.getExtension()).build());
            itemValues.put("size", AttributeValue.builder().s(String.valueOf(archive.getSize())).build());
            itemValues.put("version", AttributeValue.builder().s(archive.getVersion()).build());
            itemValues.put("numberOfDownloads", AttributeValue.builder().s(String.valueOf(archive.getNumberOfDownloads())).build());

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

    public Archive getFile(String key, String keyVal) {
        try {
            HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();

            keyToGet.put(key, AttributeValue.builder().s(keyVal).build());

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
            HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();

            keyToGet.put(key, AttributeValue.builder().s(keyVal).build());

            DeleteItemRequest deleteReq = DeleteItemRequest
                    .builder()
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

    public void update(String key, String keyValue) {
        try {
            Map<String, AttributeValue> valueMap = new HashMap<>();
            valueMap.put(key, AttributeValue.builder().s(keyValue).build());

            UpdateItemRequest request = UpdateItemRequest
                    .builder()
                    .key(valueMap)
                    .build();

            UpdateItemResponse response = dynamoDbClient.updateItem(request);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
}

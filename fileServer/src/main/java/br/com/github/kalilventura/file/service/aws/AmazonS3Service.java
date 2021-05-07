package br.com.github.kalilventura.file.service.aws;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final S3Client s3client;

    @Value("${s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        try {
            String fileName = multipartFile.getOriginalFilename();
            String contentType = multipartFile.getContentType();
            InputStream inputStream = multipartFile.getInputStream();

            return uploadFile(inputStream, fileName, contentType);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @SneakyThrows
    public String uploadFile(InputStream file, String fileName, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .contentType(contentType)
                    .key(fileName)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(file, file.available());
            PutObjectResponse response = s3client.putObject(putObjectRequest, requestBody);
            return response.eTag();
        } catch (IOException ex) {
            throw ex;
        }
    }

    public ResponseInputStream<GetObjectResponse> downloadFile(String key) {
        GetObjectRequest request = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3client.getObject(request);
        return response;
    }

    public boolean deleteFile(String key) {
        DeleteObjectRequest request = DeleteObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .build();
        DeleteObjectResponse response = s3client.deleteObject(request);
        return response.sdkHttpResponse().isSuccessful();
    }
}

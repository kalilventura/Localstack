package br.com.github.kalilventura.eventos.service;

import br.com.github.kalilventura.eventos.service.aws.AmazonDynamoDbService;
import br.com.github.kalilventura.eventos.service.aws.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    @Autowired
    private AmazonS3Service s3Service;
    @Autowired
    private AmazonDynamoDbService dynamoDbService;

    @Value("${img.prefix.event}")
    private String prefix;

    @SneakyThrows
    public boolean uploadFile(MultipartFile multipartFile) {
        long size = multipartFile.getSize();
        String imageName = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());

        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String fileName = prefix + "-" + imageName + LocalDate.now();

        InputStream inputFile = multipartFile.getInputStream();
        br.com.github.kalilventura.eventos.domain.File file = new br.com.github.kalilventura.eventos.domain.File();

        file.setId(s3Service.uploadFile(inputFile, fileName, "file"));
        file.setExtension(extension);
        file.setName(fileName);
        file.setSize(String.valueOf(size));
        file.setVersion("1");
        file.setNumberOfDownloads("0");

        return dynamoDbService.putItem(file);
    }

    @SneakyThrows
    public Resource downloadFile(String fileName) {
        try {
            ResponseInputStream<GetObjectResponse> inputStream = s3Service.downloadFile(fileName);

            byte[] fileBytes = inputStream.readAllBytes();

            File outputFile = new File(fileName);
            FileUtils.writeByteArrayToFile(outputFile, fileBytes);

            URI fileUri = outputFile.toPath().toUri();

            Resource resource = new UrlResource(fileUri);
            return resource;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception ex) {
            throw ex;
        }

    }

}

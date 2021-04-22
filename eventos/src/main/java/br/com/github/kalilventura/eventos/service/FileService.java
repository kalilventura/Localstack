package br.com.github.kalilventura.eventos.service;

import br.com.github.kalilventura.eventos.domain.Archive;
import br.com.github.kalilventura.eventos.repository.ArchiveRepository;
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

@Service
@RequiredArgsConstructor
public class FileService {
    @Autowired
    private AmazonS3Service s3Service;
    @Autowired
    private AmazonDynamoDbService dynamoDbService;
    @Autowired
    private ArchiveRepository repository;

    @Value("${img.prefix.event}")
    private String prefix;

    @SneakyThrows
    public Archive uploadFile(MultipartFile multipartFile) {
        long size = multipartFile.getSize();
        String imageName = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());

        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String fileName = prefix + "-" + LocalDate.now() + "-" + imageName;

        InputStream inputFile = multipartFile.getInputStream();
        Archive archive = new Archive();

        String eTag = s3Service.uploadFile(inputFile, fileName, "file");

        archive.setETag(eTag);
        archive.setExtension(extension);
        archive.setName(fileName);
        archive.setSize(size);
        archive.setVersion("1");
        archive.setNumberOfDownloads(0);

        //dynamoDbService.putItem(archive);
        repository.save(archive);
        return archive;
    }

    @SneakyThrows
    public Resource downloadFile(String name) {
        try {
            Archive archive = repository.findArchiveByName(name);
            ResponseInputStream<GetObjectResponse> inputStream = s3Service.downloadFile(name);

            String fileName = archive.getName() + "." + archive.getExtension();

            byte[] fileBytes = inputStream.readAllBytes();
            File outputFile = new File(fileName);
            FileUtils.writeByteArrayToFile(outputFile, fileBytes);

            URI fileUri = outputFile.toPath().toUri();

            Resource resource = new UrlResource(fileUri);

            //dynamoDbService.getFile("name", fileName);

            long numberDownloads = archive.getNumberOfDownloads();
            archive.setNumberOfDownloads(numberDownloads++);

            repository.save(archive);

            return resource;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @SneakyThrows
    public boolean deleteFile(String name) {
        try {
            Archive archive = repository.findArchiveByName(name);
            repository.delete(archive);

            return s3Service.deleteFile(name);
        } catch (Exception ex) {
            throw ex;
        }
    }

}

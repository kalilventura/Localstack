package br.com.github.kalilventura.file.service;

import br.com.github.kalilventura.file.domain.Archive;
import br.com.github.kalilventura.file.repository.ArchiveRepository;
import br.com.github.kalilventura.file.service.aws.AmazonLambdaService;
import br.com.github.kalilventura.file.service.aws.AmazonS3Service;
import br.com.github.kalilventura.file.service.aws.SqsService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FileService {
    private final AmazonS3Service s3Service;
    private final ArchiveRepository repository;
    private final SqsService sqsService;
    private final AmazonLambdaService lambdaService;

    @Value("${img.prefix}")
    private String prefix;

    @SneakyThrows
    public Archive uploadFile(MultipartFile multipartFile) {
        long size = multipartFile.getSize();
        String originalFileName = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());

        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String fileName = prefix + "-" + originalFileName;

        InputStream inputFile = multipartFile.getInputStream();
        Archive archive = new Archive();

        String eTag = s3Service.uploadFile(inputFile, fileName, "file");

        archive.setETag(eTag);
        archive.setExtension(extension);
        archive.setName(fileName);
        archive.setSize(size);
        archive.setVersion("1");
        archive.setNumberOfDownloads(0);

        repository.save(archive);

        String message = "File " + archive.getName() + " created at " + LocalDate.now();
        JSONObject object = createJsonFile(archive.getId(), archive.getName(), message, LocalDate.now());

        sqsService.publish(object.toString());
        lambdaService.sendMessage(object.toString());

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

            long numberDownloads = archive.getNumberOfDownloads();
            archive.setNumberOfDownloads(++numberDownloads);

            repository.save(archive);

            String message = "File " + archive.getName() + " downloaded at" + LocalDate.now() + " number of downloads: " + numberDownloads;
            JSONObject object = createJsonFile(archive.getId(), archive.getName(), message, LocalDate.now());

            sqsService.publish(object.toString());
            lambdaService.sendMessage(message);

            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @SneakyThrows
    public boolean deleteFile(String name) {
        try {
            Archive archive = repository.findArchiveByName(name);
            repository.delete(archive);

            String message = "File " + archive.getName() + " deleted at" + LocalDate.now();
            JSONObject object = createJsonFile(archive.getId(), archive.getName(), message, LocalDate.now());

            sqsService.publish(object.toString());
            lambdaService.sendMessage(message);

            return s3Service.deleteFile(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private JSONObject createJsonFile(long id, String archiveName, String message, LocalDate date) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("filename", archiveName);
        object.put("createdAt", date);
        object.put("message", message);
        return object;
    }

}

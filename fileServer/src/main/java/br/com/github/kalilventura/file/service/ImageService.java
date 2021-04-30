package br.com.github.kalilventura.file.service;

import br.com.github.kalilventura.file.domain.Archive;
import br.com.github.kalilventura.file.repository.ArchiveRepository;
import br.com.github.kalilventura.file.service.aws.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Autowired
    private AmazonS3Service s3Service;
    @Autowired
    private ArchiveRepository repository;

    @Value("${img.prefix.event}")
    private String prefix;

//    public boolean uploadPicture(MultipartFile multipartFile) {
//        long size = multipartFile.getSize();
//        String imageName = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());
//
//        BufferedImage jpgImage = getJpgImageFromFile(multipartFile);
//        jpgImage = cropSquare(jpgImage);
//        //jpgImage = imageService.resize(jpgImage, size);
//
//        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
//        String fileName = prefix + imageName;
//
//        Archive archive = new Archive();
//        archive.setExtension(extension);
//        archive.setName(fileName);
//        archive.setSize(size);
//        archive.setVersion("1");
//        archive.setNumberOfDownloads(0);
//        archive.setETag(s3Service.uploadFile(getInputStream(jpgImage, extension), fileName, "image"));
//
//        return dynamoDbService.putItem(archive);
//    }

    @SneakyThrows
    public Resource downloadPicture(String name) {
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
    public boolean deleteImage(String name) {
        try {
            Archive archive = repository.findArchiveByName(name);
            repository.delete(archive);

            return s3Service.deleteFile(name);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private BufferedImage getJpgImageFromFile(MultipartFile uploadedFile) {
        try {
            BufferedImage img = ImageIO.read(uploadedFile.getInputStream());
            return img;
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler arquivo");
        }
    }

    private BufferedImage pngToJpg(BufferedImage img) {
        BufferedImage jpgImage = new BufferedImage(img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        jpgImage.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
        return jpgImage;
    }

    private InputStream getInputStream(BufferedImage img, String extension) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, extension, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler arquivo");
        }
    }

    private BufferedImage cropSquare(BufferedImage sourceImg) {
        int min = (sourceImg.getHeight() <= sourceImg.getWidth()) ? sourceImg.getHeight() : sourceImg.getWidth();
        return Scalr.crop(
                sourceImg,
                (sourceImg.getWidth() / 2) - (min / 2),
                (sourceImg.getHeight() / 2) - (min / 2),
                min,
                min);
    }

    private BufferedImage resize(BufferedImage sourceImg, int size) {
        return Scalr.resize(sourceImg, Scalr.Method.ULTRA_QUALITY, size);
    }
}

package br.com.github.kalilventura.eventos.service;

import br.com.github.kalilventura.eventos.service.aws.AmazonDynamoDbService;
import br.com.github.kalilventura.eventos.service.aws.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
    private AmazonDynamoDbService dynamoDbService;

    @Value("${img.prefix.event}")
    private String prefix;

    public boolean uploadPicture(MultipartFile multipartFile) {
        long size = multipartFile.getSize();
        String imageName = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());

        BufferedImage jpgImage = getJpgImageFromFile(multipartFile);
        jpgImage = cropSquare(jpgImage);
        //jpgImage = imageService.resize(jpgImage, size);

        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String fileName = prefix + imageName;

        br.com.github.kalilventura.eventos.domain.File file = new br.com.github.kalilventura.eventos.domain.File();
        file.setExtension(extension);
        file.setName(fileName);
        file.setSize(String.valueOf(size));
        file.setVersion("1");
        file.setNumberOfDownloads("0");

        file.setId(s3Service.uploadFile(getInputStream(jpgImage, extension), fileName, "image"));

        return dynamoDbService.putItem(file);
    }

    @SneakyThrows
    public Resource downloadPicture(String fileName) {
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

    public BufferedImage getJpgImageFromFile(MultipartFile uploadedFile) {
        try {
            BufferedImage img = ImageIO.read(uploadedFile.getInputStream());
            return img;
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler arquivo");
        }
    }

    public BufferedImage pngToJpg(BufferedImage img) {
        BufferedImage jpgImage = new BufferedImage(img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        jpgImage.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
        return jpgImage;
    }

    public InputStream getInputStream(BufferedImage img, String extension) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, extension, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler arquivo");
        }
    }

    public BufferedImage cropSquare(BufferedImage sourceImg) {
        int min = (sourceImg.getHeight() <= sourceImg.getWidth()) ? sourceImg.getHeight() : sourceImg.getWidth();
        return Scalr.crop(
                sourceImg,
                (sourceImg.getWidth() / 2) - (min / 2),
                (sourceImg.getHeight() / 2) - (min / 2),
                min,
                min);
    }

    public BufferedImage resize(BufferedImage sourceImg, int size) {
        return Scalr.resize(sourceImg, Scalr.Method.ULTRA_QUALITY, size);
    }
}

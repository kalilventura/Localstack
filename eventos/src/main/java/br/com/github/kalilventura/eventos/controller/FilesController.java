package br.com.github.kalilventura.eventos.controller;

import br.com.github.kalilventura.eventos.domain.Archive;
import br.com.github.kalilventura.eventos.service.FileService;
import br.com.github.kalilventura.eventos.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FilesController {
    @Autowired
    private FileService fileService;
    @Autowired
    private ImageService imageService;

    @PostMapping("/upload/image")
    public ResponseEntity<Void> uploadImage(@RequestParam("file") MultipartFile file) {
        boolean response = imageService.uploadPicture(file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload/file")
    public ResponseEntity<Archive> uploadFile(@RequestParam("file") MultipartFile file) {
        Archive response = fileService.uploadFile(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/image/{fileName:.+}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        try {
            Resource resource = imageService.downloadPicture(fileName);
            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/download/file/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        try {
            Resource resource = fileService.downloadFile(fileName);
            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @DeleteMapping("/delete/file/{fileName:.+}")
    public ResponseEntity deleteFile(@PathVariable String fileName) {
        fileService.deleteFile(fileName);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/delete/image/{fileName:.+}")
    public ResponseEntity deleteImage(@PathVariable String fileName) {
        imageService.deleteImage(fileName);
        return ResponseEntity
                .noContent()
                .build();
    }
}

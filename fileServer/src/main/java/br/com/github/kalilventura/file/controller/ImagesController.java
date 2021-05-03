package br.com.github.kalilventura.file.controller;

import br.com.github.kalilventura.file.domain.Archive;
import br.com.github.kalilventura.file.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@Api(value = "Images")
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImagesController {
    private final ImageService imageService;

    @PostMapping("/upload")
    @ApiOperation(value = "Upload an image in s3 bucket")
    public ResponseEntity<Archive> uploadImage(@RequestParam("file") MultipartFile file) {
        Archive response = imageService.uploadPicture(file);
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/download/{fileName:.+}")
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


    @DeleteMapping("/delete/{fileName:.+}")
    public ResponseEntity deleteImage(@PathVariable String fileName) {
        try {
            imageService.deleteImage(fileName);
            return ResponseEntity
                    .noContent()
                    .build();
        } catch (Exception ex) {
            throw ex;
        }
    }
}

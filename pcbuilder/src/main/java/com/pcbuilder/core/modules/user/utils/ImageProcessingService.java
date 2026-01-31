package com.pcbuilder.core.modules.user.utils;

import com.pcbuilder.core.modules.exception.ImageProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class ImageProcessingService {
    public InputStream resizeImage(MultipartFile file, int width, int height) {
        log.info("Resizing image to width: {} and height: {}", width, height);
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage resizedImage = Scalr.resize(
                    originalImage,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.AUTOMATIC,
                    width,
                    height,
                    Scalr.OP_ANTIALIAS
            );
            return convertToInputStream(resizedImage, "jpg");
        } catch (IOException ex) {
            log.error("Error resizing image", ex);
            throw new ImageProcessingException("Failed to resize image");
        }
    }
    public InputStream convertToInputStream(BufferedImage image, String formatName) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException ex) {
            log.error("Error converting image to InputStream", ex);
            throw new ImageProcessingException("Failed to convert image to InputStream");
        }
    }
    public boolean isValidImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException ex) {
            log.error("Error validating image", ex);
            return false;
        }
    }
}

package com.pcbuilder.core.modules.user.service;

import com.pcbuilder.core.modules.auth.dto.MessageResponse;
import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import com.pcbuilder.core.modules.user.utils.FileStorageService;
import com.pcbuilder.core.modules.user.utils.ImageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarService {
    private final FileStorageService fileStorageService;
    private final ImageProcessingService imageProcessingService;
    private final UserRepository userRepository;


    public Optional<MessageResponse> uploadAvatar(MultipartFile file, String username) {
        return userRepository.findByUsername(username).map(user -> {
            if(!imageProcessingService.isValidImage(file)) {
                throw new IllegalArgumentException("Invalid image file");
            }

            InputStream resizeImage = imageProcessingService.resizeImage(file, 800, 600);
            InputStream thumbImage = imageProcessingService.resizeImage(file, 100, 100);

            String fileName = fileStorageService.storeStream(resizeImage, "jpg");
            String thumbFileName = fileStorageService.storeStream(thumbImage, "jpg");

            updateUserData(user, fileName, thumbFileName);
            return new MessageResponse("Avatar uploaded successfully");
        });
    }

    private void updateUserData(UserEntity user, String fileName, String thumbFileName) {
        deleteOldAvatar(user);
        user.setAvatarFileName(fileName);
        user.setAvatarThumbFileName(thumbFileName);
        userRepository.save(user);
    }
    public Optional<Resource> getAvatarResource(String username, boolean thumbnail) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    String fileName = thumbnail ? user.getAvatarThumbFileName() : user.getAvatarFileName();
                    return fileStorageService.loadFileAsResource(fileName);
                });
    }
    @Transactional
    public Optional<MessageResponse> deleteAvatar(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    deleteOldAvatar(user);
                    user.setAvatarFileName(null);
                    user.setAvatarThumbFileName(null);
                    userRepository.save(user);
                    return new MessageResponse("Avatar deleted successfully");
                });
    }
    public Resource getDefaultAvatarResource(boolean thumbnail) {
        String path = thumbnail ? "static/images/default_avatar_thumb.jpg" : "static/images/default_avatar.jpg";
        Resource resource = new ClassPathResource(path);
        if (resource.exists()) {
            return resource;
        }
        throw new RuntimeException("Default avatar not found in classpath");
    }

    public String[] processOAuthAvatar(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return null;
        }

        try {
            URL url = new URL(imageUrl);
            BufferedImage originalImage = ImageIO.read(url);

            if (originalImage == null) return null;

            BufferedImage resized = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, 800, 600, Scalr.OP_ANTIALIAS);
            BufferedImage thumb = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, 100, 100, Scalr.OP_ANTIALIAS);

            InputStream resizeStream = imageProcessingService.convertToInputStream(resized, "jpg");
            InputStream thumbStream = imageProcessingService.convertToInputStream(thumb, "jpg");

            String fileName = fileStorageService.storeStream(resizeStream, "jpg");
            String thumbFileName = fileStorageService.storeStream(thumbStream, "jpg");

            log.info("OAuth avatar processed successfully: {}", fileName);
            return new String[]{fileName, thumbFileName};

        } catch (Exception e) {
            log.error("Failed to process OAuth avatar from URL: {}", imageUrl, e);
            return null;
        }
    }

    private void deleteOldAvatar(UserEntity user) {
        if (user.getAvatarFileName() != null) {
            fileStorageService.deleteFile(user.getAvatarFileName());
        }
        if (user.getAvatarThumbFileName() != null) {
            fileStorageService.deleteFile(user.getAvatarThumbFileName());
        }
    }




}

package com.pcbuilder.core.modules.user.controller;

import com.pcbuilder.core.modules.auth.dto.MessageResponse;
import com.pcbuilder.core.modules.user.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> uploadAvatar(
            @RequestParam MultipartFile file,
            Authentication authentication) {

        return avatarService.uploadAvatar(file, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body(new MessageResponse("Could not upload avatar")));
    }

    @GetMapping("/{username}/avatar")
    public ResponseEntity<Resource> getAvatar(
            @PathVariable String username,
            @RequestParam(defaultValue = "false") boolean thumbnail) {

        return avatarService.getAvatarResource(username, thumbnail)
                .map(resource -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<MessageResponse> deleteAvatar(Authentication authentication) {
        return avatarService.deleteAvatar(authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body(new MessageResponse("Avatar not found or could not be deleted")));
    }
}
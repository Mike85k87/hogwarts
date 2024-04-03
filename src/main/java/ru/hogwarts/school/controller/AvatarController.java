package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;
    private final int maxFileInByte = 300 * 1024;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@RequestParam Long studentId,
                                               @RequestParam MultipartFile avatar)
            throws IOException {
        if (avatar.getSize() > maxFileInByte) {
            return ResponseEntity.badRequest().body("Изображение слишком большое");
        }
        avatarService.uploadAvatar(studentId, avatar);
        return ResponseEntity.ok().body("Аватар загружен");
    }

    @GetMapping("/{id}/avatar-from-db")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable long id) {
        Avatar avatar = avatarService.readFromDB(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(avatar.getData());
    }

    @GetMapping("/{id}/avatar-from-file")
    public ResponseEntity<byte[]> download(@PathVariable long id) throws IOException {
        File avatar = avatarService.readFromFile(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(Files.probeContentType(avatar.toPath())));
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(Files.readAllBytes(avatar.toPath()));
    }
}

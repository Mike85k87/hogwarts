package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {

    private final String avatarsDir;
    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    public AvatarService(@Value("${path.to.avatar.folder}") String avatarsDir,
                         StudentService studentService,
                         AvatarRepository avatarRepository) {
        this.avatarsDir = avatarsDir;
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public void uploadAvatar (Long studentId, MultipartFile avatarFile) throws IOException {
        Student student = studentService.findStudent(studentId);
        Path filePath = saveToFile(student, avatarFile);
        saveToDb(filePath, avatarFile, student);
    }
    private Path saveToFile (Student student, MultipartFile avatarFile) throws IOException {
        Path filePath = Path.of(avatarsDir,
                student.getId() + "." + getExtensions(avatarFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (
                InputStream inputStream = avatarFile.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024);
                OutputStream outputStream = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 1024);
        ) {
            bufferedInputStream.transferTo(bufferedOutputStream);
        }
        return filePath;
    }
    private void saveToDb(Path filePath, MultipartFile avatarFile, Student student) throws IOException {
        Avatar avatar = avatarRepository.findByStudent_id(student.getId()).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFileSize(avatarFile.getSize());
        avatar.setFilePath(filePath.toString());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(avatarFile.getBytes());
        avatarRepository.save(avatar);
    }

    private String getExtensions(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public Avatar readFromDB(long id) {
        return avatarRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Аватар не найден"));
    }
    public File readFromFile(long id) throws IOException {
        Avatar avatar = readFromDB(id);
        Path path = Path.of(avatar.getFilePath());
        return new File(path.toString());
    }
}

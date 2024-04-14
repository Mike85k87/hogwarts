package ru.hogwarts.school.service;

import nonapi.io.github.classgraph.utils.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AvatarServiceTest {
    StudentService studentService = mock(StudentService.class);
    AvatarRepository avatarRepository = mock(AvatarRepository.class);
    String avatarsDir = "./src/test/resources/avatar";
    AvatarService avatarService = new AvatarService(avatarsDir, studentService, avatarRepository);
    Student student = new Student(1L, "Mike", 18);


    @Test
    void shouldCheckUploadAvatarWhenAvatarSavedInDBAndDirectory() throws IOException {
        String fileName = "1.pdf";
        MultipartFile file = new MockMultipartFile(fileName, fileName,
                "application/pdf", new byte[]{});
        when(studentService.findStudent(student.getId())).thenReturn(student);
        when(avatarRepository.findByStudent_id(student.getId())).thenReturn(Optional.empty());
        avatarService.uploadAvatar(student.getId(), file);
        verify(avatarRepository, times(1)).save(any());
        assertTrue(FileUtils.canRead(new File(avatarsDir + "/" + student.getId()
                + "." + fileName.substring(fileName.lastIndexOf(".") + 1))));
    }

    @Test
    void shouldCheckReadFromDB() {
        long avatarId = 1L;
        Avatar mockAvatar = new Avatar();
        when(avatarRepository.findById(avatarId)).thenReturn(Optional.of(mockAvatar));
        Avatar result = avatarService.readFromDB(avatarId);

        assertNotNull(result);
        assertEquals(mockAvatar, result);
    }

    @Test
    void shouldCheckReadFromFile() throws IOException {
        long avatarId = 1L;
        Avatar mockAvatar = new Avatar();
        mockAvatar.setFilePath("path.to.file");
        when(avatarRepository.findById(avatarId)).thenReturn(Optional.of(mockAvatar));
        File mockFile = mock(File.class);
        Path mockPath = mock(Path.class);
        when(mockPath.toString()).thenReturn(mockAvatar.getFilePath());
        when(mockFile.toPath()).thenReturn(mockPath);
        File result = avatarService.readFromFile(avatarId);

        assertNotNull(result);
        assertEquals(mockAvatar.getFilePath(), result.getPath());
    }
}

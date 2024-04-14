package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {
    @MockBean
    FacultyRepository facultyRepository;
    @SpyBean
    FacultyService facultyService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    Faculty faculty = new Faculty(1L, "Griffindor", "brown");
    @InjectMocks
    FacultyController facultyController;

    @Test
    void create_shouldReturnFacultyAndStatus200() throws Exception {
        when(facultyRepository.save(faculty)).thenReturn(faculty);
        mockMvc.perform(post("/faculty")
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(faculty));
    }

    @Test
    void read_shouldReturnStatusIsNotFound() throws Exception {
        when(facultyRepository.findById(faculty.getId()))
                .thenReturn(Optional.empty());
        assertThrows(Exception.class,
                () -> {
                    mockMvc.perform(get("/faculty/" + faculty.getId()));
                });
    }

    @Test
    void edit_shouldReturnFacultyAndChanges() throws Exception {
        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(faculty)).thenReturn(faculty);
        mockMvc.perform(put("/faculty/" + faculty.getId())
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(faculty));
    }

    @Test
    void delete_shouldReturnDeletedFaculty() {
        when(facultyRepository.findById(faculty.getId()))
                .thenReturn(Optional.empty());
        assertThrows(Exception.class,
                () -> {
                    mockMvc.perform(delete("/faculty/" + faculty.getId()));
                });
        verify(facultyService).deleteFaculty(faculty.getId());
    }

    @Test
    void readByColor_shouldReturnFacultiesCollectionAndStatus200() throws Exception {
        Faculty faculty1 = new Faculty(124L, "FakeGriff", "black");
        when(facultyRepository.findAllByColor(faculty.getColor()))
                .thenReturn(List.of(faculty, faculty1));
        mockMvc.perform(get("/faculty?color=" + faculty.getColor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(faculty))
                .andExpect(jsonPath("$.[1]").value(faculty1));
    }
}

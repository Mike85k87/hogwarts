package ru.hogwarts.school.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {
    @MockBean
    StudentRepository studentRepository;
    @SpyBean
    StudentService studentService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    Student student = new Student(555L, "Garry", 15);
    @InjectMocks
    StudentController studentController;

    @Test
    void create_shouldReturnStudentAndStatus200() throws Exception {
        when(studentRepository.save(student)).thenReturn(student);
        mockMvc.perform(post("/student")
                        .content(objectMapper.writeValueAsString(student))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void read_shouldReturnStatusIsNotFound() {
        when(studentRepository.findById(student.getId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/student/" + student.getId()));
        });
    }

    @Test
    void edit_shouldReturnStudentAndChanges() throws Exception {
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        mockMvc.perform(put("/student/" + student.getId())
                        .content(objectMapper.writeValueAsString(student))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnDeletedStudent() {
        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(Exception.class, () ->{
            mockMvc.perform(delete(("/student/" + student.getId())));
        });
        verify(studentService).deleteStudent(student.getId());
    }

    @Test
    void read_shouldReturnCollectionStudentsAgesStayBetween() throws Exception {
        int minAge = 13;
        int maxAge = 24;
        Student student1 = new Student(4300L, "FakeGarry", 112);
        when(studentRepository.findByAgeBetween(minAge,maxAge))
                .thenReturn(List.of(student));
        ResultActions resultActions = mockMvc.perform(get("/student/age/")
                .param("minAge", String.valueOf(minAge))
                .param("maxAge", String.valueOf(maxAge))
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Garry"))
                .andExpect(jsonPath("$[0].age").value(14))
                .andExpect(jsonPath("$[1].name").value("FakeGarry"))
                .andExpect(jsonPath("$[1].age").value(112));
    }

    @Test
    void read_shouldReturnFacultyById() throws Exception {
        long facultyId = 123;
        Collection<Student> mockStudents = Arrays.asList(
                new Student(1L, "John", 20),
                new Student(2L, "Alice", 22)
        );
        Mockito.when(studentService.findByFacultyId(facultyId)).thenReturn(mockStudents);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/student/findfaculty/")
                .param("facultyId", String.valueOf(facultyId))
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockStudents.size()))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[1].name").value("Alice"))
                .andExpect(jsonPath("$[1].age").value(22));
    }
}

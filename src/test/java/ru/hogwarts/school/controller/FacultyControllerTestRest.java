package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTestRest {
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    FacultyRepository facultyRepository;
    @LocalServerPort
    int port;
    String baseUrl;
    Faculty faculty = new Faculty(1L, "math", "red");

    @BeforeEach
    void beforeEach() {
        facultyRepository.deleteAll();
        baseUrl = "http://localhost:" + port + "/faculty";

    }

    @Test
    void createFaculty_shouldReturnFacultyAndStatus200() {
        Faculty faculty1 = facultyRepository.save(faculty);
        ResponseEntity<Faculty> result = testRestTemplate.postForEntity(
                baseUrl,
                faculty1,
                Faculty.class
        );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(faculty1, result.getBody());
    }
    @Test
    void getFaculty_shouldReturnFacultyAndStatus200(){
        Faculty faculty1 = new Faculty(2L, "history", "red");
        Faculty saveFaculty = facultyRepository.save(faculty1);

        ResponseEntity<Faculty> result = testRestTemplate.getForEntity(
                baseUrl + "/" + saveFaculty.getId(),
                Faculty.class
        );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(saveFaculty, result.getBody());
    }
    @Test
    void getFaculty_shouldReturnStatus404(){

        ResponseEntity<String> result = testRestTemplate.getForEntity(
                baseUrl + "/" + faculty.getId(),
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Факультет не найден", result.getBody());
    }
    @Test
    void updateFaculty_shouldReturnFacultyAndStatus200(){
        Faculty saveFaculty = facultyRepository.save(faculty);

        ResponseEntity<Faculty> result = testRestTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(saveFaculty),
                Faculty.class
        );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(saveFaculty, result.getBody());
    }
    @Test
    void deleteFaculty_shouldReturnFacultyAndStatus200(){
        Faculty saveFaculty = facultyRepository.save(faculty);

        ResponseEntity<Faculty> result = testRestTemplate.exchange(
                baseUrl+"/"+saveFaculty.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(saveFaculty),
                Faculty.class
        );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(saveFaculty, result.getBody());
    }
    @Test
    void getByColor_shouldReturnCollectionFacultiesAndStatus200(){
        Faculty faculty1 = new Faculty(2L, "english", "red");
        Faculty faculty2 = new Faculty(3L, " math", "red");

        Faculty saveFaculty = facultyRepository.save(faculty1);
        Faculty saveFaculty1 = facultyRepository.save(faculty2);

        List<Faculty> faculties = List.of(saveFaculty, saveFaculty1);

        ResponseEntity<List<Faculty>> result = testRestTemplate.exchange(
                baseUrl + "/color?color=" + saveFaculty.getColor(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(faculties, result.getBody());
    }
    @Test
    void getByNameOrColor_shouldReturnCollectionFacultiesAndStatus200(){
        Faculty faculty1 = new Faculty(2L, "english", "red");
        Faculty faculty2 = new Faculty(3L, " math", "green");

        Faculty saveFaculty = facultyRepository.save(faculty1);
        Faculty saveFaculty1 = facultyRepository.save(faculty2);

        List<Faculty> faculties = List.of(saveFaculty, saveFaculty1);

        ResponseEntity<List<Faculty>> result = testRestTemplate.exchange(
                baseUrl + "/nameOrColor?name=" + saveFaculty.getName()+"&color="+saveFaculty1.getColor(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(faculties, result.getBody());
    }


}
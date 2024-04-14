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
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTestRest {
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    FacultyRepository facultyRepository;
    @LocalServerPort
    int port;

    String baseUrl;
    Student student = new Student(1L, "Olga", 15);
    Faculty faculty = new Faculty(1L, "math", "red");

    @BeforeEach
    void beforeEach() {
        studentRepository.deleteAll();
        baseUrl = "http://localhost:" + port + "/student";

    }

    @Test
    void createStudent_shouldReturnStudentAndStatus200() {
        ResponseEntity<Student> result = testRestTemplate.postForEntity(
                baseUrl,
                student,
                Student.class
        );
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(student, result.getBody());
    }

    @Test
    void readStudent_shouldReturnStudentAndStatus200() {
        Student saveStudent = studentRepository.save(student);
        ResponseEntity<Student> result = testRestTemplate.getForEntity(
                baseUrl+"/"+saveStudent.getId(),
                Student.class);

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals(saveStudent,result.getBody());
    }
    @Test
    void readStudent_shouldReturnStatus404() {
        ResponseEntity<String> result = testRestTemplate.getForEntity(
                baseUrl + "/" + student.getId(),
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Студент не найден", result.getBody());

    }

    @Test
    void updateStudent_shouldReturnStudentAndStatus200() {
        Student saveStudent = studentRepository.save(student);

        ResponseEntity<Student> result = testRestTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(saveStudent),
                Student.class
        );
        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals(saveStudent,result.getBody());
    }
    @Test
    void deleteStudent_shouldReturnStudentAndStatus200() {
        Student saveStudent = studentRepository.save(student);

        ResponseEntity<Student> result = testRestTemplate.exchange(
                baseUrl+"/"+saveStudent.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(saveStudent),
                Student.class
        );
        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals(saveStudent,result.getBody());
    }

    @Test
    void findStudentsByAge_shouldReturnStudentsCollectionAndStatus200() {
        Student student2 = new Student(2L, "Kira", 15);
        Student student1 = new Student(3L, "Ivan", 15);

        Student saveStudent = studentRepository.save(student2);
        Student saveStudent1 = studentRepository.save(student1);

        ResponseEntity<List<Student>> result = testRestTemplate.exchange(
                baseUrl + "/byAge?age=" + saveStudent.getAge(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals(List.of(saveStudent,saveStudent1), result.getBody());
    }
    @Test
    void findByAgeBetween_shouldReturnStudentsCollectionAndStatus200(){

        Student student1 = new Student(1L, "Nika", 10);
        Student student2 = new Student(2L, "Olga", 15);
        Student student3 = new Student(3L, "Kira", 17);
        Student student4 = new Student(4L, "Mark", 20);

        Student saveStudent1 = studentRepository.save(student1);
        Student saveStudent2 = studentRepository.save(student2);
        Student saveStudent3 = studentRepository.save(student3);
        Student saveStudent4 = studentRepository.save(student4);


        List<Student> students =List.of(saveStudent2, saveStudent3);


        ResponseEntity<List<Student>> result = testRestTemplate.exchange(
                baseUrl+"/ageBetween?min=" +saveStudent2.getAge()+ "&max="+saveStudent3.getAge(),
                HttpMethod.GET,
                null,
                new  ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals(students,result.getBody());
    }

    @Test
    void getFacultyByStudentId_shouldReturnStudentAndStatus200(){
        Faculty saveFaculty = facultyRepository.save(faculty);
        student.setFaculty(saveFaculty);

        Student saveStudent = studentRepository.save(student);


        ResponseEntity<Faculty> result = testRestTemplate.getForEntity(
                baseUrl + "/getFacultyByStudentId?studentId=" + saveStudent.getId(),
                Faculty.class
        );
        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals(saveFaculty,result.getBody());

    }

    @Test
    void findAllByFacultyId_shouldReturnStudentsCollectionAndStatus200(){
        Faculty saveFaculty =facultyRepository.save(faculty);
        Student student1 = new Student(2L, "Nika", 20);
        Student student2 = new Student(3L, "Kira", 17);
        Student student3 = new Student(4L, "Arina", 18);


        student2.setFaculty(faculty);
        student3.setFaculty(faculty);


        Student saveStudent2 = studentRepository.save(student2);
        Student saveStudent3 = studentRepository.save(student3);

        List<Student> students =List.of(saveStudent2, saveStudent3);


        ResponseEntity<List<Student>> result = testRestTemplate.exchange(
                baseUrl+"/getStudentsByFacultyId?facultyId=" +saveStudent2.getId(),
                HttpMethod.GET,
                null,
                new  ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals(students,result.getBody());
    }


}

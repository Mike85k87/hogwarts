package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {

        this.repository = repository;
    }

    public Student addStudent(Student student) {

        return repository.save(student);
    }

    public Student findStudent(long id) {
        return repository.findById(id).orElseThrow(()-> new RuntimeException("Студент не найден"));
    }

    public Student editStudent(long id, Student student) {
        findStudent(id);
        return repository.save(student);
    }

    public Student deleteStudent(long id) {
        Student result = findStudent(id);
        repository.delete(result);
        return result;
    }

    public Collection<Student> findByAge(int age) {

        return repository.findAllByAge(age);
    }
    public Collection<Student> findByAgeBetween(int minAge, int maxAge) {
        return repository.findByAgeBetween(minAge, maxAge);
    }

    public Faculty findStudentFaculty(long studentId) {
        return findStudent(studentId).getFaculty();
    }

    public Collection<Student> findByFacultyId(long facultyId) {
        return repository.findByFaculty_Id(facultyId);
    }
}

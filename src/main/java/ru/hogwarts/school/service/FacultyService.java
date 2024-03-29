package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {
    private final FacultyRepository faculties;

    public FacultyService(FacultyRepository faculties) {
        this.faculties = faculties;
    }

    public Faculty addFaculty(Faculty faculty) {
        return faculties.save(faculty);
    }

    public Faculty findFaculty(long id) {

        return faculties.findById(id).orElseThrow(()-> new RuntimeException("Факультет не найден"));
    }

    public Faculty editFaculty(long id, Faculty faculty) {
        findFaculty(id);
        return faculties.save(faculty);
    }

    public Faculty deleteFaculty(long id) {

        Faculty result = findFaculty(id);
        faculties.delete(result);
        return result;
    }

    public Collection<Faculty> findByColor(String color) {

        return faculties.findAllByColor(color);
    }

    public Collection<Faculty> findAllByNameIgnoreCaseOrColorIgnoreCase(String name, String color) {
        return faculties.findAllByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }
}

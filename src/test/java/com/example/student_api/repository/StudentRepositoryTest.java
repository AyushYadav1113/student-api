package com.example.student_api.repository;

import com.example.student_api.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StudentRepositoryTest {

    @Autowired
    private StudentRepository repository;

    @Test
    void shouldFindStudentByEmail() {
        Student student = Student.builder()
                .firstName("Ayush")
                .lastName("Yadav")
                .email("ayush@example.com")
                .age(22)
                .build();

        repository.save(student);

        Optional<Student> result = repository.findByEmail("ayush@example.com");

        assertTrue(result.isPresent());
        assertEquals("Ayush", result.get().getFirstName());
    }

    @Test
    void shouldCheckEmailExists() {
        Student student = Student.builder()
                .firstName("Ayush")
                .lastName("Yadav")
                .email("ayush@example.com")
                .age(22)
                .build();

        repository.save(student);

        assertTrue(repository.existsByEmail("ayush@example.com"));
    }
}

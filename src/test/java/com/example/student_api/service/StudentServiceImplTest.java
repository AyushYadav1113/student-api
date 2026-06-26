package com.example.student_api.service;

import com.example.student_api.dto.StudentRequest;
import com.example.student_api.dto.StudentResponse;
import com.example.student_api.dto.UpdateStudentRequest;
import com.example.student_api.entity.Student;
import com.example.student_api.exception.DuplicateResourceException;
import com.example.student_api.exception.ResourceNotFoundException;
import com.example.student_api.mapper.StudentMapper;
import com.example.student_api.repository.StudentRepository;
import com.example.student_api.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository repository;

    @Mock
    private StudentMapper mapper;

    @InjectMocks
    private StudentServiceImpl service;

    private Student student;
    private StudentResponse response;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .firstName("Ayush")
                .lastName("Yadav")
                .email("ayush@example.com")
                .age(22)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        response = new StudentResponse(
                1L,
                "Ayush",
                "Yadav",
                "ayush@example.com",
                22,
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }

    @Test
    void shouldCreateStudent() {
        StudentRequest request = new StudentRequest(
                "Ayush", "Yadav", "ayush@example.com", 22);

        when(repository.existsByEmail(request.email())).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(student);
        when(repository.save(student)).thenReturn(student);
        when(mapper.toResponse(student)).thenReturn(response);

        StudentResponse result = service.create(request);

        assertNotNull(result);
        assertEquals("Ayush", result.firstName());
        verify(repository).save(student);
    }

    @Test
    void shouldThrowWhenEmailAlreadyExistsOnCreate() {
        StudentRequest request = new StudentRequest(
                "Ayush", "Yadav", "ayush@example.com", 22);

        when(repository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldGetStudentById() {
        when(repository.findById(1L)).thenReturn(Optional.of(student));
        when(mapper.toResponse(student)).thenReturn(response);

        StudentResponse result = service.getById(1L);

        assertNotNull(result);
        assertEquals("Ayush", result.firstName());
        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowWhenStudentNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void shouldGetAllStudents() {
        when(repository.findAll()).thenReturn(List.of(student));
        when(mapper.toResponse(student)).thenReturn(response);

        List<StudentResponse> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("ayush@example.com", result.getFirst().email());
    }

    @Test
    void shouldUpdateStudent() {
        UpdateStudentRequest request = new UpdateStudentRequest(
                "Ayush", "Kumar", "ayush.kumar@example.com", 23);

        Student updatedStudent = Student.builder()
                .id(1L)
                .firstName("Ayush")
                .lastName("Kumar")
                .email("ayush.kumar@example.com")
                .age(23)
                .build();

        StudentResponse updatedResponse = new StudentResponse(
                1L, "Ayush", "Kumar", "ayush.kumar@example.com", 23, null, null);

        when(repository.findById(1L)).thenReturn(Optional.of(student));
        when(repository.existsByEmail(request.email())).thenReturn(false);
        when(repository.save(student)).thenReturn(updatedStudent);
        when(mapper.toResponse(updatedStudent)).thenReturn(updatedResponse);

        StudentResponse result = service.update(1L, request);

        assertEquals("Kumar", result.lastName());
        verify(mapper).updateEntity(student, request);
    }

    @Test
    void shouldThrowWhenUpdatingMissingStudent() {
        UpdateStudentRequest request = new UpdateStudentRequest(
                "Ayush", "Kumar", "ayush.kumar@example.com", 23);

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, request));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExistsOnUpdate() {
        UpdateStudentRequest request = new UpdateStudentRequest(
                "Ayush", "Kumar", "existing@example.com", 23);

        when(repository.findById(1L)).thenReturn(Optional.of(student));
        when(repository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.update(1L, request));
    }

    @Test
    void shouldDeleteStudent() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingMissingStudent() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
        verify(repository, never()).deleteById(1L);
    }
}

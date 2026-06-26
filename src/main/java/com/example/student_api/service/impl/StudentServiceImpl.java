package com.example.student_api.service.impl;

import com.example.student_api.dto.StudentRequest;
import com.example.student_api.dto.StudentResponse;
import com.example.student_api.dto.UpdateStudentRequest;
import com.example.student_api.entity.Student;
import com.example.student_api.exception.DuplicateResourceException;
import com.example.student_api.exception.ResourceNotFoundException;
import com.example.student_api.mapper.StudentMapper;
import com.example.student_api.repository.StudentRepository;
import com.example.student_api.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;

    @Override
    @Transactional
    public StudentResponse create(StudentRequest request) {
        log.info("Creating student with email={}", request.email());

        if (repository.existsByEmail(request.email())) {
            log.warn("Duplicate email on create: email={}", request.email());
            throw new DuplicateResourceException(
                    "Student with email already exists: " + request.email());
        }

        Student saved = repository.save(mapper.toEntity(request));
        log.info("Student created successfully: id={}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    public List<StudentResponse> getAll() {
        log.info("Fetching all students");
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public StudentResponse getById(Long id) {
        log.info("Fetching student: id={}", id);
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Student not found: id={}", id);
                    return new ResourceNotFoundException("Student not found with id: " + id);
                });
    }

    @Override
    @Transactional
    public StudentResponse update(Long id, UpdateStudentRequest request) {
        log.info("Updating student: id={}", id);

        Student student = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Student not found for update: id={}", id);
                    return new ResourceNotFoundException("Student not found with id: " + id);
                });

        if (!student.getEmail().equals(request.email())
                && repository.existsByEmail(request.email())) {
            log.warn("Duplicate email on update: email={}", request.email());
            throw new DuplicateResourceException(
                    "Student with email already exists: " + request.email());
        }

        mapper.updateEntity(student, request);
        Student updated = repository.save(student);
        log.info("Student updated successfully: id={}", updated.getId());
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting student: id={}", id);

        if (!repository.existsById(id)) {
            log.warn("Student not found for delete: id={}", id);
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }

        repository.deleteById(id);
        log.info("Student deleted successfully: id={}", id);
    }
}

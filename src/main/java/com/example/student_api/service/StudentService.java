package com.example.student_api.service;

import com.example.student_api.dto.StudentRequest;
import com.example.student_api.dto.StudentResponse;
import com.example.student_api.dto.UpdateStudentRequest;

import java.util.List;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    List<StudentResponse> getAll();

    StudentResponse getById(Long id);

    StudentResponse update(Long id, UpdateStudentRequest request);

    void delete(Long id);
}

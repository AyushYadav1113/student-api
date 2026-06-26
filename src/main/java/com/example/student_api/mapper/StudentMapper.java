package com.example.student_api.mapper;

import com.example.student_api.dto.StudentRequest;
import com.example.student_api.dto.StudentResponse;
import com.example.student_api.dto.UpdateStudentRequest;
import com.example.student_api.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toEntity(StudentRequest request) {
        return Student.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .age(request.age())
                .build();
    }

    public StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getAge(),
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }

    public void updateEntity(Student student, UpdateStudentRequest request) {
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setAge(request.age());
    }
}

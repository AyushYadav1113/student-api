package com.example.student_api.controller;

import com.example.student_api.dto.StudentRequest;
import com.example.student_api.dto.StudentResponse;
import com.example.student_api.dto.UpdateStudentRequest;
import com.example.student_api.exception.DuplicateResourceException;
import com.example.student_api.exception.GlobalExceptionHandler;
import com.example.student_api.exception.ResourceNotFoundException;
import com.example.student_api.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@Import(GlobalExceptionHandler.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService service;

    private StudentResponse sampleResponse() {
        return new StudentResponse(
                1L,
                "Ayush",
                "Yadav",
                "ayush@example.com",
                22,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateStudent() throws Exception {
        StudentRequest request = new StudentRequest(
                "Ayush", "Yadav", "ayush@example.com", 22);

        when(service.create(any(StudentRequest.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ayush@example.com"));
    }

    @Test
    void shouldGetAllStudents() throws Exception {
        when(service.getAll()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ayush@example.com"));
    }

    @Test
    void shouldGetStudentById() throws Exception {
        when(service.getById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/v1/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldUpdateStudent() throws Exception {
        UpdateStudentRequest request = new UpdateStudentRequest(
                "Ayush", "Kumar", "ayush.kumar@example.com", 23);

        when(service.update(eq(1L), any(UpdateStudentRequest.class)))
                .thenReturn(new StudentResponse(
                        1L,
                        "Ayush",
                        "Kumar",
                        "ayush.kumar@example.com",
                        23,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ));

        mockMvc.perform(put("/api/v1/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Kumar"));
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/v1/students/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnValidationErrorForInvalidCreateRequest() throws Exception {
        StudentRequest request = new StudentRequest("", "Yadav", "invalid-email", 0);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    void shouldReturnNotFoundWhenStudentMissing() throws Exception {
        when(service.getById(99L))
                .thenThrow(new ResourceNotFoundException("Student not found with id: 99"));

        mockMvc.perform(get("/api/v1/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        StudentRequest request = new StudentRequest(
                "Ayush", "Yadav", "ayush@example.com", 22);

        when(service.create(any(StudentRequest.class)))
                .thenThrow(new DuplicateResourceException("Student with email already exists"));

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }
}

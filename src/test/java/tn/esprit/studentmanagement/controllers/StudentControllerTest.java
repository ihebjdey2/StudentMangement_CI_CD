package tn.esprit.studentmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.services.IStudentService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStudentService studentService; // ✅ interface correcte

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("📋 Récupérer tous les étudiants")
    void testGetAllStudents() throws Exception {
        Student s1 = new Student();
        s1.setIdStudent(1L);
        s1.setFirstName("Iheb");
        s1.setLastName("Jdey");
        s1.setEmail("iheb@example.com");

        when(studentService.getAllStudents()).thenReturn(List.of(s1));

        mockMvc.perform(get("/students/getAllStudents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Iheb"))
                .andExpect(jsonPath("$[0].lastName").value("Jdey"));
    }

    @Test
    @DisplayName("➕ Ajouter un étudiant")
    void testAddStudent() throws Exception {
        Student s = new Student();
        s.setIdStudent(1L);
        s.setFirstName("Ahmed");
        s.setLastName("Ben Ali");
        s.setEmail("ahmed@example.com");

        when(studentService.saveStudent(any(Student.class))).thenReturn(s);

        mockMvc.perform(post("/students/createStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(s)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ahmed"));
    }

    @Test
    @DisplayName("🔍 Trouver un étudiant par ID")
    void testGetStudentById() throws Exception {
        Student s = new Student();
        s.setIdStudent(1L);
        s.setFirstName("Sara");
        s.setEmail("sara@example.com");

        when(studentService.getStudentById(1L)).thenReturn(s);

        mockMvc.perform(get("/students/getStudent/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("sara@example.com"));
    }

    @Test
    @DisplayName("❌ Supprimer un étudiant")
    void testDeleteStudent() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/students/deleteStudent/1"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(1L);
    }
}

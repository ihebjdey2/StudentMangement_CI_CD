package tn.esprit.studentmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.services.IEnrollment;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEnrollment enrollmentService; // ✅ corrige : interface, pas la classe concrète

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("📋 Récupérer toutes les inscriptions")
    void testGetAllEnrollments() throws Exception {
        Enrollment e1 = new Enrollment();
        e1.setIdEnrollment(1L);
        e1.setGrade(18.5);

        when(enrollmentService.getAllEnrollments()).thenReturn(List.of(e1));

        mockMvc.perform(get("/Enrollment/getAllEnrollment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEnrollment").value(1L))
                .andExpect(jsonPath("$[0].grade").value(18.5));
    }

    @Test
    @DisplayName("➕ Ajouter une inscription")
    void testAddEnrollment() throws Exception {
        Enrollment enrollment = new Enrollment();
        enrollment.setIdEnrollment(1L);
        enrollment.setGrade(15.0);

        when(enrollmentService.saveEnrollment(any(Enrollment.class))).thenReturn(enrollment);

        mockMvc.perform(post("/Enrollment/createEnrollment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value(15.0));
    }

    @Test
    @DisplayName("🔍 Trouver une inscription par ID")
    void testGetEnrollmentById() throws Exception {
        Enrollment e = new Enrollment();
        e.setIdEnrollment(2L);
        e.setGrade(17.0);

        when(enrollmentService.getEnrollmentById(2L)).thenReturn(e);

        mockMvc.perform(get("/Enrollment/getEnrollment/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnrollment").value(2L))
                .andExpect(jsonPath("$.grade").value(17.0));
    }

    @Test
    @DisplayName("❌ Supprimer une inscription")
    void testDeleteEnrollment() throws Exception {
        doNothing().when(enrollmentService).deleteEnrollment(1L);

        mockMvc.perform(delete("/Enrollment/deleteEnrollment/1"))
                .andExpect(status().isOk());

        verify(enrollmentService, times(1)).deleteEnrollment(1L);
    }
}

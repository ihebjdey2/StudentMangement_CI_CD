package tn.esprit.studentmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.services.IDepartmentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDepartmentService departmentService; // ✅ corrige : interface correcte

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("📋 Récupérer tous les départements")
    void testGetAllDepartments() throws Exception {
        Department d1 = new Department();
        d1.setIdDepartment(1L);
        d1.setName("Informatique");
        d1.setLocation("Bloc A");

        when(departmentService.getAllDepartments()).thenReturn(List.of(d1));

        mockMvc.perform(get("/Depatment/getAllDepartment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Informatique"))
                .andExpect(jsonPath("$[0].location").value("Bloc A"));
    }

    @Test
    @DisplayName("➕ Ajouter un département")
    void testAddDepartment() throws Exception {
        Department d = new Department();
        d.setIdDepartment(1L);
        d.setName("Mécatronique");
        d.setLocation("Bloc C");

        when(departmentService.saveDepartment(any(Department.class))).thenReturn(d);

        mockMvc.perform(post("/Depatment/createDepartment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mécatronique"))
                .andExpect(jsonPath("$.location").value("Bloc C"));
    }

    @Test
    @DisplayName("🔍 Récupérer un département par ID")
    void testGetDepartmentById() throws Exception {
        Department d = new Department();
        d.setIdDepartment(2L);
        d.setName("Génie Civil");
        d.setLocation("Bloc D");

        when(departmentService.getDepartmentById(2L)).thenReturn(d);

        mockMvc.perform(get("/Depatment/getDepartment/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Génie Civil"))
                .andExpect(jsonPath("$.location").value("Bloc D"));
    }

    @Test
    @DisplayName("❌ Supprimer un département")
    void testDeleteDepartment() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/Depatment/deleteDepartment/1"))
                .andExpect(status().isOk());

        verify(departmentService, times(1)).deleteDepartment(1L);
    }
}

package tn.esprit.studentmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Test d’intégration complet pour le module Department :
 * Controller + Service + Repository (base H2 réelle)
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DepartmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("🧪 Créer un département et vérifier sa persistance")
    void testCreateDepartmentAndRetrieve() throws Exception {
        Department dep = new Department();
        dep.setName("Informatique");
        dep.setLocation("Bloc A");
        dep.setHead("Dr. Majdi");
        dep.setPhone("71234567");

        // 🔹 Appel réel du contrôleur (POST /Depatment/createDepartment)
        mockMvc.perform(post("/Depatment/createDepartment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dep)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Informatique"))
                .andExpect(jsonPath("$.head").value("Dr. Majdi"));

        // 🔹 Vérifie la persistance réelle
        assertThat(departmentRepository.findAll()).hasSize(1);
        assertThat(departmentRepository.findAll().get(0).getLocation()).isEqualTo("Bloc A");
    }

    @Test
    @DisplayName("📋 Récupérer tous les départements")
    void testGetAllDepartments() throws Exception {
        Department d1 = new Department();
        d1.setName("Mécatronique");
        d1.setLocation("Bloc B");
        d1.setHead("Mme. Jaziri");
        departmentRepository.save(d1);

        mockMvc.perform(get("/Depatment/getAllDepartment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mécatronique"))
                .andExpect(jsonPath("$[0].location").value("Bloc B"));
    }

    @Test
    @DisplayName("🔍 Récupérer un département par ID")
    void testGetDepartmentById() throws Exception {
        Department dep = new Department();
        dep.setName("Génie Civil");
        dep.setLocation("Bloc C");
        dep.setHead("Mr. Fakhfakh");
        Department saved = departmentRepository.save(dep);

        mockMvc.perform(get("/Depatment/getDepartment/" + saved.getIdDepartment()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Génie Civil"))
                .andExpect(jsonPath("$.head").value("Mr. Fakhfakh"));
    }

    @Test
    @DisplayName("❌ Supprimer un département")
    void testDeleteDepartment() throws Exception {
        Department dep = new Department();
        dep.setName("Finance");
        dep.setLocation("Bloc D");
        dep.setHead("Mme. Trabelsi");
        Department saved = departmentRepository.save(dep);

        // 🔹 Appel de la suppression
        mockMvc.perform(delete("/Depatment/deleteDepartment/" + saved.getIdDepartment()))
                .andExpect(status().isOk());

        // 🔹 Vérifie la suppression en base
        assertThat(departmentRepository.findAll()).isEmpty();
    }
}

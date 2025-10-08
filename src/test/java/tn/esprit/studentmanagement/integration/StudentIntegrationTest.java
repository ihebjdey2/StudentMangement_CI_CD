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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Test d’intégration complet du module Student :
 * Controller + Service + Repository (base H2 en mémoire)
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase // force l'utilisation de la DB H2
@ActiveProfiles("test") // ⚙️ Active le fichier application-test.properties
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StudentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // =============================
    // 🔹 Test 1 : Création & persistance
    // =============================
    @Test
    @DisplayName("🧪 Créer un étudiant et vérifier sa persistance")
    void testCreateStudentAndRetrieve() throws Exception {
        Student student = new Student();
        student.setFirstName("Iheb");
        student.setLastName("Jdey");
        student.setEmail("iheb@example.com");

        // Envoi de la requête POST
        mockMvc.perform(post("/students/createStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Iheb"))
                .andExpect(jsonPath("$.email").value("iheb@example.com"));

        // Vérification base H2
        assertThat(studentRepository.count()).isEqualTo(1);
        Student saved = studentRepository.findAll().get(0);
        assertThat(saved.getFirstName()).isEqualTo("Iheb");
        assertThat(saved.getEmail()).isEqualTo("iheb@example.com");
    }

    // =============================
    // 🔹 Test 2 : Lecture de tous les étudiants
    // =============================
    @Test
    @DisplayName("📋 Récupérer tous les étudiants via l’API")
    void testGetAllStudents() throws Exception {
        Student s1 = new Student();
        s1.setFirstName("Sara");
        s1.setLastName("Ben Ali");
        s1.setEmail("sara@example.com");
        studentRepository.save(s1);

        mockMvc.perform(get("/students/getAllStudents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Sara"))
                .andExpect(jsonPath("$[0].email").value("sara@example.com"));

        assertThat(studentRepository.count()).isEqualTo(1);
    }

    // =============================
    // 🔹 Test 3 : Lecture par ID
    // =============================
    @Test
    @DisplayName("🔍 Récupérer un étudiant par ID")
    void testGetStudentById() throws Exception {
        Student s = new Student();
        s.setFirstName("Youssef");
        s.setLastName("Trabelsi");
        s.setEmail("youssef@example.com");
        Student saved = studentRepository.save(s);

        mockMvc.perform(get("/students/getStudent/" + saved.getIdStudent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("youssef@example.com"))
                .andExpect(jsonPath("$.firstName").value("Youssef"));
    }

    // =============================
    // 🔹 Test 4 : Suppression d’un étudiant
    // =============================
    @Test
    @DisplayName("❌ Supprimer un étudiant et vérifier suppression")
    void testDeleteStudent() throws Exception {
        Student s = new Student();
        s.setFirstName("Ahmed");
        s.setLastName("Hammami");
        s.setEmail("ahmed@example.com");
        Student saved = studentRepository.save(s);

        mockMvc.perform(delete("/students/deleteStudent/" + saved.getIdStudent()))
                .andExpect(status().isOk());

        assertThat(studentRepository.findAll()).isEmpty();
    }
}

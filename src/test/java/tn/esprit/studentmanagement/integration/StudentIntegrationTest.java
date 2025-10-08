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
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Test d’intégration complet du module Student :
 * Controller + Service + Repository (base H2)
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase // utilise automatiquement H2
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StudentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("🧪 Créer un étudiant et vérifier sa persistance")
    void testCreateStudentAndRetrieve() throws Exception {
        // ✅ Création d’un étudiant
        Student student = new Student();
        student.setFirstName("Iheb");
        student.setLastName("Jdey");
        student.setEmail("iheb@example.com");

        // 🔹 Appel réel du contrôleur (POST /students/createStudent)
        mockMvc.perform(post("/students/createStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Iheb"))
                .andExpect(jsonPath("$.email").value("iheb@example.com"));

        // 🔹 Vérification réelle en base H2
        assertThat(studentRepository.findAll()).hasSize(1);
        assertThat(studentRepository.findAll().get(0).getFirstName()).isEqualTo("Iheb");
    }

    @Test
    @DisplayName("📋 Récupérer tous les étudiants via l’API")
    void testGetAllStudents() throws Exception {
        // Prépare des données dans H2
        Student s1 = new Student();
        s1.setFirstName("Sara");
        s1.setLastName("Ben Ali");
        s1.setEmail("sara@example.com");
        studentRepository.save(s1);

        // 🔹 Appel du contrôleur
        mockMvc.perform(get("/students/getAllStudents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Sara"))
                .andExpect(jsonPath("$[0].email").value("sara@example.com"));
    }

    @Test
    @DisplayName("🔍 Récupérer un étudiant par ID")
    void testGetStudentById() throws Exception {
        // Prépare un étudiant dans H2
        Student s = new Student();
        s.setFirstName("Youssef");
        s.setLastName("Trabelsi");
        s.setEmail("youssef@example.com");
        Student saved = studentRepository.save(s);

        // 🔹 Appel réel du contrôleur
        mockMvc.perform(get("/students/getStudent/" + saved.getIdStudent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("youssef@example.com"))
                .andExpect(jsonPath("$.firstName").value("Youssef"));
    }

    @Test
    @DisplayName("❌ Supprimer un étudiant")
    void testDeleteStudent() throws Exception {
        // Prépare un étudiant dans H2
        Student s = new Student();
        s.setFirstName("Ahmed");
        s.setLastName("Hammami");
        s.setEmail("ahmed@example.com");
        Student saved = studentRepository.save(s);

        // 🔹 Appel du contrôleur pour supprimer
        mockMvc.perform(delete("/students/deleteStudent/" + saved.getIdStudent()))
                .andExpect(status().isOk());

        // 🔹 Vérifie que la suppression est effective
        assertThat(studentRepository.findAll()).isEmpty();
    }
}

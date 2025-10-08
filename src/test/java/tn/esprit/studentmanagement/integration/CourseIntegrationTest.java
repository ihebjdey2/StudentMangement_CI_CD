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
import tn.esprit.studentmanagement.entities.Course;
import tn.esprit.studentmanagement.repositories.CourseRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Test d’intégration complet pour le module Course :
 * Controller + Service + Repository (base H2 réelle)
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("🧪 Créer un cours et vérifier sa persistance")
    void testCreateCourseAndRetrieve() throws Exception {
        Course course = new Course();
        course.setName("DevOps Avancé");
        course.setCode("DV200");
        course.setCredit(5);
        course.setDescription("Cours sur l'intégration continue et le déploiement continu.");

        // 🔹 Appel du contrôleur
        mockMvc.perform(post("/Course/createCourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("DevOps Avancé"))
                .andExpect(jsonPath("$.code").value("DV200"));

        // 🔹 Vérification en base
        assertThat(courseRepository.findAll()).hasSize(1);
        assertThat(courseRepository.findAll().get(0).getCredit()).isEqualTo(5);
    }

    @Test
    @DisplayName("📋 Récupérer tous les cours")
    void testGetAllCourses() throws Exception {
        Course c1 = new Course();
        c1.setName("Spring Boot");
        c1.setCode("SB100");
        c1.setCredit(4);
        c1.setDescription("Introduction à Spring Boot.");
        courseRepository.save(c1);

        mockMvc.perform(get("/Course/getAllCourse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Spring Boot"))
                .andExpect(jsonPath("$[0].code").value("SB100"));
    }

    @Test
    @DisplayName("🔍 Récupérer un cours par ID")
    void testGetCourseById() throws Exception {
        Course c = new Course();
        c.setName("Microservices");
        c.setCode("MS500");
        c.setCredit(6);
        c.setDescription("Architecture microservices et Docker.");
        Course saved = courseRepository.save(c);

        mockMvc.perform(get("/Course/getCourse/" + saved.getIdCourse()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Microservices"))
                .andExpect(jsonPath("$.credit").value(6));
    }

    @Test
    @DisplayName("❌ Supprimer un cours")
    void testDeleteCourse() throws Exception {
        Course c = new Course();
        c.setName("Intelligence Artificielle");
        c.setCode("AI300");
        c.setCredit(5);
        c.setDescription("Introduction à l'IA et au Machine Learning.");
        Course saved = courseRepository.save(c);

        // 🔹 Suppression via API
        mockMvc.perform(delete("/Course/deleteCourse/" + saved.getIdCourse()))
                .andExpect(status().isOk());

        // 🔹 Vérifie la suppression
        assertThat(courseRepository.findAll()).isEmpty();
    }
}

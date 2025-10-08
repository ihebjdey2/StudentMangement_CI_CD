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
import tn.esprit.studentmanagement.entities.Course;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.entities.Status;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.CourseRepository;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Test d’intégration complet pour le module Enrollment :
 * Compatible avec @RequestMapping("/Enrollment") (avec majuscule)
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase // force H2 en mémoire
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EnrollmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ============================
    // 🔹 Test 1 : Création
    // ============================
    @Test
    @DisplayName("🧪 Créer une inscription et vérifier sa persistance")
    void testCreateEnrollmentAndRetrieve() throws Exception {
        // ✅ Préparer un étudiant et un cours dans la base H2
        Student s = new Student();
        s.setFirstName("Iheb");
        s.setLastName("Jdey");
        s.setEmail("iheb@example.com");
        s = studentRepository.save(s);

        Course c = new Course();
        c.setName("DevOps");
        c.setCode("DV101");
        c.setCredit(4);
        c = courseRepository.save(c);

        // ✅ Créer une inscription
        Enrollment e = new Enrollment();
        e.setGrade(18.5);
        e.setStatus(Status.ACTIVE);
        e.setEnrollmentDate(LocalDate.now());
        e.setStudent(s);
        e.setCourse(c);

        // 🔹 Appel réel du contrôleur
        mockMvc.perform(post("/Enrollment/createEnrollment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(e)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value(18.5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // 🔹 Vérification en base
        assertThat(enrollmentRepository.count()).isEqualTo(1);
        Enrollment saved = enrollmentRepository.findAll().get(0);
        assertThat(saved.getStudent().getFirstName()).isEqualTo("Iheb");
        assertThat(saved.getCourse().getName()).isEqualTo("DevOps");
    }

    // ============================
    // 🔹 Test 2 : Lecture (toutes)
    // ============================
    @Test
    @DisplayName("📋 Récupérer toutes les inscriptions via l’API")
    void testGetAllEnrollments() throws Exception {
        Student s = new Student();
        s.setFirstName("Sara");
        s.setLastName("Ben Ali");
        s.setEmail("sara@example.com");
        s = studentRepository.save(s);

        Course c = new Course();
        c.setName("Spring Boot");
        c.setCode("SB202");
        c.setCredit(3);
        c = courseRepository.save(c);

        Enrollment e = new Enrollment();
        e.setGrade(16.0);
        e.setStatus(Status.COMPLETED);
        e.setEnrollmentDate(LocalDate.now());
        e.setStudent(s);
        e.setCourse(c);
        enrollmentRepository.save(e);

        mockMvc.perform(get("/Enrollment/getAllEnrollment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].grade").value(16.0))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));

        assertThat(enrollmentRepository.count()).isEqualTo(1);
    }

    // ============================
    // 🔹 Test 3 : Lecture par ID
    // ============================
    @Test
    @DisplayName("🔍 Récupérer une inscription par ID")
    void testGetEnrollmentById() throws Exception {
        Student s = new Student();
        s.setFirstName("Youssef");
        s.setLastName("Trabelsi");
        s.setEmail("youssef@example.com");
        s = studentRepository.save(s);

        Course c = new Course();
        c.setName("Architecture Logicielle");
        c.setCode("AR300");
        c.setCredit(5);
        c = courseRepository.save(c);

        Enrollment e = new Enrollment();
        e.setGrade(14.5);
        e.setStatus(Status.ACTIVE);
        e.setEnrollmentDate(LocalDate.now());
        e.setStudent(s);
        e.setCourse(c);
        Enrollment saved = enrollmentRepository.save(e);

        mockMvc.perform(get("/Enrollment/getEnrollment/" + saved.getIdEnrollment()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value(14.5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    // ============================
    // 🔹 Test 4 : Suppression
    // ============================
    @Test
    @DisplayName("❌ Supprimer une inscription et vérifier suppression")
    void testDeleteEnrollment() throws Exception {
        Student s = new Student();
        s.setFirstName("Amira");
        s.setLastName("Hammami");
        s.setEmail("amira@example.com");
        s = studentRepository.save(s);

        Course c = new Course();
        c.setName("Microservices");
        c.setCode("MS400");
        c.setCredit(6);
        c = courseRepository.save(c);

        Enrollment e = new Enrollment();
        e.setGrade(19.0);
        e.setStatus(Status.COMPLETED);
        e.setEnrollmentDate(LocalDate.now());
        e.setStudent(s);
        e.setCourse(c);
        Enrollment saved = enrollmentRepository.save(e);

        mockMvc.perform(delete("/Enrollment/deleteEnrollment/" + saved.getIdEnrollment()))
                .andExpect(status().isOk());

        assertThat(enrollmentRepository.findAll()).isEmpty();
    }
}

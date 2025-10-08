package tn.esprit.studentmanagement.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tn.esprit.studentmanagement.entities.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @DisplayName("✅ Enregistrer une inscription avec étudiant et cours")
    void testSaveEnrollment() {
        // 🔹 Création du cours
        Course course = new Course();
        course.setName("Développement Java");
        course.setCode("JAVA101");
        course.setCredit(4);
        courseRepository.save(course);

        // 🔹 Création de l’étudiant
        Student student = new Student();
        student.setFirstName("Iheb");
        student.setLastName("Jdey");
        student.setEmail("iheb.jdey@example.com");
        student.setPhone("98765432");
        studentRepository.save(student);

        // 🔹 Création de l’inscription
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(Status.ACTIVE);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setGrade(15.5);

        Enrollment saved = enrollmentRepository.save(enrollment);

        assertThat(saved.getIdEnrollment()).isNotNull();
        assertThat(saved.getCourse().getName()).isEqualTo("Développement Java");
        assertThat(saved.getStudent().getFirstName()).isEqualTo("Iheb");
        assertThat(saved.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("📋 Récupérer toutes les inscriptions")
    void testFindAllEnrollments() {
        List<Enrollment> list = enrollmentRepository.findAll();
        assertThat(list).isNotNull();
    }

    @Test
    @DisplayName("🔍 Trouver une inscription par ID")
    void testFindEnrollmentById() {
        Course course = new Course();
        course.setName("Mathématiques");
        courseRepository.save(course);

        Student student = new Student();
        student.setFirstName("Sami");
        studentRepository.save(student);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(Status.COMPLETED);
        enrollmentRepository.save(enrollment);

        Optional<Enrollment> found = enrollmentRepository.findById(enrollment.getIdEnrollment());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    @DisplayName("❌ Supprimer une inscription")
    void testDeleteEnrollment() {
        Course course = new Course();
        course.setName("Physique");
        courseRepository.save(course);

        Student student = new Student();
        student.setFirstName("Mouna");
        studentRepository.save(student);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(Status.DROPPED);
        Enrollment saved = enrollmentRepository.save(enrollment);

        enrollmentRepository.delete(saved);

        Optional<Enrollment> found = enrollmentRepository.findById(saved.getIdEnrollment());
        assertThat(found).isEmpty();
    }
}

package tn.esprit.studentmanagement.repositories;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tn.esprit.studentmanagement.entities.Course;
import tn.esprit.studentmanagement.repositories.CourseRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @DisplayName("✅ Enregistrer un cours")
    void testSaveCourse() {
        Course course = new Course();
        course.setName("Spring Boot Avancé");
        course.setCode("SB-2025");
        course.setCredit(6);
        course.setDescription("Formation approfondie sur Spring Boot et JPA");

        Course saved = courseRepository.save(course);

        assertThat(saved.getIdCourse()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Spring Boot Avancé");
        assertThat(saved.getCode()).isEqualTo("SB-2025");
    }

    @Test
    @DisplayName("📋 Lister tous les cours")
    void testFindAllCourses() {
        Course c1 = new Course();
        c1.setName("Java SE");
        courseRepository.save(c1);

        Course c2 = new Course();
        c2.setName("Python");
        courseRepository.save(c2);

        List<Course> list = courseRepository.findAll();
        assertThat(list).isNotEmpty();
        assertThat(list.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("🔍 Trouver un cours par ID")
    void testFindById() {
        Course course = new Course();
        course.setName("Angular Framework");
        courseRepository.save(course);

        Optional<Course> found = courseRepository.findById(course.getIdCourse());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Angular Framework");
    }

    @Test
    @DisplayName("✏️ Mettre à jour un cours")
    void testUpdateCourse() {
        Course course = new Course();
        course.setName("C++ Initiation");
        course.setCredit(3);
        courseRepository.save(course);

        course.setCredit(5);
        Course updated = courseRepository.save(course);

        assertThat(updated.getCredit()).isEqualTo(5);
    }

    @Test
    @DisplayName("❌ Supprimer un cours")
    void testDeleteCourse() {
        Course course = new Course();
        course.setName("Kotlin Android");
        courseRepository.save(course);

        courseRepository.delete(course);
        Optional<Course> found = courseRepository.findById(course.getIdCourse());

        assertThat(found).isEmpty();
    }
}

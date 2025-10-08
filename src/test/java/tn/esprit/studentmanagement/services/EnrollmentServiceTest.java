package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    @DisplayName("📋 Récupérer toutes les inscriptions")
    void testGetAllEnrollments() {
        Enrollment e1 = new Enrollment();
        e1.setIdEnrollment(1L);

        Enrollment e2 = new Enrollment();
        e2.setIdEnrollment(2L);

        when(enrollmentRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

        List<Enrollment> result = enrollmentService.getAllEnrollments();

        assertThat(result).hasSize(2);
        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("🔍 Trouver une inscription par ID")
    void testGetEnrollmentById() {
        Enrollment enrollment = new Enrollment();
        enrollment.setIdEnrollment(5L);

        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));

        Enrollment found = enrollmentService.getEnrollmentById(5L);

        assertThat(found).isNotNull();
        assertThat(found.getIdEnrollment()).isEqualTo(5L);
        verify(enrollmentRepository, times(1)).findById(5L);
    }

    @Test
    @DisplayName("✅ Créer une nouvelle inscription")
    void testSaveEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setGrade(15.5); // ✅ Remplacé float → Double

        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);

        Enrollment saved = enrollmentService.saveEnrollment(enrollment);

        assertThat(saved.getGrade()).isEqualTo(15.5f);
        verify(enrollmentRepository, times(1)).save(enrollment);
    }

    @Test
    @DisplayName("❌ Supprimer une inscription")
    void testDeleteEnrollment() {
        Long id = 3L;
        doNothing().when(enrollmentRepository).deleteById(id);

        enrollmentService.deleteEnrollment(id);

        verify(enrollmentRepository, times(1)).deleteById(id);
    }
}

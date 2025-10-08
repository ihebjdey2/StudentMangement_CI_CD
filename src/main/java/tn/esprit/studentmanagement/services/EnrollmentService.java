package tn.esprit.studentmanagement.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;
import tn.esprit.studentmanagement.entities.Enrollment;

import java.util.List;

@Service
@AllArgsConstructor // ✅ Lombok crée un constructeur pour injection
public class EnrollmentService implements IEnrollment {

    private final EnrollmentRepository enrollmentRepository; // ✅ final + sans @Autowired

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Enrollment getEnrollmentById(Long idEnrollment) {
        // ✅ meilleure pratique : orElse(null) pour éviter NoSuchElementException
        return enrollmentRepository.findById(idEnrollment).orElse(null);
    }

    @Override
    public Enrollment saveEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public void deleteEnrollment(Long idEnrollment) {
        enrollmentRepository.deleteById(idEnrollment);
    }
}

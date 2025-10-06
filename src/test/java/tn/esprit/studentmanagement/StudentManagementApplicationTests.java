package tn.esprit.studentmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test basique pour s'assurer que le contexte Spring démarre
 * avec la configuration H2 en mémoire.
 */
@SpringBootTest(properties = "spring.profiles.active=test")
class StudentManagementApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("✅ Test exécuté avec succès sur la base H2 (profil test).");
    }
}

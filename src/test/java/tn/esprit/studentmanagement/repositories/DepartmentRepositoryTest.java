package tn.esprit.studentmanagement.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    @DisplayName("✅ Enregistrement et recherche d’un département")
    void testSaveAndFindDepartment() {
        Department dep = new Department();
        dep.setName("Informatique");
        dep.setLocation("Bloc A");
        dep.setPhone("71234567");
        dep.setHead("Dr. Karim");

        Department saved = departmentRepository.save(dep);

        Optional<Department> found = departmentRepository.findById(saved.getIdDepartment());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Informatique");
        assertThat(found.get().getHead()).isEqualTo("Dr. Karim");
    }

    @Test
    @DisplayName("📋 Récupération de tous les départements")
    void testFindAllDepartments() {
        Department dep1 = new Department();
        dep1.setName("Mathématiques");
        Department dep2 = new Department();
        dep2.setName("Physique");

        departmentRepository.save(dep1);
        departmentRepository.save(dep2);

        List<Department> deps = departmentRepository.findAll();
        assertThat(deps).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("❌ Suppression d’un département")
    void testDeleteDepartment() {
        Department dep = new Department();
        dep.setName("Chimie");
        Department saved = departmentRepository.save(dep);

        departmentRepository.delete(saved);

        Optional<Department> found = departmentRepository.findById(saved.getIdDepartment());
        assertThat(found).isEmpty();
    }
}

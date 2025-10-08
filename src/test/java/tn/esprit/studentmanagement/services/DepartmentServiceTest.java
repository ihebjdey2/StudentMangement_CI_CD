package tn.esprit.studentmanagement.services;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;
import tn.esprit.studentmanagement.services.DepartmentService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    
    @Test
    @DisplayName("📋 Lister tous les départements")
    void testGetAllDepartments() {
        Department d1 = new Department();
        d1.setName("Finance");

        Department d2 = new Department();
        d2.setName("Mathématiques");

        when(departmentRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        List<Department> list = departmentService.getAllDepartments();

        assertThat(list).hasSize(2);
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("🔍 Trouver un département par ID")
    void testFindDepartmentById() {
        Department dept = new Department();
        dept.setIdDepartment(1L);
        dept.setName("Réseaux");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));

        Department found = departmentService.getDepartmentById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Réseaux");
        verify(departmentRepository, times(1)).findById(1L);
    }

    
    @Test
    @DisplayName("❌ Supprimer un département")
    void testDeleteDepartment() {
        Long id = 2L;
        doNothing().when(departmentRepository).deleteById(id);

        departmentService.deleteDepartment(id);

        verify(departmentRepository, times(1)).deleteById(id);
    }
}

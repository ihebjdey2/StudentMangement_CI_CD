package tn.esprit.studentmanagement.services;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;
import tn.esprit.studentmanagement.services.StudentService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("📋 Récupérer tous les étudiants")
    void testGetAllStudents() {
        Student s1 = new Student();
        s1.setFirstName("Sami");
        Student s2 = new Student();
        s2.setFirstName("Mouna");

        when(studentRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        List<Student> students = studentService.getAllStudents();

        assertThat(students).hasSize(2);
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("🔍 Trouver un étudiant par ID")
    void testGetStudentById() {
        Student student = new Student();
        student.setIdStudent(1L);
        student.setFirstName("Youssef");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student found = studentService.getStudentById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("Youssef");
        verify(studentRepository, times(1)).findById(1L);
    }

    
    @Test
    @DisplayName("❌ Supprimer un étudiant")
    void testDeleteStudent() {
        Long id = 10L;
        doNothing().when(studentRepository).deleteById(id);

        studentService.deleteStudent(id);

        verify(studentRepository, times(1)).deleteById(id);
    }
}

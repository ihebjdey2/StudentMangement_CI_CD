package tn.esprit.studentmanagement.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tn.esprit.studentmanagement.entities.Student;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // ✅ Lance un test avec H2 et les repositories Spring Data
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testSaveStudent() {
        Student student = new Student();
        student.setFirstName("Iheb");
        student.setLastName("Jdey");
        student.setEmail("iheb@example.com");
        student.setPhone("123456789");
        student.setAddress("Tunis");

        Student saved = studentRepository.save(student);

        assertThat(saved.getIdStudent()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Iheb");
    }

    @Test
    void testFindById() {
        Student student = new Student();
        student.setFirstName("Ahmed");
        student.setLastName("Ben Ali");
        student.setEmail("ahmed@example.com");
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findById(student.getIdStudent());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("ahmed@example.com");
    }

    @Test
    void testFindAll() {
        Student s1 = new Student();
        s1.setFirstName("Ali");
        studentRepository.save(s1);

        Student s2 = new Student();
        s2.setFirstName("Sana");
        studentRepository.save(s2);

        List<Student> students = studentRepository.findAll();

        assertThat(students.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testDeleteStudent() {
        Student student = new Student();
        student.setFirstName("Youssef");
        studentRepository.save(student);

        studentRepository.delete(student);

        Optional<Student> found = studentRepository.findById(student.getIdStudent());
        assertThat(found).isEmpty();
    }
}

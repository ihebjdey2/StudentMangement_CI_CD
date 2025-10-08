package tn.esprit.studentmanagement.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;

import java.util.List;

@Service
@AllArgsConstructor // ✅ Injection propre par constructeur
public class DepartmentService implements IDepartmentService {

    private final DepartmentRepository departmentRepository; // ✅ plus d'@Autowired, champ final

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Long idDepartment) {
        // ✅ évite le crash si l’ID n’existe pas
        return departmentRepository.findById(idDepartment).orElse(null);
    }

    @Override
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(Long idDepartment) {
        departmentRepository.deleteById(idDepartment);
    }
}

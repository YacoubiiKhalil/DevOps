package tn.esprit.studentmanagement.services;

import org.springframework.stereotype.Service;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.exceptions.DepartmentNotFoundException;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;

import java.util.List;

@Service
public class DepartmentService implements IDepartmentService {

    private final DepartmentRepository departmentRepository;

    // ✅ Injection par constructeur (au lieu de @Autowired sur le champ)
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Long idDepartment) {
        // ✅ Vérification de l'Optional avant d'accéder à la valeur
        return departmentRepository.findById(idDepartment)
                .orElseThrow(() -> new DepartmentNotFoundException(
                        "Department not found with id: " + idDepartment
                ));
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
package tn.esprit.studentmanagement.services;

import org.springframework.stereotype.Service;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.exceptions.EnrollmentNotFoundException;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;

import java.util.List;

@Service
public class EnrollmentService implements IEnrollment {

    private final EnrollmentRepository enrollmentRepository;

    // ✅ Injection par constructeur (déjà correct)
    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Enrollment getEnrollmentById(Long idEnrollment) {
        // ✅ Exception spécifique au lieu de RuntimeException
        return enrollmentRepository.findById(idEnrollment)
                .orElseThrow(() -> new EnrollmentNotFoundException(
                        "Enrollment not found with id: " + idEnrollment
                ));
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
package tn.esprit.studentmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.studentmanagement.entities.Student; // ← CORRECTION

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> { // ← CORRECT
    // Méthodes spécifiques pour Student...
}
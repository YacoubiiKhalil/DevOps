package tn.esprit.studentmanagement.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTest {

    @Test
    void testEnrollmentCreation() {
        // 1. Créer une inscription
        Enrollment enrollment = new Enrollment();

        // 2. Donner des valeurs
        enrollment.setIdEnrollment(1L);
        enrollment.setEnrollmentDate(LocalDate.of(2024, 1, 15));
        enrollment.setGrade(15.5);
        enrollment.setStatus(Status.ACTIVE);

        // Créer Student avec quelques valeurs
        Student student = new Student();
        student.setIdStudent(100L);
        student.setFirstName("Mohamed");
        student.setLastName("Ali");

        // Créer Course avec le bon setter : setName() pas setCourseName()
        Course course = new Course();
        course.setIdCourse(200L);
        course.setName("Java Programming");  // CORRECTION ICI !
        course.setCode("CS101");
        course.setCredit(3);

        enrollment.setStudent(student);
        enrollment.setCourse(course);

        // 3. Vérifier les valeurs
        assertEquals(1L, enrollment.getIdEnrollment());
        assertEquals(LocalDate.of(2024, 1, 15), enrollment.getEnrollmentDate());
        assertEquals(15.5, enrollment.getGrade());
        assertEquals(Status.ACTIVE, enrollment.getStatus());

        // Vérifier les relations
        assertEquals(student, enrollment.getStudent());
        assertEquals(100L, enrollment.getStudent().getIdStudent());

        assertEquals(course, enrollment.getCourse());
        assertEquals(200L, enrollment.getCourse().getIdCourse());
        assertEquals("Java Programming", enrollment.getCourse().getName());
    }

    @Test
    void testAllArgsConstructor() {
        // Créer les objets associés
        Student student = new Student();
        student.setIdStudent(101L);
        student.setFirstName("Fatima");

        Course course = new Course();
        course.setIdCourse(201L);
        course.setName("Database Systems");

        // Utiliser le constructeur avec tous les paramètres
        Enrollment enrollment = new Enrollment(
                2L,
                LocalDate.of(2024, 2, 20),
                18.0,
                Status.COMPLETED,
                student,
                course
        );

        // Vérifier
        assertEquals(2L, enrollment.getIdEnrollment());
        assertEquals(LocalDate.of(2024, 2, 20), enrollment.getEnrollmentDate());
        assertEquals(18.0, enrollment.getGrade());
        assertEquals(Status.COMPLETED, enrollment.getStatus());
        assertEquals("Fatima", enrollment.getStudent().getFirstName());
        assertEquals("Database Systems", enrollment.getCourse().getName());
    }

    @Test
    void testGradeBoundaries() {
        Enrollment enrollment = new Enrollment();

        // Test avec différentes notes
        enrollment.setGrade(0.0);
        assertEquals(0.0, enrollment.getGrade());

        enrollment.setGrade(20.0);
        assertEquals(20.0, enrollment.getGrade());

        enrollment.setGrade(10.5);
        assertEquals(10.5, enrollment.getGrade());
    }

    @Test
    void testStatusValues() {
        Enrollment enrollment = new Enrollment();

        // Tester tous les statuts
        enrollment.setStatus(Status.ACTIVE);
        assertEquals(Status.ACTIVE, enrollment.getStatus());

        enrollment.setStatus(Status.COMPLETED);
        assertEquals(Status.COMPLETED, enrollment.getStatus());

        enrollment.setStatus(Status.DROPPED);
        assertEquals(Status.DROPPED, enrollment.getStatus());
    }

    @Test
    void testToStringContainsInformation() {
        Enrollment enrollment = new Enrollment();
        enrollment.setIdEnrollment(5L);
        enrollment.setGrade(16.75);

        Student student = new Student();
        student.setFirstName("Karim");
        enrollment.setStudent(student);

        Course course = new Course();
        course.setName("Web Development");
        enrollment.setCourse(course);

        String result = enrollment.toString();

        assertNotNull(result);
        // Le toString devrait contenir des infos importantes
        assertTrue(result.contains("Enrollment") ||
                result.contains("5") ||
                result.contains("16.75"));
    }
}
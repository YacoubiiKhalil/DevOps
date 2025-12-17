package tn.esprit.studentmanagement.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void testStudentSimple() {
        // 1. Créer un étudiant
        Student student = new Student();

        // 2. Mettre des valeurs
        student.setIdStudent(100L);
        student.setFirstName("Karim");
        student.setLastName("Ben Salah");
        student.setEmail("karim@esprit.tn");
        student.setPhone("55112233");
        student.setDateOfBirth(LocalDate.of(1999, 7, 20));
        student.setAddress("Nabeul, Tunisie");

        // 3. Vérifier les valeurs
        assertEquals(100L, student.getIdStudent());
        assertEquals("Karim", student.getFirstName());
        assertEquals("Ben Salah", student.getLastName());
        assertEquals("karim@esprit.tn", student.getEmail());
        assertEquals("55112233", student.getPhone());
        assertEquals(LocalDate.of(1999, 7, 20), student.getDateOfBirth());
        assertEquals("Nabeul, Tunisie", student.getAddress());
    }

    @Test
    void testToString() {
        Student student = new Student();
        student.setIdStudent(200L);
        student.setFirstName("Test");
        student.setLastName("User");

        String result = student.toString();

        assertNotNull(result);
        assertTrue(result.contains("Test"));
        assertTrue(result.contains("User"));
    }

    @Test
    void testAllArgsConstructor() {
        // Test avec le constructeur complet
        Student student = new Student(
                300L,
                "Ahmed",
                "Ben Ali",
                "ahmed@esprit.tn",
                "99887766",
                LocalDate.of(2000, 1, 1),
                "Tunis",
                null,
                null
        );

        assertEquals(300L, student.getIdStudent());
        assertEquals("Ahmed", student.getFirstName());
        assertEquals("Ben Ali", student.getLastName());
    }
}
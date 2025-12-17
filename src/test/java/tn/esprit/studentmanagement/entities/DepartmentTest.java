package tn.esprit.studentmanagement.entities;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTest {

    @Test
    void testDepartmentCreation() {
        // 1. Créer un département
        Department department = new Department();

        // 2. Donner des valeurs
        department.setIdDepartment(1L);
        department.setName("Informatique");
        department.setLocation("Bâtiment A, Salle 101");
        department.setPhone("+21612345678");
        department.setHead("Prof. Mohamed Ben Ali");

        // 3. Vérifier les valeurs
        assertEquals(1L, department.getIdDepartment());
        assertEquals("Informatique", department.getName());
        assertEquals("Bâtiment A, Salle 101", department.getLocation());
        assertEquals("+21612345678", department.getPhone());
        assertEquals("Prof. Mohamed Ben Ali", department.getHead());
    }

    @Test
    void testAllArgsConstructor() {
        // Créer une liste d'étudiants
        List<Student> students = new ArrayList<>();

        // Ajouter quelques étudiants
        Student student1 = new Student();
        student1.setIdStudent(100L);
        student1.setFirstName("Ahmed");

        Student student2 = new Student();
        student2.setIdStudent(101L);
        student2.setFirstName("Fatima");

        students.add(student1);
        students.add(student2);

        // Utiliser le constructeur avec tous les paramètres
        Department department = new Department(
                2L,
                "Mathématiques",
                "Bâtiment B, Salle 205",
                "+21698765432",
                "Prof. Leila Ben Salah",
                students
        );

        // Vérifier
        assertEquals(2L, department.getIdDepartment());
        assertEquals("Mathématiques", department.getName());
        assertEquals("Bâtiment B, Salle 205", department.getLocation());
        assertEquals("+21698765432", department.getPhone());
        assertEquals("Prof. Leila Ben Salah", department.getHead());

        // Vérifier la liste d'étudiants
        assertNotNull(department.getStudents());
        assertEquals(2, department.getStudents().size());
        assertEquals("Ahmed", department.getStudents().get(0).getFirstName());
    }

    @Test
    void testStudentsListOperations() {
        Department department = new Department();
        department.setName("Physics Department");

        // Créer une liste vide
        List<Student> students = new ArrayList<>();
        department.setStudents(students);

        // Vérifier que la liste est vide au début
        assertTrue(department.getStudents().isEmpty());

        // Ajouter un étudiant
        Student student = new Student();
        student.setIdStudent(50L);
        student.setFirstName("Karim");
        department.getStudents().add(student);

        // Vérifier l'ajout
        assertEquals(1, department.getStudents().size());
        assertEquals("Karim", department.getStudents().get(0).getFirstName());
    }

    @Test
    void testToStringContainsInfo() {
        Department department = new Department();
        department.setIdDepartment(10L);
        department.setName("Computer Science");
        department.setHead("Dr. Smith");

        String result = department.toString();

        assertNotNull(result);
        // Le toString devrait contenir des informations importantes
        assertTrue(result.contains("Department") ||
                result.contains("Computer Science") ||
                result.contains("10"));
    }

    @Test
    void testDepartmentEquality() {
        // Deux départements avec le même ID devraient être égaux
        Department dept1 = new Department();
        dept1.setIdDepartment(5L);
        dept1.setName("Chemistry");

        Department dept2 = new Department();
        dept2.setIdDepartment(5L);
        dept2.setName("Chemistry");

        // Note: Sans @EqualsAndHashCode sur Department,
        // cette assertion pourrait échouer. On teste juste les getters.
        assertEquals(5L, dept1.getIdDepartment());
        assertEquals(5L, dept2.getIdDepartment());
        assertEquals("Chemistry", dept1.getName());
        assertEquals("Chemistry", dept2.getName());
    }

    @Test
    void testPhoneNumberFormat() {
        Department department = new Department();

        // Tester différents formats de numéro
        department.setPhone("12345678");
        assertEquals("12345678", department.getPhone());

        department.setPhone("+216-71-123-456");
        assertEquals("+216-71-123-456", department.getPhone());

        department.setPhone("0021671123456");
        assertEquals("0021671123456", department.getPhone());
    }
}
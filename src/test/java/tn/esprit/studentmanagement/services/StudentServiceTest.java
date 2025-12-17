package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void getAllStudents_ShouldReturnList() {
        // Arrange
        Student student1 = new Student(1L, "Mohamed", "Ali", "mohamed@esprit.tn",
                "12345678", LocalDate.of(2000, 5, 15), "Tunis", null, null);
        Student student2 = new Student(2L, "Fatima", "Zahra", "fatima@esprit.tn",
                "87654321", LocalDate.of(1999, 3, 20), "Sfax", null, null);

        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // Act
        List<Student> students = studentService.getAllStudents();

        // Assert
        assertNotNull(students);
        assertEquals(2, students.size());
        assertEquals("Mohamed", students.get(0).getFirstName());
        assertEquals("Fatima", students.get(1).getFirstName());
        assertEquals("Ali", students.get(0).getLastName());

        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getStudentById_WhenExists_ShouldReturnStudent() {
        // Arrange
        Long studentId = 1L;
        Student student = new Student(studentId, "Karim", "Ben Ahmed", "karim@esprit.tn",
                "11223344", LocalDate.of(2001, 7, 10), "Nabeul", null, null);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // Act
        Student result = studentService.getStudentById(studentId);

        // Assert
        assertNotNull(result);
        assertEquals(studentId, result.getIdStudent());
        assertEquals("Karim", result.getFirstName());
        assertEquals("Ben Ahmed", result.getLastName());
        assertEquals("karim@esprit.tn", result.getEmail());

        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void getStudentById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        Long studentId = 999L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act
        Student result = studentService.getStudentById(studentId);

        // Assert
        assertNull(result);
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void saveStudent_ShouldReturnSavedStudent() {
        // Arrange
        Student studentToSave = new Student(null, "New", "Student", "new@esprit.tn",
                "99887766", LocalDate.of(2002, 1, 1), "Bizerte", null, null);
        Student savedStudent = new Student(1L, "New", "Student", "new@esprit.tn",
                "99887766", LocalDate.of(2002, 1, 1), "Bizerte", null, null);

        when(studentRepository.save(studentToSave)).thenReturn(savedStudent);

        // Act
        Student result = studentService.saveStudent(studentToSave);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdStudent());
        assertEquals("New", result.getFirstName());
        assertEquals("Student", result.getLastName());
        assertEquals("new@esprit.tn", result.getEmail());

        verify(studentRepository, times(1)).save(studentToSave);
    }

    @Test
    void deleteStudent_ShouldCallRepository() {
        // Arrange
        Long studentId = 1L;

        doNothing().when(studentRepository).deleteById(studentId);

        // Act
        studentService.deleteStudent(studentId);

        // Assert
        verify(studentRepository, times(1)).deleteById(studentId);
    }

    @Test
    void saveStudent_UpdateExistingStudent() {
        // Arrange
        Long existingId = 5L;
        Student existingStudent = new Student(existingId, "Old", "Name", "old@test.com",
                "11111111", LocalDate.now(), "Old City", null, null);
        Student updatedStudent = new Student(existingId, "Updated", "Name", "updated@test.com",
                "22222222", LocalDate.now(), "New City", null, null);

        when(studentRepository.save(existingStudent)).thenReturn(updatedStudent);

        // Act
        Student result = studentService.saveStudent(existingStudent);

        // Assert
        assertEquals(existingId, result.getIdStudent());
        assertEquals("Updated", result.getFirstName());
        assertEquals("updated@test.com", result.getEmail());
        assertEquals("New City", result.getAddress());

        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void getAllStudents_EmptyList() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Student> students = studentService.getAllStudents();

        // Assert
        assertNotNull(students);
        assertTrue(students.isEmpty());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void saveStudent_WithMinimumData() {
        // Test avec données minimales
        Student minimalStudent = new Student();
        minimalStudent.setFirstName("Minimal");
        minimalStudent.setEmail("min@test.com");

        Student savedStudent = new Student(10L, "Minimal", null, "min@test.com",
                null, null, null, null, null);

        when(studentRepository.save(minimalStudent)).thenReturn(savedStudent);

        // Act
        Student result = studentService.saveStudent(minimalStudent);

        // Assert
        assertEquals(10L, result.getIdStudent());
        assertEquals("Minimal", result.getFirstName());
        assertEquals("min@test.com", result.getEmail());
    }
}
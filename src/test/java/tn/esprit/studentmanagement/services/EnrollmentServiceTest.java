package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.*;
import tn.esprit.studentmanagement.exceptions.EnrollmentNotFoundException;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    void getAllEnrollments_ShouldReturnList() {
        // Arrange
        Student student = new Student(1L, "Mohamed", "Ali", "mohamed@esprit.tn", "123", LocalDate.now(), "Tunis", null, null);
        Course course = new Course(1L, "Java", "CS101", 3, "Java Programming", null);

        Enrollment enrollment1 = new Enrollment(1L, LocalDate.now(), 15.5, Status.ACTIVE, student, course);
        Enrollment enrollment2 = new Enrollment(2L, LocalDate.now(), 18.0, Status.COMPLETED, student, course);

        when(enrollmentRepository.findAll()).thenReturn(Arrays.asList(enrollment1, enrollment2));

        // Act
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();

        // Assert
        assertNotNull(enrollments);
        assertEquals(2, enrollments.size());
        assertEquals(15.5, enrollments.get(0).getGrade());
        assertEquals(Status.COMPLETED, enrollments.get(1).getStatus());

        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void getEnrollmentById_WhenExists_ShouldReturnEnrollment() {
        // Arrange
        Long enrollmentId = 1L;
        Student student = new Student();
        student.setFirstName("Ahmed");

        Course course = new Course();
        course.setName("Database");

        Enrollment enrollment = new Enrollment(enrollmentId, LocalDate.now(), 16.0, Status.ACTIVE, student, course);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        // Act
        Enrollment result = enrollmentService.getEnrollmentById(enrollmentId);

        // Assert
        assertNotNull(result);
        assertEquals(enrollmentId, result.getIdEnrollment());
        assertEquals(16.0, result.getGrade());
        assertEquals("Ahmed", result.getStudent().getFirstName());
        assertEquals("Database", result.getCourse().getName());

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
    }

    @Test
    void getEnrollmentById_WhenNotExists_ShouldThrowException() {
        // Arrange
        Long enrollmentId = 999L;

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        // Act & Assert
        EnrollmentNotFoundException exception = assertThrows(
                EnrollmentNotFoundException.class,
                () -> enrollmentService.getEnrollmentById(enrollmentId)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(enrollmentRepository, times(1)).findById(enrollmentId);
    }

    @Test
    void saveEnrollment_ShouldReturnSavedEnrollment() {
        // Arrange
        Student student = new Student();
        Course course = new Course();

        Enrollment enrollmentToSave = new Enrollment(null, LocalDate.now(), 14.5, Status.ACTIVE, student, course);
        Enrollment savedEnrollment = new Enrollment(1L, LocalDate.now(), 14.5, Status.ACTIVE, student, course);

        when(enrollmentRepository.save(enrollmentToSave)).thenReturn(savedEnrollment);

        // Act
        Enrollment result = enrollmentService.saveEnrollment(enrollmentToSave);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdEnrollment());
        assertEquals(14.5, result.getGrade());
        assertEquals(Status.ACTIVE, result.getStatus());

        verify(enrollmentRepository, times(1)).save(enrollmentToSave);
    }

    @Test
    void deleteEnrollment_ShouldCallRepository() {
        // Arrange
        Long enrollmentId = 1L;

        doNothing().when(enrollmentRepository).deleteById(enrollmentId);

        // Act
        enrollmentService.deleteEnrollment(enrollmentId);

        // Assert
        verify(enrollmentRepository, times(1)).deleteById(enrollmentId);
    }

    @Test
    void saveEnrollment_WithGradeValidation() {
        // Arrange
        Enrollment enrollment = new Enrollment();
        enrollment.setGrade(20.0); // Note maximale
        enrollment.setStatus(Status.ACTIVE);

        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);

        // Act
        Enrollment result = enrollmentService.saveEnrollment(enrollment);

        // Assert
        assertEquals(20.0, result.getGrade());
        verify(enrollmentRepository, times(1)).save(enrollment);
    }

    @Test
    void saveEnrollment_WithDifferentStatus() {
        // Test avec différents statuts
        Enrollment enrollmentActive = new Enrollment();
        enrollmentActive.setStatus(Status.ACTIVE);

        Enrollment enrollmentCompleted = new Enrollment();
        enrollmentCompleted.setStatus(Status.COMPLETED);

        Enrollment enrollmentDropped = new Enrollment();
        enrollmentDropped.setStatus(Status.DROPPED);

        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenReturn(enrollmentActive, enrollmentCompleted, enrollmentDropped);

        // Act & Assert
        assertEquals(Status.ACTIVE, enrollmentService.saveEnrollment(enrollmentActive).getStatus());
        assertEquals(Status.COMPLETED, enrollmentService.saveEnrollment(enrollmentCompleted).getStatus());
        assertEquals(Status.DROPPED, enrollmentService.saveEnrollment(enrollmentDropped).getStatus());
    }
}
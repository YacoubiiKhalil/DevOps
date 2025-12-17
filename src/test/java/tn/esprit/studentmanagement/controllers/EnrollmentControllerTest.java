package tn.esprit.studentmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.studentmanagement.entities.*;
import tn.esprit.studentmanagement.services.IEnrollment;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IEnrollment enrollmentService;

    @InjectMocks
    private EnrollmentController enrollmentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Configure ObjectMapper pour supporter LocalDate
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // ← CORRECTION ICI !

        mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController).build();
    }

    @Test
    void getAllEnrollment_ShouldReturnList() throws Exception {
        // Arrange
        Student student = new Student(1L, "Mohamed", "Ali", "mohamed@esprit.tn",
                "12345678", LocalDate.of(2000, 5, 15), "Tunis", null, null);
        Course course = new Course(1L, "Java Programming", "CS101", 3, "Java course", null);

        Enrollment enrollment1 = new Enrollment(1L, LocalDate.of(2024, 1, 15), 15.5, Status.ACTIVE, student, course);
        Enrollment enrollment2 = new Enrollment(2L, LocalDate.of(2024, 2, 20), 18.0, Status.COMPLETED, student, course);

        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentService.getAllEnrollments()).thenReturn(enrollments);

        // Act & Assert
        mockMvc.perform(get("/Enrollment/getAllEnrollment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idEnrollment").value(1))
                .andExpect(jsonPath("$[0].grade").value(15.5))
                .andExpect(jsonPath("$[1].idEnrollment").value(2))
                .andExpect(jsonPath("$[1].status").value("COMPLETED"));

        verify(enrollmentService, times(1)).getAllEnrollments();
    }

    @Test
    void getEnrollment_WhenExists_ShouldReturnEnrollment() throws Exception {
        // Arrange
        Long enrollmentId = 1L;
        Student student = new Student(1L, "Ahmed", "Ben Ali", "ahmed@esprit.tn",
                "11223344", LocalDate.now(), "Sfax", null, null);
        Course course = new Course(1L, "Database", "CS201", 3, "DB course", null);
        Enrollment enrollment = new Enrollment(enrollmentId, LocalDate.of(2024, 3, 10), 16.5, Status.ACTIVE, student, course);

        when(enrollmentService.getEnrollmentById(enrollmentId)).thenReturn(enrollment);

        // Act & Assert
        mockMvc.perform(get("/Enrollment/getEnrollment/{id}", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnrollment").value(1))
                .andExpect(jsonPath("$.grade").value(16.5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(enrollmentService, times(1)).getEnrollmentById(enrollmentId);
    }

    @Test
    void createEnrollment_ShouldReturnCreatedEnrollment() throws Exception {
        // Arrange
        // Créer un Enrollment SANS LocalDate pour l'envoi
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setGrade(14.5);
        newEnrollment.setStatus(Status.ACTIVE);

        // Pour la réponse simulée
        Student student = new Student(1L, "Test", "Student", "test@esprit.tn",
                "99999999", LocalDate.now(), "Test City", null, null);
        Course course = new Course(1L, "Test Course", "TEST101", 3, "Test", null);
        Enrollment savedEnrollment = new Enrollment(3L, LocalDate.now(), 14.5, Status.ACTIVE, student, course);

        when(enrollmentService.saveEnrollment(any(Enrollment.class))).thenReturn(savedEnrollment);

        // Act & Assert
        mockMvc.perform(post("/Enrollment/createEnrollment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEnrollment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnrollment").value(3))
                .andExpect(jsonPath("$.grade").value(14.5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(enrollmentService, times(1)).saveEnrollment(any(Enrollment.class));
    }

    @Test
    void updateEnrollment_ShouldReturnUpdatedEnrollment() throws Exception {
        // Arrange
        // Créer un Enrollment SANS LocalDate pour l'envoi
        Enrollment updatedEnrollment = new Enrollment();
        updatedEnrollment.setIdEnrollment(1L);
        updatedEnrollment.setGrade(19.0);
        updatedEnrollment.setStatus(Status.COMPLETED);

        // Pour la réponse simulée
        Student student = new Student(1L, "Updated", "Student", "updated@esprit.tn",
                "88888888", LocalDate.now(), "Updated City", null, null);
        Course course = new Course(2L, "Updated Course", "UPD101", 4, "Updated", null);
        Enrollment savedEnrollment = new Enrollment(1L, LocalDate.now(), 19.0, Status.COMPLETED, student, course);

        when(enrollmentService.saveEnrollment(any(Enrollment.class))).thenReturn(savedEnrollment);

        // Act & Assert
        mockMvc.perform(put("/Enrollment/updateEnrollment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEnrollment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnrollment").value(1))
                .andExpect(jsonPath("$.grade").value(19.0))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(enrollmentService, times(1)).saveEnrollment(any(Enrollment.class));
    }

    @Test
    void deleteEnrollment_ShouldCallService() throws Exception {
        // Arrange
        Long enrollmentId = 1L;
        doNothing().when(enrollmentService).deleteEnrollment(enrollmentId);

        // Act & Assert
        mockMvc.perform(delete("/Enrollment/deleteEnrollment/{id}", enrollmentId))
                .andExpect(status().isOk());

        verify(enrollmentService, times(1)).deleteEnrollment(enrollmentId);
    }

    @Test
    void getEnrollment_WhenNotExists_ShouldReturnOk() throws Exception {
        // Arrange
        Long enrollmentId = 999L;

        when(enrollmentService.getEnrollmentById(enrollmentId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/Enrollment/getEnrollment/{id}", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(enrollmentService, times(1)).getEnrollmentById(enrollmentId);
    }

    @Test
    void createEnrollment_WithMinimumData() throws Exception {
        // Test avec données minimales
        Enrollment minimalEnrollment = new Enrollment();
        minimalEnrollment.setGrade(10.0);
        minimalEnrollment.setStatus(Status.ACTIVE);

        Enrollment savedEnrollment = new Enrollment(5L, LocalDate.now(), 10.0, Status.ACTIVE, null, null);

        when(enrollmentService.saveEnrollment(any(Enrollment.class))).thenReturn(savedEnrollment);

        // Act & Assert
        mockMvc.perform(post("/Enrollment/createEnrollment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalEnrollment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnrollment").value(5))
                .andExpect(jsonPath("$.grade").value(10.0));
    }
}
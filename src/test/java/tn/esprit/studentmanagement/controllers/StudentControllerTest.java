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
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.services.IStudentService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IStudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Configure ObjectMapper pour supporter Java 8 date/time
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    void getAllStudents_ShouldReturnList() throws Exception {
        // Arrange
        Department department = new Department(1L, "Informatique", "Bat A", "123", "Prof A", null);

        Student student1 = new Student(1L, "Mohamed", "Ali", "mohamed@esprit.tn",
                "12345678", LocalDate.of(2000, 5, 15), "Tunis", department, null);
        Student student2 = new Student(2L, "Fatima", "Zahra", "fatima@esprit.tn",
                "87654321", LocalDate.of(1999, 3, 20), "Sfax", department, null);

        List<Student> students = Arrays.asList(student1, student2);

        when(studentService.getAllStudents()).thenReturn(students);

        // Act & Assert
        mockMvc.perform(get("/students/getAllStudents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Mohamed"))
                .andExpect(jsonPath("$[0].lastName").value("Ali"))
                .andExpect(jsonPath("$[1].firstName").value("Fatima"))
                .andExpect(jsonPath("$[1].lastName").value("Zahra"));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void getStudent_WhenExists_ShouldReturnStudent() throws Exception {
        // Arrange
        Long studentId = 1L;
        Department department = new Department(1L, "Informatique", "Bat A", "123", "Prof A", null);
        Student student = new Student(studentId, "Karim", "Ben Ahmed", "karim@esprit.tn",
                "11223344", LocalDate.of(2001, 7, 10), "Nabeul", department, null);

        when(studentService.getStudentById(studentId)).thenReturn(student);

        // Act & Assert
        mockMvc.perform(get("/students/getStudent/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idStudent").value(1))
                .andExpect(jsonPath("$.firstName").value("Karim"))
                .andExpect(jsonPath("$.lastName").value("Ben Ahmed"))
                .andExpect(jsonPath("$.email").value("karim@esprit.tn"))
                .andExpect(jsonPath("$.department.name").value("Informatique"));

        verify(studentService, times(1)).getStudentById(studentId);
    }

    @Test
    void createStudent_ShouldReturnCreatedStudent() throws Exception {
        // Arrange
        // Créer un Student simple pour l'envoi
        Student newStudent = new Student();
        newStudent.setFirstName("New");
        newStudent.setLastName("Student");
        newStudent.setEmail("new@esprit.tn");
        newStudent.setPhone("99887766");

        // Pour la réponse simulée
        Department department = new Department(1L, "Informatique", "Bat A", "123", "Prof A", null);
        Student savedStudent = new Student(3L, "New", "Student", "new@esprit.tn",
                "99887766", LocalDate.of(2002, 1, 1), "Bizerte", department, null);

        when(studentService.saveStudent(any(Student.class))).thenReturn(savedStudent);

        // Act & Assert
        mockMvc.perform(post("/students/createStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idStudent").value(3))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("Student"))
                .andExpect(jsonPath("$.email").value("new@esprit.tn"));

        verify(studentService, times(1)).saveStudent(any(Student.class));
    }

    @Test
    void updateStudent_ShouldReturnUpdatedStudent() throws Exception {
        // Arrange
        // Créer un Student simple pour l'envoi
        Student updatedStudent = new Student();
        updatedStudent.setIdStudent(1L);
        updatedStudent.setFirstName("Updated");
        updatedStudent.setLastName("Name");
        updatedStudent.setEmail("updated@test.com");
        updatedStudent.setAddress("New City");

        // Pour la réponse simulée
        Department department = new Department(1L, "Informatique", "Bat A", "123", "Prof A", null);
        Student savedStudent = new Student(1L, "Updated", "Name", "updated@test.com",
                "22222222", LocalDate.now(), "New City", department, null);

        when(studentService.saveStudent(any(Student.class))).thenReturn(savedStudent);

        // Act & Assert
        mockMvc.perform(put("/students/updateStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idStudent").value(1))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.email").value("updated@test.com"))
                .andExpect(jsonPath("$.address").value("New City"));

        verify(studentService, times(1)).saveStudent(any(Student.class));
    }

    @Test
    void deleteStudent_ShouldCallService() throws Exception {
        // Arrange
        Long studentId = 1L;
        doNothing().when(studentService).deleteStudent(studentId);

        // Act & Assert
        mockMvc.perform(delete("/students/deleteStudent/{id}", studentId))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(studentId);
    }

    @Test
    void getStudent_WhenNotExists_ShouldReturnOk() throws Exception {
        // Arrange
        Long studentId = 999L;

        // Votre service retourne null quand non trouvé
        when(studentService.getStudentById(studentId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/students/getStudent/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(studentService, times(1)).getStudentById(studentId);
    }

    @Test
    void createStudent_WithMinimumData() throws Exception {
        // Test avec données minimales
        Student minimalStudent = new Student();
        minimalStudent.setFirstName("Minimal");
        minimalStudent.setEmail("min@test.com");

        Student savedStudent = new Student(5L, "Minimal", null, "min@test.com",
                null, null, null, null, null);

        when(studentService.saveStudent(any(Student.class))).thenReturn(savedStudent);

        // Act & Assert
        mockMvc.perform(post("/students/createStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idStudent").value(5))
                .andExpect(jsonPath("$.firstName").value("Minimal"))
                .andExpect(jsonPath("$.email").value("min@test.com"));
    }

    @Test
    void getAllStudents_EmptyList() throws Exception {
        // Arrange
        when(studentService.getAllStudents()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/students/getAllStudents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(studentService, times(1)).getAllStudents();
    }
}
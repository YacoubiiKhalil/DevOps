package tn.esprit.studentmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import tn.esprit.studentmanagement.services.IDepartmentService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IDepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();
    }

    @Test
    void getAllDepartment_ShouldReturnList() throws Exception {
        // Arrange
        Department dept1 = new Department(1L, "Informatique", "Bat A", "123", "Prof A", null);
        Department dept2 = new Department(2L, "Mathématiques", "Bat B", "456", "Prof B", null);
        List<Department> departments = Arrays.asList(dept1, dept2);

        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act & Assert
        mockMvc.perform(get("/Depatment/getAllDepartment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Informatique"))
                .andExpect(jsonPath("$[1].name").value("Mathématiques"));

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    void getDepartment_WhenExists_ShouldReturnDepartment() throws Exception {
        // Arrange
        Long departmentId = 1L;
        Department department = new Department(departmentId, "Informatique", "Bat A", "123", "Prof A", null);

        when(departmentService.getDepartmentById(departmentId)).thenReturn(department);

        // Act & Assert
        mockMvc.perform(get("/Depatment/getDepartment/{id}", departmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDepartment").value(1))
                .andExpect(jsonPath("$.name").value("Informatique"))
                .andExpect(jsonPath("$.location").value("Bat A"));

        verify(departmentService, times(1)).getDepartmentById(departmentId);
    }

    @Test
    void createDepartment_ShouldReturnCreatedDepartment() throws Exception {
        // Arrange
        Department newDepartment = new Department(null, "New Dept", "Bat C", "789", "Prof C", null);
        Department savedDepartment = new Department(3L, "New Dept", "Bat C", "789", "Prof C", null);

        when(departmentService.saveDepartment(any(Department.class))).thenReturn(savedDepartment);

        // Act & Assert
        mockMvc.perform(post("/Depatment/createDepartment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDepartment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDepartment").value(3))
                .andExpect(jsonPath("$.name").value("New Dept"));

        verify(departmentService, times(1)).saveDepartment(any(Department.class));
    }

    @Test
    void updateDepartment_ShouldReturnUpdatedDepartment() throws Exception {
        // Arrange
        Department updatedDepartment = new Department(1L, "Updated Dept", "New Loc", "999", "New Head", null);

        when(departmentService.saveDepartment(any(Department.class))).thenReturn(updatedDepartment);

        // Act & Assert
        mockMvc.perform(put("/Depatment/updateDepartment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDepartment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDepartment").value(1))
                .andExpect(jsonPath("$.name").value("Updated Dept"))
                .andExpect(jsonPath("$.location").value("New Loc"));

        verify(departmentService, times(1)).saveDepartment(any(Department.class));
    }

    @Test
    void deleteDepartment_ShouldCallService() throws Exception {
        // Arrange
        Long departmentId = 1L;
        doNothing().when(departmentService).deleteDepartment(departmentId);

        // Act & Assert
        mockMvc.perform(delete("/Depatment/deleteDepartment/{id}", departmentId))
                .andExpect(status().isOk());

        verify(departmentService, times(1)).deleteDepartment(departmentId);
    }

    @Test
    void getDepartment_WhenNotExists_ShouldReturnOk() throws Exception {
        // Arrange
        Long departmentId = 999L;

        // Votre service retourne null quand non trouvé
        when(departmentService.getDepartmentById(departmentId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/Depatment/getDepartment/{id}", departmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(departmentService, times(1)).getDepartmentById(departmentId);
    }
}
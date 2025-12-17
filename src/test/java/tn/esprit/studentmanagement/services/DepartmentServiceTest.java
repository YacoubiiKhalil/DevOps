package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.exceptions.DepartmentNotFoundException;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void getAllDepartments_ShouldReturnList() {
        // Arrange
        Department dept1 = new Department(1L, "Informatique", "Bat A", "123", "Prof A", null);
        Department dept2 = new Department(2L, "Mathématiques", "Bat B", "456", "Prof B", null);

        when(departmentRepository.findAll()).thenReturn(Arrays.asList(dept1, dept2));

        // Act
        List<Department> departments = departmentService.getAllDepartments();

        // Assert
        assertNotNull(departments);
        assertEquals(2, departments.size());
        assertEquals("Informatique", departments.get(0).getName());
        assertEquals("Mathématiques", departments.get(1).getName());

        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void getDepartmentById_WhenExists_ShouldReturnDepartment() {
        // Arrange
        Long departmentId = 1L;
        Department department = new Department(departmentId, "Informatique", "Bat A", "123", "Prof A", null);

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // Act
        Department result = departmentService.getDepartmentById(departmentId);

        // Assert
        assertNotNull(result);
        assertEquals(departmentId, result.getIdDepartment());
        assertEquals("Informatique", result.getName());

        verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    void getDepartmentById_WhenNotExists_ShouldThrowException() {
        // Arrange
        Long departmentId = 999L;

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act & Assert
        DepartmentNotFoundException exception = assertThrows(
                DepartmentNotFoundException.class,
                () -> departmentService.getDepartmentById(departmentId)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    void saveDepartment_ShouldReturnSavedDepartment() {
        // Arrange
        Department departmentToSave = new Department(null, "New Dept", "Bat C", "789", "Prof C", null);
        Department savedDepartment = new Department(1L, "New Dept", "Bat C", "789", "Prof C", null);

        when(departmentRepository.save(departmentToSave)).thenReturn(savedDepartment);

        // Act
        Department result = departmentService.saveDepartment(departmentToSave);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdDepartment());
        assertEquals("New Dept", result.getName());

        verify(departmentRepository, times(1)).save(departmentToSave);
    }

    @Test
    void deleteDepartment_ShouldCallRepository() {
        // Arrange
        Long departmentId = 1L;

        doNothing().when(departmentRepository).deleteById(departmentId);

        // Act
        departmentService.deleteDepartment(departmentId);

        // Assert
        verify(departmentRepository, times(1)).deleteById(departmentId);
    }

    @Test
    void saveDepartment_WithExistingId_ShouldUpdate() {
        // Arrange
        Long existingId = 1L;
        Department existingDept = new Department(existingId, "Old Name", "Old Loc", "111", "Old Head", null);
        Department updatedDept = new Department(existingId, "Updated Name", "New Loc", "222", "New Head", null);

        when(departmentRepository.save(existingDept)).thenReturn(updatedDept);

        // Act
        Department result = departmentService.saveDepartment(existingDept);

        // Assert
        assertEquals(existingId, result.getIdDepartment());
        assertEquals("Updated Name", result.getName());
        assertEquals("New Loc", result.getLocation());

        verify(departmentRepository, times(1)).save(existingDept);
    }
}
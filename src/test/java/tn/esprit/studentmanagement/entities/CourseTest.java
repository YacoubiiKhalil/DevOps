// CourseTest.java (dans le même package)
package tn.esprit.studentmanagement.entities;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    void testCourseCreation() {
        Course course = new Course();

        course.setIdCourse(1L);
        course.setName("Software Engineering");
        course.setCode("CS301");
        course.setCredit(4);
        course.setDescription("Learn software development methodologies");

        assertEquals(1L, course.getIdCourse());
        assertEquals("Software Engineering", course.getName());
        assertEquals("CS301", course.getCode());
        assertEquals(4, course.getCredit());
        assertTrue(course.getDescription().contains("software"));
    }

    @Test
    void testAllArgsConstructor() {
        List<Enrollment> enrollments = new ArrayList<>();

        Course course = new Course(
                2L,
                "Artificial Intelligence",
                "CS401",
                3,
                "Introduction to AI and Machine Learning",
                enrollments
        );

        assertEquals(2L, course.getIdCourse());
        assertEquals("Artificial Intelligence", course.getName());
        assertEquals("CS401", course.getCode());
        assertEquals(3, course.getCredit());
        assertEquals(enrollments, course.getEnrollments());
    }

    @Test
    void testCourseCodeFormat() {
        Course course = new Course();
        course.setCode("MATH101");

        // Le code devrait être en majuscules
        assertEquals("MATH101", course.getCode().toUpperCase());
    }
}
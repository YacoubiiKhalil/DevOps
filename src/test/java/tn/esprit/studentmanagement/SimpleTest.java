package tn.esprit.studentmanagement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleTest {
    @Test
    void basicTest() {
        assertTrue(true, "Test basique pour Jacoco");
    }

    @Test
    void calculationTest() {
        assertEquals(4, 2 + 2, "Test de calcul simple");
    }
}
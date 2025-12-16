package tn.esprit.studentmanagement.exceptions;

public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(String message) {
        super(message);
    }

    // Optionnel : constructeur avec cause
    public EnrollmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
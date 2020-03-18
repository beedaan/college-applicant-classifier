package com.builtbybrendan.collegApplicantClassifier.application;

import com.builtbybrendan.collegeApplicantClassifier.application.Application;
import com.builtbybrendan.collegeApplicantClassifier.application.ApplicationValidator;
import com.builtbybrendan.collegeApplicantClassifier.application.State;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationValidatorTest {

    ApplicationValidator applicationValidator = new ApplicationValidator();

    @Test
    void gpaShouldNotBeGreaterThanGpaScale() {
        Application application = Application.builder()
                .gpa(4.0)
                .gpaScale(3.0)
                .satScore(1920)
                .actScore(27)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicationValidator.validate(application)
        );

        assertEquals("GPA cannot be greater than GPA Scale", exception.getMessage());
    }

    @Test
    void shouldHaveBothOrOnlyOneSatScoreOrActScore() {
        Application application = Application.builder()
                .gpa(3.0)
                .gpaScale(4.0)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicationValidator.validate(application)
        );

        assertEquals("Must contain SAT Score, ACT Score, or both", exception.getMessage());
    }

    @Test
    void shouldNotThrowExceptionIfValid() {
        Application application = Application.builder()
                .firstName("Joe")
                .lastName("Smith")
                .state(State.CALIFORNIA)
                .age(18)
                .gpa(3.0)
                .gpaScale(4.0)
                .satScore(1920)
                .actScore(27)
                .felonyDates(Collections.emptyList())
                .build();

        assertDoesNotThrow(() -> applicationValidator.validate(application));
    }

    @Test
    void shouldThrowExceptionIfFirstNameIsNull() {
        Application application = Application.builder()
                .firstName(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicationValidator.validate(application)
        );

        assertEquals("First Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfFirstNameIsEmpty() {
        Application application = Application.builder()
                .firstName("")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicationValidator.validate(application)
        );

        assertEquals("First Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfLastNameIsNull() {
        Application application = Application.builder()
                .lastName(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicationValidator.validate(application)
        );

        assertEquals("Last Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfLastNameIsEmpty() {
        Application application = Application.builder()
                .lastName("")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicationValidator.validate(application)
        );

        assertEquals("Last Name cannot be null or empty", exception.getMessage());
    }
}

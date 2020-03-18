package com.builtbybrendan.collegApplicantClassifier.application;

import com.builtbybrendan.collegeApplicantClassifier.applicant.Applicant;
import com.builtbybrendan.collegeApplicantClassifier.applicant.ApplicantValidator;
import com.builtbybrendan.collegeApplicantClassifier.applicant.State;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicantValidatorTest {

    ApplicantValidator applicantValidator = new ApplicantValidator();

    @Test
    void gpaShouldNotBeGreaterThanGpaScale() {
        Applicant applicant = Applicant.builder()
                .gpa(4.0)
                .gpaScale(3.0)
                .satScore(1920)
                .actScore(27)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicantValidator.validate(applicant)
        );

        assertEquals("GPA cannot be greater than GPA Scale", exception.getMessage());
    }

    @Test
    void shouldHaveBothOrOnlyOneSatScoreOrActScore() {
        Applicant applicant = Applicant.builder()
                .gpa(3.0)
                .gpaScale(4.0)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicantValidator.validate(applicant)
        );

        assertEquals("Must contain SAT Score, ACT Score, or both", exception.getMessage());
    }

    @Test
    void shouldNotThrowExceptionIfValid() {
        Applicant applicant = Applicant.builder()
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

        assertDoesNotThrow(() -> applicantValidator.validate(applicant));
    }

    @Test
    void shouldThrowExceptionIfFirstNameIsNull() {
        Applicant applicant = Applicant.builder()
                .satScore(1920)
                .firstName(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicantValidator.validate(applicant)
        );

        assertEquals("First Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfFirstNameIsEmpty() {
        Applicant applicant = Applicant.builder()
                .satScore(1920)
                .firstName("")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicantValidator.validate(applicant)
        );

        assertEquals("First Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfLastNameIsNull() {
        Applicant applicant = Applicant.builder()
                .satScore(1920)
                .firstName("Joe")
                .lastName(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicantValidator.validate(applicant)
        );

        assertEquals("Last Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfLastNameIsEmpty() {
        Applicant applicant = Applicant.builder()
                .satScore(1920)
                .firstName("Joe")
                .lastName("")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                applicantValidator.validate(applicant)
        );

        assertEquals("Last Name cannot be null or empty", exception.getMessage());
    }
}

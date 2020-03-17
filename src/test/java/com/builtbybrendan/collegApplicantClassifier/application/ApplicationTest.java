package com.builtbybrendan.collegApplicantClassifier.application;


import com.builtbybrendan.collegeApplicantClassifier.application.Application;
import com.builtbybrendan.collegeApplicantClassifier.application.State;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationTest {

    @Test
    void gpaShouldNotBeGreaterThanGpaScale() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Application.builder()
                        .gpa(4.0)
                        .gpaScale(3.0)
                        .build()
        );

        assertEquals("GPA cannot be greater than GPA Scale", exception.getMessage());
    }

    @Test
    void shouldNotThrowExceptionIfValid() {
        Application.builder()
                .firstName("Joe")
                .lastName("Smith")
                .state(State.CALIFORNIA)
                .age(18)
                .gpa(3.0)
                .gpaScale(4.0)
                .satScore(1920)
                .actScore(27)
                .felonies(0)
                .build();
    }
}

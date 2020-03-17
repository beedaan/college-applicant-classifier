package com.builtbybrendan.collegApplicantClassifier.application;

import com.builtbybrendan.collegeApplicantClassifier.application.Application;
import com.builtbybrendan.collegeApplicantClassifier.application.ApplicationService;
import com.builtbybrendan.collegeApplicantClassifier.application.Classification;
import com.builtbybrendan.collegeApplicantClassifier.application.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationServiceTest {

    ApplicationService applicationService = new ApplicationService();

    Application application;

    @BeforeEach
    void setup() {
        application = Application.builder()
                .firstName("Joe")
                .lastName("Smith")
                .state(State.MARYLAND)
                .age(18)
                .gpa(3.0)
                .gpaScale(4.0)
                .satScore(1920)
                .actScore(27)
                .felonies(0)
                .build();
    }

    @Test
    void processApplicationShouldReturnFurtherReviewIfNeitherAcceptNorReject() {
        assertEquals(Classification.FURTHER_REVIEW, applicationService.processApplication(application).getClassification());
    }
}

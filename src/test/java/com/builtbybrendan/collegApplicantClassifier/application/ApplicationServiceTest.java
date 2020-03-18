package com.builtbybrendan.collegApplicantClassifier.application;

import com.builtbybrendan.collegeApplicantClassifier.application.Application;
import com.builtbybrendan.collegeApplicantClassifier.application.ApplicationService;
import com.builtbybrendan.collegeApplicantClassifier.application.ApplicationStatus;
import com.builtbybrendan.collegeApplicantClassifier.application.ApplicationValidator;
import com.builtbybrendan.collegeApplicantClassifier.application.Classification;
import com.builtbybrendan.collegeApplicantClassifier.application.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @InjectMocks
    ApplicationService applicationService = new ApplicationService();
    @Spy
    ApplicationValidator applicationValidator;

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
                .felonyDates(Collections.emptyList())
                .build();
    }

    @Test
    void processApplicationShouldReturnFurtherReviewIfNeitherAcceptNorReject() {
        assertEquals(Classification.FURTHER_REVIEW, applicationService.processApplication(application).getClassification());

        verify(applicationValidator).validate(application);
    }

    @Test
    void processApplicationShouldRejectIfMoreThanOneFelony() {
        application.setFelonyDates(Collections.singletonList(LocalDate.now()));

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant cannot have 1 or more felonies over the past 5 years", applicationStatus.getReason());

        verify(applicationValidator).validate(application);
    }
}

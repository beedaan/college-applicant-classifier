package com.builtbybrendan.collegApplicantClassifier.application;

import com.builtbybrendan.collegeApplicantClassifier.application.Application;
import com.builtbybrendan.collegeApplicantClassifier.application.ApplicationRepository;
import com.builtbybrendan.collegeApplicantClassifier.application.ApplicationService;
import com.builtbybrendan.collegeApplicantClassifier.application.ApplicationStatus;
import com.builtbybrendan.collegeApplicantClassifier.application.ApplicationValidator;
import com.builtbybrendan.collegeApplicantClassifier.application.Classification;
import com.builtbybrendan.collegeApplicantClassifier.application.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @InjectMocks
    ApplicationService applicationService = new ApplicationService();

    @Spy
    ApplicationValidator applicationValidator;
    @Spy
    ApplicationRepository applicationRepository;

    @Captor
    ArgumentCaptor<Application> applicationArgumentCaptor;

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
        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.FURTHER_REVIEW, applicationStatus.getClassification());
        assertNull(applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(Classification.FURTHER_REVIEW, applicationArgumentCaptor.getValue().getApplicationStatus().getClassification());
        assertNull(applicationArgumentCaptor.getValue().getApplicationStatus().getReason());
    }

    @Test
    void processApplicationShouldRejectIfMoreThanOneFelony() {
        application.setFelonyDates(Collections.singletonList(LocalDate.now()));

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant cannot have 1 or more felonies over the past 5 years", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(Classification.FURTHER_REVIEW, applicationArgumentCaptor.getValue().getApplicationStatus().getClassification());
        assertNull(applicationArgumentCaptor.getValue().getApplicationStatus().getReason());
    }

    @Test
    void processApplicationShouldRejectIfMoreThanOneFelonyLessThan5YearsAgo() {
        application.setFelonyDates(Collections.singletonList(LocalDate.now().minusYears(5).plusDays(1)));

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant cannot have 1 or more felonies over the past 5 years", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfNoFeloniesWithin5Years() {
        application.setFelonyDates(Collections.singletonList(LocalDate.now().minusYears(5)));

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.FURTHER_REVIEW, applicationStatus.getClassification());
        assertNull(applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(Classification.FURTHER_REVIEW, applicationArgumentCaptor.getValue().getApplicationStatus().getClassification());
        assertNull(applicationArgumentCaptor.getValue().getApplicationStatus().getReason());
    }
}

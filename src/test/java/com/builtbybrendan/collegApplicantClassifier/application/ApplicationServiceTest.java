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
        assertEquals(Classification.INSTANT_REJECT, applicationArgumentCaptor.getValue().getApplicationStatus().getClassification());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
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

    @Test
    void processApplicationShouldRejectIfGpaBelow70PercentWith4Scale() {
        application.setGpa(2.7);
        application.setGpaScale(4.0);

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant cannot have GPA below 70%", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfGpaEqualTo70PercentWith4Scale() {
        application.setGpa(2.8);
        application.setGpaScale(4.0);

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.FURTHER_REVIEW, applicationStatus.getClassification());
        assertNull(applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldRejectIfGpaBelow70PercentWith5Scale() {
        application.setGpa(3.4);
        application.setGpaScale(5.0);

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant cannot have GPA below 70%", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfGpaEqualTo70PercentWith5Scale() {
        application.setGpa(3.5);
        application.setGpaScale(5.0);

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.FURTHER_REVIEW, applicationStatus.getClassification());
        assertNull(applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldRejectIfAgeIsNegative() {
        application.setAge(-20);

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant cannot have a negative age", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfAgeZero() {
        application.setAge(0);

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.FURTHER_REVIEW, applicationStatus.getClassification());
        assertNull(applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfAgeIsPositive() {
        application.setAge(1);

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.FURTHER_REVIEW, applicationStatus.getClassification());
        assertNull(applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldRejectIfFirstNameDoesNotHaveFirstLetterCapitalized() {
        application.setFirstName("joe");

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant must have a first name with the first letter capitalized, the rest lower case", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldRejectIfFirstNameHasNonFirstLetterCapitalized() {
        application.setFirstName("joE");

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant must have a first name with the first letter capitalized, the rest lower case", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldRejectIfFirstNameIsAllUppercase() {
        application.setFirstName("JOE");

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant must have a first name with the first letter capitalized, the rest lower case", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfFirstNameIs1Character() {
        application.setFirstName("J");

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.FURTHER_REVIEW, applicationStatus.getClassification());
        assertNull(applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldRejectIfLastNameDoesNotHaveFirstLetterCapitalized() {
        application.setLastName("joe");

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant must have a last name with the first letter capitalized, the rest lower case", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldRejectIfLastNameHasNonFirstLetterCapitalized() {
        application.setLastName("joE");

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant must have a last name with the first letter capitalized, the rest lower case", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldRejectIfLastNameIsAllUppercase() {
        application.setLastName("JOE");

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.INSTANT_REJECT, applicationStatus.getClassification());
        assertEquals("Applicant must have a last name with the first letter capitalized, the rest lower case", applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfLastNameIs1Character() {
        application.setLastName("J");

        ApplicationStatus applicationStatus = applicationService.processApplication(application);

        assertEquals(Classification.FURTHER_REVIEW, applicationStatus.getClassification());
        assertNull(applicationStatus.getReason());

        verify(applicationValidator).validate(application);

        verify(applicationRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicationStatus, applicationArgumentCaptor.getValue().getApplicationStatus());
    }
}

package com.builtbybrendan.collegApplicantClassifier.application;

import com.builtbybrendan.collegeApplicantClassifier.applicant.Applicant;
import com.builtbybrendan.collegeApplicantClassifier.applicant.ApplicantRepository;
import com.builtbybrendan.collegeApplicantClassifier.applicant.ApplicantService;
import com.builtbybrendan.collegeApplicantClassifier.applicant.ApplicantStatus;
import com.builtbybrendan.collegeApplicantClassifier.applicant.ApplicantValidator;
import com.builtbybrendan.collegeApplicantClassifier.applicant.Classification;
import com.builtbybrendan.collegeApplicantClassifier.applicant.State;
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
public class ApplicantServiceTest {

    @InjectMocks
    ApplicantService applicantService = new ApplicantService();

    @Spy
    ApplicantValidator applicantValidator;
    @Spy
    ApplicantRepository applicantRepository;

    @Captor
    ArgumentCaptor<Applicant> applicationArgumentCaptor;

    Applicant defaultApplicant;

    @BeforeEach
    void setup() {
        defaultApplicant = Applicant.builder()
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
        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(Classification.FURTHER_REVIEW, applicationArgumentCaptor.getValue().getApplicantStatus().getClassification());
        assertNull(applicationArgumentCaptor.getValue().getApplicantStatus().getReason());
    }

    @Test
    void processApplicationShouldRejectIfMoreThanOneFelony() {
        defaultApplicant.setFelonyDates(Collections.singletonList(LocalDate.now()));

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant cannot have 1 or more felonies over the past 5 years", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(Classification.INSTANT_REJECT, applicationArgumentCaptor.getValue().getApplicantStatus().getClassification());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldRejectIfMoreThanOneFelonyLessThan5YearsAgo() {
        defaultApplicant.setFelonyDates(Collections.singletonList(LocalDate.now().minusYears(5).plusDays(1)));

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant cannot have 1 or more felonies over the past 5 years", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfNoFeloniesWithin5Years() {
        defaultApplicant.setFelonyDates(Collections.singletonList(LocalDate.now().minusYears(5)));

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(Classification.FURTHER_REVIEW, applicationArgumentCaptor.getValue().getApplicantStatus().getClassification());
        assertNull(applicationArgumentCaptor.getValue().getApplicantStatus().getReason());
    }

    @Test
    void processApplicationShouldRejectIfGpaBelow70PercentWith4Scale() {
        defaultApplicant.setGpa(2.7);
        defaultApplicant.setGpaScale(4.0);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant cannot have GPA below 70%", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfGpaEqualTo70PercentWith4Scale() {
        defaultApplicant.setGpa(2.8);
        defaultApplicant.setGpaScale(4.0);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldRejectIfGpaBelow70PercentWith5Scale() {
        defaultApplicant.setGpa(3.4);
        defaultApplicant.setGpaScale(5.0);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant cannot have GPA below 70%", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfGpaEqualTo70PercentWith5Scale() {
        defaultApplicant.setGpa(3.5);
        defaultApplicant.setGpaScale(5.0);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldRejectIfAgeIsNegative() {
        defaultApplicant.setAge(-20);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant cannot have a negative age", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfAgeZero() {
        defaultApplicant.setAge(0);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfAgeIsPositive() {
        defaultApplicant.setAge(1);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldRejectIfFirstNameDoesNotHaveFirstLetterCapitalized() {
        defaultApplicant.setFirstName("joe");

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant must have a first name with the first letter capitalized, the rest lower case", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldRejectIfFirstNameHasNonFirstLetterCapitalized() {
        defaultApplicant.setFirstName("joE");

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant must have a first name with the first letter capitalized, the rest lower case", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldRejectIfFirstNameIsAllUppercase() {
        defaultApplicant.setFirstName("JOE");

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant must have a first name with the first letter capitalized, the rest lower case", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfFirstNameIs1Character() {
        defaultApplicant.setFirstName("J");

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldRejectIfLastNameDoesNotHaveFirstLetterCapitalized() {
        defaultApplicant.setLastName("joe");

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant must have a last name with the first letter capitalized, the rest lower case", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldRejectIfLastNameHasNonFirstLetterCapitalized() {
        defaultApplicant.setLastName("joE");

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant must have a last name with the first letter capitalized, the rest lower case", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldRejectIfLastNameIsAllUppercase() {
        defaultApplicant.setLastName("JOE");

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.INSTANT_REJECT, applicantStatus.getClassification());
        assertEquals("Applicant must have a last name with the first letter capitalized, the rest lower case", applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotRejectIfLastNameIs1Character() {
        defaultApplicant.setLastName("J");

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotApproveIfJustInStateAgeRequirementPasses() {
        defaultApplicant.setState(State.CALIFORNIA);
        defaultApplicant.setAge(17);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotApproveIfJustOutOfStateAgeRequirementPasses() {
        defaultApplicant.setAge(81);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotApproveIfJustGpaRequirementPasses() {
        defaultApplicant.setGpa(3.6);
        defaultApplicant.setGpaScale(4.0);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotApproveIfJustSatRequirementPasses() {
        defaultApplicant.setSatScore(1921);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldNotApproveIfJustActRequirementPasses() {
        defaultApplicant.setActScore(28);

        ApplicantStatus applicantStatus = applicantService.processApplicant(defaultApplicant);

        assertEquals(Classification.FURTHER_REVIEW, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(defaultApplicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldApproveIfAllRequirementsPass() {
        Applicant applicant = Applicant.builder()
                .firstName("Joe")
                .lastName("Smith")
                .state(State.CALIFORNIA)
                .age(17)
                .gpa(3.6)
                .gpaScale(4.0)
                .satScore(1921)
                .felonyDates(Collections.emptyList())
                .build();

        ApplicantStatus applicantStatus = applicantService.processApplicant(applicant);

        assertEquals(Classification.INSTANT_ACCEPT, applicantStatus.getClassification());
        assertNull(applicantStatus.getReason());

        verify(applicantValidator).validate(applicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }
}

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

    private void processApplicationTestHelper(Applicant applicant, Classification expectedClassification) {
        processApplicationTestHelper(applicant, expectedClassification, null);
    }

    private void processApplicationTestHelper(Applicant applicant, Classification expectedClassification, String expectedReason) {
        ApplicantStatus applicantStatus = applicantService.processApplicant(applicant);

        assertEquals(expectedClassification, applicantStatus.getClassification());
        if (expectedReason != null) {
            assertEquals(expectedReason, applicantStatus.getReason());
        } else {
            assertNull(applicantStatus.getReason());
        }

        verify(applicantValidator).validate(applicant);

        verify(applicantRepository).save(applicationArgumentCaptor.capture());
        assertEquals(applicantStatus, applicationArgumentCaptor.getValue().getApplicantStatus());
    }

    @Test
    void processApplicationShouldReturnFurtherReviewIfNeitherAcceptNorReject() {
        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldRejectIfMoreThanOneFelony() {
        defaultApplicant.setFelonyDates(Collections.singletonList(LocalDate.now()));

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant cannot have 1 or more felonies over the past 5 years");
    }

    @Test
    void processApplicationShouldRejectIfMoreThanOneFelonyLessThan5YearsAgo() {
        defaultApplicant.setFelonyDates(Collections.singletonList(LocalDate.now().minusYears(5).plusDays(1)));

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant cannot have 1 or more felonies over the past 5 years");
    }

    @Test
    void processApplicationShouldNotRejectIfNoFeloniesWithin5Years() {
        defaultApplicant.setFelonyDates(Collections.singletonList(LocalDate.now().minusYears(5)));

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldRejectIfGpaBelow70PercentWith4Scale() {
        defaultApplicant.setGpa(2.7);
        defaultApplicant.setGpaScale(4.0);

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant cannot have GPA below 70%");
    }

    @Test
    void processApplicationShouldNotRejectIfGpaEqualTo70PercentWith4Scale() {
        defaultApplicant.setGpa(2.8);
        defaultApplicant.setGpaScale(4.0);

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldRejectIfGpaBelow70PercentWith5Scale() {
        defaultApplicant.setGpa(3.4);
        defaultApplicant.setGpaScale(5.0);

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant cannot have GPA below 70%");
    }

    @Test
    void processApplicationShouldNotRejectIfGpaEqualTo70PercentWith5Scale() {
        defaultApplicant.setGpa(3.5);
        defaultApplicant.setGpaScale(5.0);

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldRejectIfAgeIsNegative() {
        defaultApplicant.setAge(-20);

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant cannot have a negative age");
    }

    @Test
    void processApplicationShouldNotRejectIfAgeZero() {
        defaultApplicant.setAge(0);

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldNotRejectIfAgeIsPositive() {
        defaultApplicant.setAge(1);

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldRejectIfFirstNameDoesNotHaveFirstLetterCapitalized() {
        defaultApplicant.setFirstName("joe");

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant must have a first name with the first letter capitalized, the rest lower case");
    }

    @Test
    void processApplicationShouldRejectIfFirstNameHasNonFirstLetterCapitalized() {
        defaultApplicant.setFirstName("joE");

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant must have a first name with the first letter capitalized, the rest lower case");
    }

    @Test
    void processApplicationShouldRejectIfFirstNameIsAllUppercase() {
        defaultApplicant.setFirstName("JOE");

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant must have a first name with the first letter capitalized, the rest lower case");
    }

    @Test
    void processApplicationShouldNotRejectIfFirstNameIs1Character() {
        defaultApplicant.setFirstName("J");

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldRejectIfLastNameDoesNotHaveFirstLetterCapitalized() {
        defaultApplicant.setLastName("smith");

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant must have a last name with the first letter capitalized, the rest lower case");
    }

    @Test
    void processApplicationShouldRejectIfLastNameHasNonFirstLetterCapitalized() {
        defaultApplicant.setLastName("smIth");

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant must have a last name with the first letter capitalized, the rest lower case");
    }

    @Test
    void processApplicationShouldRejectIfLastNameIsAllUppercase() {
        defaultApplicant.setLastName("SMITH");

        processApplicationTestHelper(defaultApplicant, Classification.INSTANT_REJECT,
                "Applicant must have a last name with the first letter capitalized, the rest lower case");
    }

    @Test
    void processApplicationShouldNotRejectIfLastNameIs1Character() {
        defaultApplicant.setLastName("S");

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldNotApproveIfJustInStateAgeRequirementPasses() {
        defaultApplicant.setState(State.CALIFORNIA);
        defaultApplicant.setAge(17);

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldNotApproveIfJustOutOfStateAgeRequirementPasses() {
        defaultApplicant.setAge(81);

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldNotApproveIfJustGpaRequirementPasses() {
        defaultApplicant.setGpa(3.6);
        defaultApplicant.setGpaScale(4.0);

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldNotApproveIfJustSatRequirementPasses() {
        defaultApplicant.setSatScore(1921);

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
    }

    @Test
    void processApplicationShouldNotApproveIfJustActRequirementPasses() {
        defaultApplicant.setActScore(28);

        processApplicationTestHelper(defaultApplicant, Classification.FURTHER_REVIEW);
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

        processApplicationTestHelper(applicant, Classification.INSTANT_ACCEPT);
    }
}

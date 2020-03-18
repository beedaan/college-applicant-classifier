package com.builtbybrendan.collegeApplicantClassifier.applicant;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicantService {

    private static final int ACCEPTABLE_FELONIES = 0;
    private static final int YEARS_OF_ACCEPTABLE_FELONIES = 5;
    private static final double UNACCEPTABLE_GPA_PERCENT = 0.7;

    private static final int MINIMUM_ACCEPTABLE_AGE = 17;
    private static final int MAXIMUM_ACCEPTABLE_AGE = 25;
    private static final int MINIMUM_ACCEPTABLE_AGE_OUT_OF_STATE = 81;
    private static final State IN_STATE = State.CALIFORNIA;
    private static final double MINIMUM_ACCEPTABLE_GPA = 0.9;
    private static final int MINIMUM_ACCEPTABLE_SAT_SCORE = 1921;
    private static final int MINIMUM_ACCEPTABLE_ACT_SCORE = 28;

    private ApplicantValidator applicantValidator = new ApplicantValidator();
    private ApplicantRepository applicantRepository = new DummyApplicantRepositoryImpl();

    public ApplicantStatus processApplicant(Applicant applicant) {
        applicantValidator.validate(applicant);

        ApplicantStatus applicantStatus = ApplicantStatus.builder()
                .classification(Classification.FURTHER_REVIEW)
                .build();

        if (isApplicantQualifiedForInstantAccept(applicant)) {
            applicantStatus.setClassification(Classification.INSTANT_ACCEPT);
        }

        String rejectReason = findInstantRejectReason(applicant);

        if (rejectReason != null) {
            applicantStatus = ApplicantStatus.builder()
                    .classification(Classification.INSTANT_REJECT)
                    .reason(rejectReason)
                    .build();
        }

        applicant.setApplicantStatus(applicantStatus);
        applicantRepository.save(applicant);
        return applicantStatus;
    }

    private String findInstantRejectReason(Applicant applicant) {
        String reason = null;

        if (doUnacceptableFeloniesExist(applicant)) {
            reason = String.format("Applicant cannot have %s or more felonies over the past %s years",
                    ACCEPTABLE_FELONIES + 1, YEARS_OF_ACCEPTABLE_FELONIES);
        } else if (isGpaUnacceptable(applicant)) {
            DecimalFormat df = new DecimalFormat("#%");
            reason = String.format("Applicant cannot have GPA below %s", df.format(UNACCEPTABLE_GPA_PERCENT));
        } else if (isAgeUnacceptable(applicant)) {
            reason = "Applicant cannot have a negative age";
        } else if (isNameIncorrectlyCapitalized(applicant.getFirstName())) {
            reason = "Applicant must have a first name with the first letter capitalized, the rest lower case";
        } else if (isNameIncorrectlyCapitalized(applicant.getLastName())) {
            reason = "Applicant must have a last name with the first letter capitalized, the rest lower case";
        }

        return reason;
    }

    private boolean doUnacceptableFeloniesExist(Applicant applicant) {
        LocalDate startOfUnacceptableFelonies = LocalDate.now().minusYears(YEARS_OF_ACCEPTABLE_FELONIES);

        List<LocalDate> feloniesWithinRecentHistory = applicant.getFelonyDates().stream()
                .filter(felonyDate -> felonyDate.isAfter(startOfUnacceptableFelonies))
                .collect(Collectors.toList());

        return feloniesWithinRecentHistory.size() > ACCEPTABLE_FELONIES;
    }

    private boolean isGpaUnacceptable(Applicant applicant) {
        double gpaPercent = applicant.getGpa() / applicant.getGpaScale();
        return gpaPercent < UNACCEPTABLE_GPA_PERCENT;
    }

    private boolean isAgeUnacceptable(Applicant applicant) {
        return applicant.getAge() < 0;
    }

    private boolean isNameIncorrectlyCapitalized(String name) {
        char[] nameArr = name.toCharArray();

        if (Character.isLowerCase(nameArr[0])) {
            return true;
        }

        for (int i = 1; i < nameArr.length; i++) {
            if (Character.isUpperCase(nameArr[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isApplicantQualifiedForInstantAccept(Applicant applicant) {
        return doesApplicantMeetAgeRequirements(applicant)
                && doesApplicantMeetGpaRequirement(applicant)
                && doesApplicantMeetStandardizedTestRequirement(applicant);
    }

    private boolean doesApplicantMeetAgeRequirements(Applicant applicant) {
        return (applicant.getState().equals(IN_STATE)
                && applicant.getAge() >= MINIMUM_ACCEPTABLE_AGE
                && applicant.getAge() <= MAXIMUM_ACCEPTABLE_AGE) ||
                applicant.getAge() >= MINIMUM_ACCEPTABLE_AGE_OUT_OF_STATE;
    }

    private boolean doesApplicantMeetGpaRequirement(Applicant applicant) {
        return applicant.getGpa() / applicant.getGpaScale() >= MINIMUM_ACCEPTABLE_GPA;
    }

    private boolean doesApplicantMeetStandardizedTestRequirement(Applicant applicant) {
        return applicant.getSatScore() >= MINIMUM_ACCEPTABLE_SAT_SCORE
                || applicant.getActScore() >= MINIMUM_ACCEPTABLE_ACT_SCORE;
    }
}

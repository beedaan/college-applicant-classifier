package com.builtbybrendan.collegeApplicantClassifier.application;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationService {

    private static final int ACCEPTABLE_FELONIES = 0;
    private static final int YEARS_OF_ACCEPTABLE_FELONIES = 5;
    private static final double UNACCEPTABLE_GPA_PERCENT = 0.7;

    private ApplicationValidator applicationValidator = new ApplicationValidator();
    private ApplicationRepository applicationRepository = new DummyApplicationRepositoryImpl();

    public ApplicationStatus processApplication(Application application) {
        applicationValidator.validate(application);

        ApplicationStatus applicationStatus = ApplicationStatus.builder()
                .classification(Classification.FURTHER_REVIEW)
                .build();

        String rejectReason = findInstantRejectReason(application);

        if (rejectReason != null) {
            applicationStatus = ApplicationStatus.builder()
                    .classification(Classification.INSTANT_REJECT)
                    .reason(rejectReason)
                    .build();
        }

        application.setApplicationStatus(applicationStatus);
        applicationRepository.save(application);
        return applicationStatus;
    }

    private String findInstantRejectReason(Application application) {
        String reason = null;

        if (doUnacceptableFeloniesExist(application)) {
            reason = String.format("Applicant cannot have %s or more felonies over the past %s years",
                    ACCEPTABLE_FELONIES + 1, YEARS_OF_ACCEPTABLE_FELONIES);
        } else if (isGpaUnacceptable(application)) {
            DecimalFormat df = new DecimalFormat("#%");
            reason = String.format("Applicant cannot have GPA below %s", df.format(UNACCEPTABLE_GPA_PERCENT));
        } else if (isAgeUnacceptable(application)) {
            reason = "Applicant cannot have a negative age";
        } else if (isNameIncorrectlyCapitalized(application.getFirstName())) {
            reason = "Applicant must have a first name with the first letter capitalized, the rest lower case";
        } else if (isNameIncorrectlyCapitalized(application.getLastName())) {
            reason = "Applicant must have a last name with the first letter capitalized, the rest lower case";
        }

        return reason;
    }

    private boolean doUnacceptableFeloniesExist(Application application) {
        LocalDate startOfUnacceptableFelonies = LocalDate.now().minusYears(YEARS_OF_ACCEPTABLE_FELONIES);

        List<LocalDate> feloniesWithinRecentHistory = application.getFelonyDates().stream()
                .filter(felonyDate -> felonyDate.isAfter(startOfUnacceptableFelonies))
                .collect(Collectors.toList());

        return feloniesWithinRecentHistory.size() > ACCEPTABLE_FELONIES;
    }

    private boolean isGpaUnacceptable(Application application) {
        double gpaPercent = application.getGpa() / application.getGpaScale();
        return gpaPercent < UNACCEPTABLE_GPA_PERCENT;
    }

    private boolean isAgeUnacceptable(Application application) {
        return application.getAge() < 0;
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
}

package com.builtbybrendan.collegeApplicantClassifier.application;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationService {

    public static final int ACCEPTABLE_FELONIES = 0;
    public static final int YEARS_OF_ACCEPTABLE_FELONIES = 5;

    public ApplicationStatus processApplication(Application application) {
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

        return applicationStatus;
    }

    private String findInstantRejectReason(Application application) {
        String reason = null;

        if (doUnacceptableFeloniesExist(application)) {
            reason = String.format("Applicant cannot have %s or more felonies over the past %s years",
                    ACCEPTABLE_FELONIES + 1, YEARS_OF_ACCEPTABLE_FELONIES);
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
}

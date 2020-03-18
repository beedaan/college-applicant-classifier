package com.builtbybrendan.collegeApplicantClassifier.application;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationService {

    private static final int ACCEPTABLE_FELONIES = 0;
    private static final int YEARS_OF_ACCEPTABLE_FELONIES = 5;

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

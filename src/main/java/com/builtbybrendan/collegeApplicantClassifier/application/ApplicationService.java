package com.builtbybrendan.collegeApplicantClassifier.application;

public class ApplicationService {

    public ApplicationStatus processApplication(Application application) {
        return ApplicationStatus.builder()
                .classification(Classification.FURTHER_REVIEW)
                .build();
    }
}

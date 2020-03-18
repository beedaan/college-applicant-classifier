package com.builtbybrendan.collegeApplicantClassifier.application;

public class ApplicationValidator {

    public void validate(Application application) {
        if (application.getGpaScale() < application.getGpa()) {
            throw new IllegalArgumentException("GPA cannot be greater than GPA Scale");
        } else if (application.getSatScore() == null && application.getActScore() == null) {
            throw new IllegalArgumentException("Must contain SAT Score, ACT Score, or both");
        } else if (application.getFirstName() == null || application.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First Name cannot be null or empty");
        } else if (application.getLastName() == null || application.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last Name cannot be null or empty");
        }
    }
}

package com.builtbybrendan.collegeApplicantClassifier.applicant;

public class ApplicantValidator {

    public void validate(Applicant applicant) {
        if (applicant.getGpaScale() < applicant.getGpa()) {
            throw new IllegalArgumentException("GPA cannot be greater than GPA Scale");
        } else if (applicant.getSatScore() == null && applicant.getActScore() == null) {
            throw new IllegalArgumentException("Must contain SAT Score, ACT Score, or both");
        } else if (applicant.getFirstName() == null || applicant.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First Name cannot be null or empty");
        } else if (applicant.getLastName() == null || applicant.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last Name cannot be null or empty");
        }
    }
}

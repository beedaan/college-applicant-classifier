package com.builtbybrendan.collegeApplicantClassifier.applicant;

public class ApplicantValidator {

    /**
     * Validate the Applicant.  Throw an {@link IllegalArgumentException} if the validation fails.  The following checks are performed:
     * <ul>
     *  <li>The GPA must be less than the GPA Scale
     *  <li>The applicant must contain either an SAT Score, an ACT Score, or both.
     *  <li>The First Name cannot be null or empty.
     *  <li>The Last Name cannot be null or empty.</li>
     * </ul>
     *
     * @param applicant the college applicant, and all of their application information
     */
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

package com.builtbybrendan.collegeApplicantClassifier.applicant;

public class DummyApplicantRepositoryImpl implements ApplicantRepository {
    @Override
    public Applicant save(Applicant applicant) {
        System.out.println(String.format("Saved the applicant %s", applicant.toString()));
        return null;
    }
}

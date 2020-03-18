package com.builtbybrendan.collegeApplicantClassifier.application;

public class DummyApplicationRepositoryImpl implements ApplicationRepository {
    @Override
    public Application save(Application application) {
        System.out.println(String.format("Saved application %s", application.toString()));
        return null;
    }
}

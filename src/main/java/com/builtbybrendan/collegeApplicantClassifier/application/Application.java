package com.builtbybrendan.collegeApplicantClassifier.application;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Application {

    public String firstName;
    public String lastName;
    public State state;
    public int age;
    public double gpa;
    public double gpaScale;
    public Integer satScore;
    public Integer actScore;
    public int felonies;
    public ApplicationStatus applicationStatus;

    @Builder
    @SuppressWarnings("unused")
    private static Application create(String firstName, String lastName, State state, int age, double gpa,
                                      double gpaScale, Integer satScore, Integer actScore, int felonies) {

        if (gpaScale < gpa) {
            throw new IllegalArgumentException("GPA cannot be greater than GPA Scale");
        } else if (satScore == null && actScore == null) {
            throw new IllegalArgumentException("Must contain SAT Score, ACT Score, or both");
        }

        Application application = new Application();
        application.setFirstName(firstName);
        application.setLastName(lastName);
        application.setState(state);
        application.setAge(age);
        application.setSatScore(satScore);
        application.setActScore(actScore);
        application.setFelonies(felonies);

        return application;
    }
}

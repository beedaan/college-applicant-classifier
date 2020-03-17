package com.builtbybrendan.collegeApplicantClassifier.application;

import com.builtbybrendan.collegeApplicantClassifier.applicationStatus.ApplicationStatus;
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
    public int satScore;
    public int actScore;
    public int felonies;
    public ApplicationStatus applicationStatus;

    @Builder
    private static Application create(String firstName, String lastName, State state, int age, double gpa,
                                      double gpaScale, int satScore, int actScore, int felonies) {
        Application application = new Application();
        application.setFirstName(firstName);
        application.setLastName(lastName);
        application.setState(state);
        application.setAge(age);
        application.setSatScore(satScore);
        application.setActScore(actScore);
        application.setFelonies(felonies);

        if (gpaScale < gpa) {
            throw new IllegalArgumentException("GPA cannot be greater than GPA Scale");
        }

        return application;
    }
}

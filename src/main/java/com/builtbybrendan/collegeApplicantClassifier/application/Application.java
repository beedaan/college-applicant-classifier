package com.builtbybrendan.collegeApplicantClassifier.application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
}

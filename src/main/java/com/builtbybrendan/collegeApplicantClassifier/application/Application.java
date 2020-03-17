package com.builtbybrendan.collegeApplicantClassifier.application;

import com.builtbybrendan.collegeApplicantClassifier.applicationStatus.ApplicationStatus;
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
    // TODO validate gpaScale is >= gpa
    public double gpaScale;
    public int satScore;
    public int actScore;
    public int felonies;
    public ApplicationStatus applicationStatus;
}

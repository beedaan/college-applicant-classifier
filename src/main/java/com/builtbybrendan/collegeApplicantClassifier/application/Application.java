package com.builtbybrendan.collegeApplicantClassifier.application;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

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
    public List<LocalDate> felonyDates;
    public ApplicationStatus applicationStatus;
}

package com.builtbybrendan.collegeApplicantClassifier.application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationStatus {

    public Classification classification;
    public String reason;
}
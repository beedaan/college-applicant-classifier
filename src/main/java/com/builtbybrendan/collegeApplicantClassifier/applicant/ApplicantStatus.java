package com.builtbybrendan.collegeApplicantClassifier.applicant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicantStatus {

    public Classification classification;
    public String reason;
}
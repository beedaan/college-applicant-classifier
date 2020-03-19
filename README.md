# College Applicant Classifier

## Overview

This project was a Software Engineering Candidate Project for Apex Systems.  It was built using TDD to write out the test cases first, then the business logic to make the tests pass.

*Description:* A local college here in California wants to save money by partially automating its applicant review process. In short:

* Some applicants can be accepted without further review by admissions staff ("instant accept").
* Other applicants can be safely rejected without further review by admissions staff ("instant
reject").
* The remaining applicants need "further review" by admissions staff, because they’re not an
"instant accept" nor an "instant reject."

## Acceptance Criteria

* To qualify as instant accept, all of the following criteria must be met.
    * In-state (California) age 17 or older, and younger than 26; or older than 80 from any state.
    * High School GPA of 90% or higher of scale provided in their application. For example, 3.6 on a 4.0 scale; or 4.5 on a 5.0 scale.
    *SAT score greater than 1920 or ACT score greater than 27. Note: Both, or only one of these, may be present in the applicant.
    * No "instant reject” criteria is hit (see below).
   
* All applicants can be subject to instant reject, if they meet any of the following criteria. Some of
    the criteria is dubious, admittedly, but the Dean insisted on it. 
    * 1 or more felonies over the past 5 years.
    * High School GPA below 70% of scale provided on application. For example, 2.8 on a 4.0
    scale.
    * The applicant claimed to be a negative age (it happens!) e.g. "-20” years old.
    * The applicant’s first and/or last name are not in the form of first letter capitalized, the
    rest lower case.
* If the candidate does not qualify for instant accept nor qualifies for instant reject, then they
    should be flagged for further review instead.

## Usage

The main entry point to process applicants is `ApplicantService.processApplicant()`.  It takes an `Applicant` as an argument and returns an `ApplicantStatus`.

An `Applicant` has the following properties

* firstName (String)
* lastName (String)
* state (State enum)
* age (int)
* gpa (double)
* gpaScale (double)
* satScore (Integer, nullable) 
* actScore (Integer, nullable)
* felonyDates (List\<LocalDate\>)
* applicantStatus (ApplicantStatus, nullable)

The applicantStatus is null until the applicant has been processed.  An applicationStatus is assigned to the applicant once they have been processed.

An `ApplicationStatus` has the following properties

* classification (Classification enum)
* reason (String, nullable)

Every applicationStatus has a classification.  Only applicationStatuses that have been instantly rejected have a reason.

## Future Work

Right now we just store a list of felonyDates.  It makes sense to create a `Felony` class and store more information about an applicant's felonies.  That way the admissions staff is able to further review the specifics of each felony, in case the Dean changes the amount of acceptable felonies to 2 or so before an instant reject.

Fields stored in the `ApplicationService` control the thresholds for the business logic.  They include:

* `ACCEPTABLE_FELONIES`
* `YEARS_OF_ACCEPTABLE_FELONIES`
* `UNACCEPTABLE_GPA_PERCENT`
* `MINIMUM_ACCEPTABLE_AGE`
* `MAXIMUM_ACCEPTABLE_AGE`
* `MINIMUM_ACCEPTABLE_AGE_OUT_OF_STATE`
* `IN_STATE`
* `MINIMUM_ACCEPTABLE_GPA`
* `MINIMUM_ACCEPTABLE_SAT_SCORE`
* `MINIMUM_ACCEPTABLE_ACT_SCORE`

We could enable the configuration of these values with `application.properties` or environment variables so that the end user could tweak these on their own. 
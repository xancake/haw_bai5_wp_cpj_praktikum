package _untouchable_.common;


@ClassPreamble (
    author          = "Michael Sch�fers",
    organization    = "Dept.Informatik; HAW Hamburg",
    date            = "2013/05/20",
    currentRevision = "0.93",
    lastModified    = "2012/07/19",
    lastModifiedBy  = "Michael Sch�fers",
    reviewers       = ( "none" )
)
enum ErrorType {                                                                // friendly on purpose
    
    ACTIVE_AFTER_LASTDEED_SMURF,
    ACTIVE_AFTER_LASTDEED_WOE,
    //
    FAULTY_BEHAVIOR_SMURF,
    FAULTY_BEHAVIOR_WOE,
    FAULTY_BEHAVIOR_SMURF_SCHEDULE,
    FAULTY_VISIBILITY_SMURF,
    FAULTY_VISIBILITY_WOE,
    //
    INCONSISTENCY_ID_SMURF,
    INCONSISTENCY_ID_WOE,
    INCONSISTENCY_LOCATION_SMURF,
    INCONSISTENCY_LOCATION_SMURF_S2EDS,
    INCONSISTENCY_LOCATION_WOE,
    INCONSISTENCY_LOCATION_WOE_W1MO1,
    INCONSISTENCY_LOCATION_WOE_W2MO2,
    INCONSISTENCY_LOCATION_WOE_W3AAP,
    INCONSISTENCY_LOCATION_WOE_W5WP1,
    INCONSISTENCY_LOCATION_WOE_W6WP2,
    INCONSISTENCY_LOCATION_WOE_W8VFP,
    //
    UNEXPECTED_ID_SMURF,
    UNEXPECTED_ID_WOE,
    UNEXPECTED_NUMBER_OF_SMURF,
    UNEXPECTED_NUMBER_OF_WOE,
    UNEXPECTED_NUMBER_OF_ID_SMURF,
    UNEXPECTED_NUMBER_OF_ID_WOE,
    UNEXPECTED_NUMBER_OF_LOCATION,
    UNEXPECTED_POSITION_SMURF,
    UNEXPECTED_POSITION_WOE,
    UNEXPECTED_POSITION_PARAMETER_SMURF,
    UNEXPECTED_POSITION_PARAMETER_WOE,

    
    
    OTHER
    
}//class

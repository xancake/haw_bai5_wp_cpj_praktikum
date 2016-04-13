package _untouchable_.common;

@ClassPreamble (
    author          = "Michael Sch�fers",
    organization    = "Dept.Informatik; HAW Hamburg",
    date            = "2012/07/19",
    currentRevision = "0.93",
    lastModified    = "2012/07/20",
    lastModifiedBy  = "Michael Sch�fers",
    reviewers       = ( "none" )
)
public abstract class CommonScheduleFactory {
    
    @ChunkPreamble ( lastModified="2012/07/20", lastModifiedBy="Michael Sch�fers" )
    public abstract CommonSchedule createSchedule();

}//class ScheduleFactory_A

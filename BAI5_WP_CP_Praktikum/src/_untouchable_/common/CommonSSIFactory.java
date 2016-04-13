package _untouchable_.common;


@ClassPreamble (
    author          = "Michael Sch�fers",
    organization    = "Dept.Informatik; HAW Hamburg",
    date            = "2013/05/20",
    currentRevision = "0.93",
    lastModified    = "2012/07/20",
    lastModifiedBy  = "Michael Sch�fers",
    reviewers       = ( "none" )
)
public abstract class CommonSSIFactory {
    
    @ChunkPreamble ( lastModified="2012/07/20", lastModifiedBy="Michael Sch�fers" )
    public abstract CommonSSI createSSI( final Integer position,  final Integer dwell );
    
}//class CommonSSIFactory

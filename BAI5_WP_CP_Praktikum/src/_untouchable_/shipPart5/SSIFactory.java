package _untouchable_.shipPart5;


import _untouchable_.common.*;


/**
 * This class is for internal use only.<br />
 * Do <strong><u>NOT(!)</u></strong> use this class (in your code directly).
 */
@ClassPreamble (
    author          = "Michael Sch�fers",
    organization    = "Dept.Informatik; HAW Hamburg",
    date            = "2013/05/20",
    currentRevision = "0.93",
    lastModified    = "2012/07/20",
    lastModifiedBy  = "Michael Sch�fers",
    reviewers       = ( "none" )
)
class SSIFactory extends CommonSSIFactory {                                     // friendly on purpose
    
    @ChunkPreamble ( lastModified="2012/07/23", lastModifiedBy="Michael Sch�fers" )
    @Override
    public CommonSSI createSSI( final Integer position,  final Integer dwell ){
        return new SSI( position, dwell );
    }//method()

}//class

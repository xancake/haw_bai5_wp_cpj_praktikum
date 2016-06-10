package a4.example;


import java.util.Collection;

import a4.api.Item_I;
import a4.api.SignatureProcessor_I;


/*
 *------------------------------------------------------------------------------
 * INF WP B3 AZ4 A4 SS16
 * 
 * VCS: git@BitBucket.org:schaefers/WPPPJ-CRC.git
 *                                                Michael Schaefers  2016/05/17
 *------------------------------------------------------------------------------
 */
public class SignatureProcessingStarter {
    
    public static void main( final String... unused ){
        final SignatureProcessor_I sp = new SignatureProcessor();
        
        final long timeStarted = System.nanoTime();
        final Collection<Item_I> signatureList =
            sp.computeSignatures(
                pathToRelatedFiles,
                filter
            );
        final long timeFinished = System.nanoTime();
        
        System.out.printf( "After %s:\n", Utility.nanoSecondBasedTimeToString( timeFinished - timeStarted ));
        System.out.printf( "\n" );
        System.out.printf( "1000000af 100400007 104c11db7 127673637 __file_size | file_name...\n" );
        System.out.printf( "-vvvvvvvv--vvvvvvvv--vvvvvvvv--vvvvvvvv-------------+----------~~~\n" );
        for( final Item_I elem : signatureList ){
            System.out.printf(
                " %08x  %08x  %08x  %08x%12d : %s\n",
                elem.getSignature( 0 ),
                elem.getSignature( 1 ),
                elem.getSignature( 2 ),
                elem.getSignature( 3 ),
                elem.getFileSize(),
                elem.getFileName()
            );
        }//for
    }//method()
    
    
    
    
    
    final static String pathToRelatedFiles = "Z:\\git\\haw_bai5_wp_cp_ln\\BAI5_WP_CP_Praktikum\\res\\img";             // path to the related files
    final static String filter = ".*\\.(J|j)(P|p)(E|e)?(G|g)$";                 // filter resp. regex e.g. ".*\\.(J|j)(P|p)(E|e)?(G|g)$"
    
}//class

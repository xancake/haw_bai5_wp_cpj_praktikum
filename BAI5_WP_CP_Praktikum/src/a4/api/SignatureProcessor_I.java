package a4.api;


import java.util.Collection;


/*
 *------------------------------------------------------------------------------
 * INF WP B3 AZ4 A4 SS16
 * 
 * VCS: git@BitBucket.org:schaefers/WPPPJ-CRC.git
 *                                                Michael Schaefers  2016/05/26
 *------------------------------------------------------------------------------
 */
/**
 * Das Interface {@link SignatureProcessor_I}
 * <ul>
 *     <li>beschreibt einen SignatureProcessor und</li>
 *     <li>definiert die Funktionalit&auml;t m&ouml;glicher Implementierungen und
 *         fordert die entsprechenden Methoden ein.</li>
 * </ul>
 * Die von Ihnen zu implementierende Klasse SignatureProcessor muss
 * <ul>
 *     <li>sich wie ein SignatureProcessor verhalten. Dies war Thema der Vorlesung.
 *     </li>
 * </ul>
 */
public interface SignatureProcessor_I {
    
    /**
     * Die Methode {@link #computeSignatures(String,String)} liefert eine {@link Collection}
     * &uuml;ber {@link Item_I}.
     * @param pathToRelatedFiles
     * @param filter
     * @return eine {@link Collection} &uuml;ber {@link Item_I}.
     */
    Collection<Item_I> computeSignatures(
        final String pathToRelatedFiles,                                        // path to the related files
        final String filter                                                     // filter resp. regex e.g. ".*\\.(J|j)(P|p)(E|e)?(G|g)$"
    );
    
    
    
    /*
    constructor():
    ==============
    SignatureProcessor();
    */
    
}//interface

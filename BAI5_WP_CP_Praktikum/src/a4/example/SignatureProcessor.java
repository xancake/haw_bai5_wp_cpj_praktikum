package a4.example;


import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import a4.api.Item;
import a4.api.Item_I;
import a4.api.SignatureProcessor_I;


/*
 *------------------------------------------------------------------------------
 * INF WP B3 AZ4 A4 SS16
 * 
 * VCS: git@BitBucket.org:schaefers/WPPPJ-CRC.git
 *                                                Michael Schaefers  2016/05/26
 *------------------------------------------------------------------------------
 */
public class SignatureProcessor implements SignatureProcessor_I {
    
    @Override
    public Collection<Item_I> computeSignatures(
        final String pathToRelatedFiles,
        final String filter
    ){
		File theDirectory = new File(pathToRelatedFiles);
		if(!theDirectory.isAbsolute()) {
			// Versuch die Pfadangabe relativ vom Klassenpfad aufzulösen, falls es sich um eine relative Pfadangabe handelt
			try {
				theDirectory = new File(ClassLoader.getSystemResource(pathToRelatedFiles).toURI());
			} catch (URISyntaxException ignore) {}
		}
		
        // do selfchecks
        if( ( ! theDirectory.exists())  ||  (! theDirectory.isDirectory()) ){
            throw new IllegalArgumentException(
                String.format(
                    "INVALID path: %s\n",
                    pathToRelatedFiles
                )
            );
        }//if
        
        
        final Collection<Item_I> coll = new ArrayList<>();
        final DemoAndReferenceProcessor drp = new DemoAndReferenceProcessor();
        
        
        for ( final File file : theDirectory.listFiles() ){
            final String fileName = file.getName();
            if( Pattern.matches( filter, fileName ) ){
                coll.add( new Item( fileName,  file.length(),  drp.computeSignatures( file )));
            }//if
        }//for
        
        return coll;
    }//method()
    
    
    
    public SignatureProcessor(){
    }//constructor()
    
}//class

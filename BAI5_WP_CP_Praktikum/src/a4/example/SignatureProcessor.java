package a4.example;


import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;

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
		File path = new File(pathToRelatedFiles);
		if(!path.isAbsolute()) {
			// Versuch die Pfadangabe relativ vom Klassenpfad aufzulösen, falls es sich um eine relative Pfadangabe handelt
			try {
				path = new File(ClassLoader.getSystemResource(pathToRelatedFiles).toURI());
			} catch (URISyntaxException ignore) {}
		}
		
        // do selfchecks
        if( ( ! path.exists())  ||  (! path.isDirectory()) ){
            throw new IllegalArgumentException(
                String.format(
                    "INVALID path: %s\n",
                    pathToRelatedFiles
                )
            );
        }//if
        
        return verarbeite(
        		path,
        		new DemoAndReferenceProcessor(),
        		(file) -> file.isDirectory() || file.getName().matches(filter)
        );
    }//method()
    
	private Collection<Item_I> verarbeite(File path, DemoAndReferenceProcessor processor, FileFilter filter) {
		Collection<Item_I> futures = new LinkedList<>();
		if(path.isFile()) {
			futures.add(new Item(path.getName(), path.length(), processor.computeSignatures(path)));
		} else {
			File[] filesOfDirectory = path.listFiles(filter);
			for(File file : filesOfDirectory) {
				futures.addAll(verarbeite(file, processor, filter));
			}
		}
		return futures;
	}
    
    
    
    public SignatureProcessor(){
    }//constructor()
    
}//class

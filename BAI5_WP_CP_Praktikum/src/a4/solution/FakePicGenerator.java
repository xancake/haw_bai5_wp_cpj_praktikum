package a4.solution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FakePicGenerator {
    
    static public void main( final String... unused ) throws IOException{
        
        final long reasonableSize = 100_000_000_000L;
        final long sizeAdaptedToQuota = 1_000_000_000L;
        final long sizeOfAllPics = sizeAdaptedToQuota;
        //
        final long theSeed = 13;
        final Random random = new Random( theSeed );
        //
        final String rootPath = "Z:\\TRIAL\\PICs\\";
        final Pattern pattern = Pattern.compile( "FakePic(\\d+)\\.[jJ][pP][gG]$" );
        
        
        
        int max = Integer.MIN_VALUE;                                            // max number of directory found
        final File rp = new File( rootPath );                                   // root path as file
        if( ! rp.isDirectory() ){
            throw new IllegalArgumentException( String.format( "%s is NO directory", rootPath ));
        }//if
        
        final File[] fa = rp.listFiles();                                       // array of files under root path
        if( null == fa ){
            throw new IllegalArgumentException( String.format( "%s is NO directory", rootPath ));
        }//if
        
        for( final File currentFile : fa ){
            if( currentFile.isFile() ){
                final Matcher m = pattern.matcher( currentFile.getCanonicalPath() );
                if( m.find() ){
                    final int tmp = Integer.parseInt( m.group(1) );
                    if( max < tmp )  max = tmp;
                }//if
            }//if
        }//for
        
        int currentNo = (max > 0 ) ? max +1 : 1 ;
        long size = 0;
        do{
            final int currentSize = 4_000_000 + ( random.nextInt() & 0x00FF_FFFF );
            final String fileName = String.format( "%sFakePic%d.jpg",  rootPath, currentNo );
          //System.out.printf( "%s %d\n",  fileName, currentSize );
            
            final File theFile = new File( fileName );
            final FileOutputStream fos = new FileOutputStream( theFile );
            int bytesStillToWrite=currentSize;
            do{
                final int blockSize = ( bytesStillToWrite > 65536 ) ? 65536 : bytesStillToWrite;
                final byte[] buffer = new byte[blockSize];
                random.nextBytes( buffer );
                fos.write( buffer );
                bytesStillToWrite-=blockSize;
            }while( bytesStillToWrite > 0 );
            fos.close();
            
            size += currentSize;
            currentNo += 1;
        }while( size < sizeOfAllPics ); 
        
    }//method()
    
}//class

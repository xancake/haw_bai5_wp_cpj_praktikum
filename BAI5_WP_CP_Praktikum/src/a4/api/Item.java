package a4.api;


import java.util.Arrays;


/*
 *------------------------------------------------------------------------------
 * INF WP B3 AZ4 A4 SS16
 * 
 * VCS: git@BitBucket.org:schaefers/WPPPJ-CRC.git
 *                                                Michael Schaefers  2016/05/26
 *------------------------------------------------------------------------------
 */
public class Item implements Item_I {
    
    @Override
    public String getFileName(){
        return theFileName;
    }//method()
    
    @Override
    public long getFileSize(){
        return theFileSize;
    }//method()
    
    @Override
    public int getSignature( final int signatureId ){
        return theSignature[signatureId];
    }//method()
    
    @Override
    public boolean equals( final Object otherObject ){
        if( this == otherObject )  return true;
        if( null == otherObject )  return false; 
        if( getClass()!=otherObject.getClass() )  return false;
        final Item otherItem = (Item)( otherObject );
        if( theFileSize != otherItem.theFileSize ) return false;
        if( isUnequal( theFileName, otherItem.theFileName )) return false;
        if( ! Arrays.equals( theSignature, otherItem.theSignature )) return false;
        return true;
    }//method()
    //
    private static boolean isUnequal( final Object objA,  final Object objB ){
        return (objA!=objB) && ((null==objA)||(!objA.equals(objB)));
    }//method()
    
    @Override
    public int hashCode(){
        final int prime = 31;
        int result = (int)( theFileSize ^ (theFileSize >>> 32) );
        result = prime * result + ((theFileName == null) ? 0 : theFileName.hashCode());
        result = prime * result + Arrays.hashCode( theSignature );
        return result;
    }//method()
    
    @Override
    public String toString(){
        return String.format(
            "[<%s>: %08x %08x %08x %08x%13d  %s]",
            Item.class.getSimpleName(),
            theSignature[0],
            theSignature[1],
            theSignature[2],
            theSignature[3],
            theFileSize,
            theFileName
        );
    }//method()
    
    
    
    
    
    public Item( final String theFileName,  final long theFileSize,  final int... theSignature ){
        this.theFileName = theFileName;
        this.theFileSize = theFileSize;
        this.theSignature = theSignature;
    }//constructor();
    
    
    
    
    
    private final String theFileName;
    private final long theFileSize;
    private final int[] theSignature;
    
}//class

package a4.example;


import java.util.Stack;


/*
 *------------------------------------------------------------------------------
 * INF WP B3 AZ4 A4 SS16
 * 
 * VCS: git@BitBucket.org:schaefers/WPPPJ-CRC.git
 *                                                Michael Schaefers  2016/05/17
 *------------------------------------------------------------------------------
 */
public class Utility {
    
    public static long mirror( long valueToBeMirrored, final int numberOfBits ){
        long resu = 0;
        for( int i=numberOfBits; i>0; i-- ){
            resu = ( (resu<<1) | ( valueToBeMirrored & 0x1) );
            valueToBeMirrored>>>=1;
        }//for
        return resu;
    }//method()
    //
    public static long mirror( final long valueToBeMirrored ){
        return mirror( valueToBeMirrored, numberOfBitsNeededForCoding( valueToBeMirrored ) );
    }//method()
    
    
    public static char numberOfBitsNeededForCoding( long valueToBeMeasured ){
        if( valueToBeMeasured<0 ){
            throw new IllegalArgumentException( "Only positive arguments are supported" );
        }//if
        //=> argument is valid
        
        char tmp=1;
        while( valueToBeMeasured>1 ){
            valueToBeMeasured>>>=1;
            tmp++;
        }//while
        return tmp;
    }//method()
    
    
    
    /**
     * Die Methode {@link #bitFieldStringOfValueLlrM()} liefert die Bits
     * des Werts &quot;value&quot; als String und zwar so interpretiert,
     * dass das <strong><u>L</u></strong>SB <strong><u>l</u></strong>inks und
     * <strong><u>r</u></strong>echts das <strong><u>M</u></strong>SB ist.
     * @param value Wert, der als String seiner einzelnen Bits berechnet werden soll.
     * @return  Die Bits des Werts &quot;value&quot; so interpretiert,
     *          dass das <strong><u>L</u></strong>SB <strong><u>l</u></strong>inks
     *          und <strong><u>r</u></strong>echts das <strong><u>M</u></strong>SB
     *          ist.
     */
    public static String bitFieldStringOfValue_LlrM( long value ){
        // parse bitField
        final Stack<Character> stack1 = new Stack<>();
        do{
            if( 0b1 == ( 0b1 & value )){
                stack1.push( '1' );
            }else{
                stack1.push( '0' );
            }//if
            value >>>= 1;
        }while( value != 0 );
        
        // add underscores between nibble borders
        final Stack<Character> stack2 = new Stack<>();
        int cnt = 0;
        loop:
        while(true){
            stack2.push( stack1.pop() );
            if( stack1.isEmpty() )  break loop;
            cnt++;
            if( 0b00 == ( cnt & 0b11 )){                                        // <=> if( 0 == cnt%4 )
                stack2.push( '_' );
            }//if
        }//while
        
        // generate string (builder)
        final StringBuilder sb = new StringBuilder();                           // there ain't no thread around ;-)
        while( ! stack2.isEmpty() ){
            sb.append( stack2.pop() );
        }//while
        
        return sb.toString();
    }//method()
    //
    public static String bitFieldStringOfValue_LlrM( long value,  final int numberOfBitsNeededForCoding ){
        if( 0>= numberOfBitsNeededForCoding ){
            throw new IllegalArgumentException();
        }//if
        //=> arguments are valid
        
        // parse bitField
        final Stack<Character> stack1 = new Stack<>();
        for( int stillToDO=numberOfBitsNeededForCoding;  --stillToDO>=0;  ){
            if( 0b1 == ( 0b1 & value )){
                stack1.push( '1' );
            }else{
                stack1.push( '0' );
            }//if
            value >>>= 1;
        }//for
        
        // add underscores between nibble borders
        final Stack<Character> stack2 = new Stack<>();
        int cnt = 0;
        loop:
        while(true){
            stack2.push( stack1.pop() );
            if( stack1.isEmpty() )  break loop;
            cnt++;
            if( 0b00 == ( cnt & 0b11 )){                                        // <=> if( 0 == cnt%4 )
                stack2.push( '_' );
            }//if
        }//while
        
        // generate string (builder)
        final StringBuilder sb = new StringBuilder();                           // there ain't no thread around ;-)
        while( ! stack2.isEmpty() ){
            sb.append( stack2.pop() );
        }//while
        
        return sb.toString();
    }//method()
    
    /**
     * Die Methode {@link #bitFieldStringOfValue_MlrL()} liefert die Bits
     * des Werts &quot;value&quot; als String und zwar so interpretiert,
     * dass das <strong><u>M</u></strong>SB <strong><u>l</u></strong>inks und
     * <strong><u>r</u></strong>echts das <strong><u>L</u></strong>SB ist.
     * @param value Wert, der als String seiner einzelnen Bits berechnet werden soll.
     * @return  Die Bits des Werts &quot;value&quot; so interpretiert,
     *          dass das <strong><u>M</u></strong>SB <strong><u>l</u></strong>inks
     *          und <strong><u>r</u></strong>echts das <strong><u>L</u></strong>SB
     *          ist.
     */
    public static String bitFieldStringOfValue_MlrL( long value ){
        // parse bitField
        final Stack<Character> stack = new Stack<>();
        do{
            if( 0b1 == ( 0b1 & value )){
                stack.push( '1' );
            }else{
                stack.push( '0' );
            }//if
            value >>>= 1;
        }while( value != 0 );
        
        // add underscores between nibble borders
        final StringBuilder sb = new StringBuilder();                           // there ain't no thread around ;-)
        int cnt = 0;
        loop:
        while(true){
            sb.append( stack.pop() );
            if( stack.isEmpty() )  break loop;
            cnt++;
            if( 0b00 == ( cnt & 0b11 )){                                        // <=> if( 0 == cnt%4 )
                sb.append( '_' );
            }//if
        }//while
        return sb.toString();
    }//method()
    //
    public static String bitFieldStringOfValue_MlrL( long value,  final int numberOfBitsNeededForCoding ){
        if( 0>= numberOfBitsNeededForCoding ){
            throw new IllegalArgumentException();
        }//if
        //=> arguments are valid
        
        // parse bitField
        final Stack<Character> stack = new Stack<>();
        for( int stillToDO=numberOfBitsNeededForCoding;  --stillToDO>=0;  ){
            if( 0b1 == ( 0b1 & value )){
                stack.push( '1' );
            }else{
                stack.push( '0' );
            }//if
            value >>>= 1;
        }//for
        
        // add underscores between nibble borders
        final StringBuilder sb = new StringBuilder();                           // there ain't no thread around ;-)
        int cnt = 0;
        loop:
        while(true){
            sb.append( stack.pop() );
            if( stack.isEmpty() )  break loop;
            cnt++;
            if( 0b00 == ( cnt & 0b11 )){                                        // <=> if( 0 == cnt%4 )
                sb.append( '_' );
            }//if
        }//while
        return sb.toString();
    }//method()
    
    
    
    
    
    static public String nanoSecondBasedTimeToString( final long nanoSeconds ){
        if ( nanoSeconds >= 1_000_000_000_000_000_000L ){
            return String.format(
                "%d.%03d.%03d.%03d,%03d.%03d.%03d[s]",
                nanoSeconds  /  1_000_000_000_000_000_000L,
               (nanoSeconds  /      1_000_000_000_000_000L) % 1_000L,
               (nanoSeconds  /          1_000_000_000_000L) % 1_000L,
               (nanoSeconds  /              1_000_000_000L) % 1_000L,
               (nanoSeconds  /                  1_000_000L) % 1_000L,
               (nanoSeconds  /                      1_000L) % 1_000L,
                nanoSeconds  %                      1_000L
            );
        }else if ( nanoSeconds >= 1_000_000_000_000_000L ){
            return String.format(
                "%d.%03d.%03d,%03d.%03d.%03d[s]",
                nanoSeconds  /  1_000_000_000_000_000L,
               (nanoSeconds  /      1_000_000_000_000L) % 1_000L,
               (nanoSeconds  /          1_000_000_000L) % 1_000L,
               (nanoSeconds  /              1_000_000L) % 1_000L,
               (nanoSeconds  /                  1_000L) % 1_000L,
                nanoSeconds  %                  1_000L
            );
        }else if ( nanoSeconds >= 1_000_000_000_000L ){
            return String.format(
                "%d.%03d,%03d.%03d.%03d[s]",
                nanoSeconds  /  1_000_000_000_000L,
               (nanoSeconds  /      1_000_000_000L) % 1_000L,
               (nanoSeconds  /          1_000_000L) % 1_000L,
               (nanoSeconds  /              1_000L) % 1_000L,
                nanoSeconds  %              1_000L
            );
        }else if ( nanoSeconds >= 1_000_000_000L ){
            return String.format(
                "%d,%03d.%03d.%03d[s]",
                nanoSeconds  /  1_000_000_000L,
               (nanoSeconds  /      1_000_000L) % 1_000L,
               (nanoSeconds  /          1_000L) % 1_000L,
                nanoSeconds  %          1_000L
            );
        }else if ( nanoSeconds >= 1_000_000L ){
            return String.format(
                "%d,%03d.%03d[ms]",
                nanoSeconds  /  1_000_000L,
               (nanoSeconds  /      1_000L) % 1_000L,
                nanoSeconds  %      1_000L
            );
        }else if ( nanoSeconds >= 1_000L ){
            return String.format(
                "%d,%03d[us]",
                nanoSeconds  /  1_000L,
                nanoSeconds  %  1_000L
            );
        }else{
            return String.format(
                "%d[ns]",
                nanoSeconds
            );
        }//if
    }//method()
    
    
    
    
    
    public static String generateWhiteSpace( final int whiteSpaceLength ){
        final StringBuffer sb = new StringBuffer();
        for( int i=whiteSpaceLength; --i>=0; ){
            sb.append( " " );
        }//for
        return sb.toString();
    }//method()
    
}//class

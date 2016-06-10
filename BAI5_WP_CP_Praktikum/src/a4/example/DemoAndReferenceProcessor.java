package a4.example;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/*
 *------------------------------------------------------------------------------
 * INF WP B3 AZ4 A4 SS16
 * 
 * VCS: git@BitBucket.org:schaefers/WPPPJ-CRC.git
 *                                                Michael Schaefers  2016/05/17
 *------------------------------------------------------------------------------
 */
public class DemoAndReferenceProcessor {
    
    public int[] computeSignatures( final File file ){
        final long fileSize = file.length();
        long bytesStillToCompute = (7 + fileSize) & ~0b11L;
        bytesStillToCompute = ( (bytesStillToCompute/12) + ( (bytesStillToCompute%12)>0 ? 1 : 0 )) *12;
        //
        int rc;                                                                 // Return Code
        //
        final int[] signature = new int[numberOfPolynomials];
        for( int index=numberOfPolynomials;  --index>=0;  )  signature[index] = 0;
        
        try(
            final FileInputStream fis  = new FileInputStream( file );
        ){
            if( 4 <= file.length() ){
                // NO "ugly" special case
                
                loop:
                while(true){
                    rc = fis.read( readBuffer, 0, amount64Ki );
                    if( amount64Ki > rc )  break loop;
                    
                    int bufferIndex=0;
                    while( amount64Ki >  bufferIndex ){
                        int word16bit = (0xff & readBuffer[bufferIndex+1])<<8  |  (0xff & readBuffer[bufferIndex]);
                        
                        for( int polyId = numberOfPolynomials;  --polyId>=0;  ){
                            final int index16bitLUT = signature[polyId] & 0xFFFF;   // 16bit look-up, hence 16 bits required
                            final int valueLUT = lut[polyId][ index16bitLUT ];
                            signature[polyId] = (signature[polyId] >>> 16) ^ valueLUT ^ (word16bit<<16);
                        }//for
                        
                        bufferIndex+=2;
                    }//while
                    bytesStillToCompute -= amount64Ki;
                }//while
                
                if( -1 == rc )  rc=0;
                for( int index=rc; index<bytesStillToCompute; index++ )  readBuffer[index] = 0;
                
                int bufferIndex=0;
                while( bytesStillToCompute >  bufferIndex ){
                    int word16bit = (0xff & readBuffer[bufferIndex+1])<<8  |  (0xff & readBuffer[bufferIndex]);
                    
                    for( int polyId = numberOfPolynomials;  --polyId>=0;  ){
                        final int index16bitLUT = signature[polyId] & 0xFFFF;   // 16bit look-up, hence 16 bits required
                        final int valueLUT = lut[polyId][ index16bitLUT ];
                        signature[polyId] = (signature[polyId] >>> 16) ^ valueLUT ^ (word16bit<<16);
                    }//for
                    bufferIndex+=2;
                }//while
                bytesStillToCompute -= amount64Ki;
                
            //##################################################################
            }else{
                //=> the very special case
                
                // handle the only block (of file)
                rc = fis.read( readBuffer, 0, amount64Ki );
                
                int theCompleteData = 0;
                switch ( rc ){
                    case -1: // file is empty => signature is zero
                        theCompleteData = 0;
                        return signature;
                      //break;
                    
                    case 1: // file contains only 1 byte
                        theCompleteData =         0
                        |  (readBuffer[0] & 0xff);                              // ..__.._.._.._B0  <- Byte 0
                      break;
                    
                    case 2: // file contains only 2 bytes
                        theCompleteData =           0
                        |  (readBuffer[0] & 0xff)                               // ..__.._.._.._B0  <- Byte 0
                        | ((readBuffer[1] & 0xff) <<  8);                       // ..__.._.._B1_..  <- Byte 1
                     break;
                    
                    case 3: // file contains only 3 bytes
                        theCompleteData =           0
                        |  (readBuffer[0] & 0xff)                               // ..__.._.._.._B0  <- Byte 0
                        | ((readBuffer[1] & 0xff) <<  8)                        // ..__.._.._B1_..  <- Byte 1
                        | ((readBuffer[2] & 0xff) << 16);                       // ..__.._B2_.._..  <- Byte 2
                      break;
                    
                    case 4: // file contains 4 bytes
                        theCompleteData =           0
                        |  (readBuffer[0] & 0xff)                               // ..__.._.._.._B0  <- Byte 0
                        | ((readBuffer[1] & 0xff) <<  8)                        // ..__.._.._B1_..  <- Byte 1
                        | ((readBuffer[2] & 0xff) << 16)                        // ..__.._B2_.._..  <- Byte 2
                        | ((readBuffer[3] & 0xff) << 24);                       // ..__B3_.._.._..  <- Byte 3
                      break;
                    
                    default:
                        throw new IllegalStateException( "UUUPPS how could this happen - did not expect to end up here" );
                      //break;
                }//switch
                //=> special case "empty / zero length" already handled
                //   in case of 1-4 bytes => 12 bytes have to be computed
                
                int bufferIndex=0;
                while( 12 > bufferIndex ){
                    for( int polyId = numberOfPolynomials;  --polyId>=0;  ){
                        final int index16bitLUT = signature[polyId] & 0xFFFF;   // 16bit look-up, hence 16 bits required
                        final int valueLUT = lut[polyId][ index16bitLUT ];
                        signature[polyId] = (signature[polyId] >>> 16) ^ valueLUT ^ ((0xffff & theCompleteData)<<16);
                    }//while
                    theCompleteData >>>= 16;
                    bufferIndex+=2;
                }//for
            }//if
        }catch( final FileNotFoundException ex ){
            // TODO: react wise and clever ;-)
            ex.printStackTrace();
        }catch( final IOException ex ){
            // TODO: react wise and clever ;-)
            ex.printStackTrace();
        }//try
        
        return signature;
    }//method()
    
    
    
    
    
    // ATTENTION!
    // This constructor sets up object specific and class specific stuff
    public DemoAndReferenceProcessor(){
        // check arguments
        if( numberOfPolynomials != NAME_OF_POLYNOMIAL.values().length ){
            throw new IllegalStateException();
        }//if
        if( numberOfPolynomials != originalPolynomial.length ){
            throw new IllegalStateException();
        }//if
        for( int indx=originalPolynomial.length;  --indx>=0;  ){
            if( expectedPolynomialBitSize != Utility.numberOfBitsNeededForCoding( originalPolynomial[indx] )){
                throw new IllegalStateException();
            }//if
        }//for
        //=>constant definitions are NOT obviously inconsistent
        
        
        
        // construct object initial state
        for( int polyId=lut.length;  --polyId>=0;  ){ 
            final long poly = originalPolynomial[polyId];
            lut[polyId] = new int[amount64Ki];
            
            
            // working with original polynomial
            for( int indx=amount64Ki;  --indx>=0;  ){
                long signature = indx;
                // da mit dem Original-Polynom gearbeitet wird, muss erst verknuepft und dann geSHIFTet werden - quasi die "Original-Idee"
                for( int stillToDo=lookup16bit;  --stillToDo>=0;  ){            // 16 bit look-bit means 16 bits have to be computed for look-up
                    if( 0b1 == ( signature & 0b1 )){
                        signature ^= poly;
                    }//if
                    signature >>>= 1;
                }//for
                
                lut[polyId][indx] = (int)( signature );
                
                if( enSelfTest ){
                    final long toBigFor32Bit = 1L << 32;
                    if( signature >= toBigFor32Bit ){
                        throw new IllegalStateException( String.format( "--->>>ERR: invalid signature occurred : %d\n", signature ));
                    }//if
                }//if
            }//for
            
        }//for
        
        if( enSelfTest ) selfTestCheckLUTs();
    }//constructor()
    
    
    
    private void selfTestCheckLUTs(){
        // Wenn ALLES richtig gemacht wurde, dann darf es KEINE Doppelten in der (jeweiligen) LUT geben
        // (Notwendiges Kriterium aber NICHT hinreichend)
        
        for( int polyId=lut.length;  --polyId>=0;  ){ 
            
            // alternative1 for selfcheck
            // ============
            final int numberOfRequiredElements = (int)((1L << 32) >>> 6 );      // <- 67108864 =
            final long[] marker = new long[numberOfRequiredElements];
            for( int i=marker.length;  --i>=1;  )  marker[i] = 0;
            loop:
            for( final int elem : lut[polyId] ){
                int index  = elem >>> 6;
                int bitPos = elem & 0b0011_1111;
                long bit = 0b1L << bitPos;
              //if( bitPos<0 || 63<bitPos ){ System.out.printf( "\n\n--->>>ERR: %d -> %d<<<---\n\n",  bitPos, bit );  break loop; }
                if( bit == ( marker[index] & bit )){
                    System.out.printf(
                        "ERR: %d  ->  %d:%d  {%016x}\n",
                        elem,
                        index,
                        bitPos,
                        marker[index]
                    );
                    break loop;
                }//if
                marker[index] |= bit;
            }//for
            //
            // Der erste Eintrag (der Eintrag fuer 0) muss 0 sein
            if( 0 != lut[polyId][0] ){
                System.out.printf(
                    "ERR: LUT[0]=0 != %d\n",
                    lut[polyId][0]
                );
            }//if
            
            // alternative2 for selfcheck
            // ============
            final Set<Integer> markerSet = new HashSet<>();
            loop:
            for( final int elem : lut[polyId] ){
                if( ! markerSet.add( elem )){
                    System.out.printf(
                        "ERR: %d  occured twice\n",
                        elem
                    );
                    break loop;
                }//if
            }//for
        }//for
        
    }//method()
    
    
    
    
    
    final boolean enSelfTest =  true;
    
    
    
    
    
    // Vieles vom Folgenden wird NICHT gebraucht, aber als "Angebot" ist es mit aufgefuehrt.
    // Um ein einheitliches Naming sicherzustellen, bitte die entsprechenden Namen verwenden,
    // wenn auch die entsprechenden Dinge verwendet werden.
    static private enum NAME_OF_POLYNOMIAL { A, B, C, D };
    final static long polynomialA = 0x1000000afL;                               // 32 bit polynomial with MSB on left side and on right side LSB
    final static long polynomialB = 0x100400007L;                               // 32 bit polynomial with MSB on left side and on right side LSB
    final static long polynomialC = 0x104c11db7L;                               // 32 bit polynomial with MSB on left side and on right side LSB
    final static long polynomialD = 0x127673637L;                               // 32 bit polynomial with MSB on left side and on right side LSB
    final static int A = NAME_OF_POLYNOMIAL.A.ordinal();
    final static int B = NAME_OF_POLYNOMIAL.B.ordinal();
    final static int C = NAME_OF_POLYNOMIAL.C.ordinal();
    final static int D = NAME_OF_POLYNOMIAL.D.ordinal();
    final static long[] originalPolynomial = {
        Utility.mirror( polynomialA ),                                          // the 32 bit polynomial "A" with LSB on left side and on right side MSB
        Utility.mirror( polynomialB ),                                          // the 32 bit polynomial "B" with LSB on left side and on right side MSB
        Utility.mirror( polynomialC ),                                          // the 32 bit polynomial "C" with LSB on left side and on right side MSB
        Utility.mirror( polynomialD )                                           // the 32 bit polynomial "D" with LSB on left side and on right side MSB
    };
    final static long[] adaptedPolynomial = {
        originalPolynomial[A] >>> 1,                                            // the adapted 32 bit polynomial "A" with LSB on left side and on right side MSB, the original MSB is already shifted out
        originalPolynomial[B] >>> 1,                                            // the adapted 32 bit polynomial "B" with LSB on left side and on right side MSB, the original MSB is already shifted out
        originalPolynomial[C] >>> 1,                                            // the adapted 32 bit polynomial "C" with LSB on left side and on right side MSB, the original MSB is already shifted out
        originalPolynomial[D] >>> 1                                             // the adapted 32 bit polynomial "D" with LSB on left side and on right side MSB, the original MSB is already shifted out
    };
    final static int numberOfPolynomials = originalPolynomial.length;
    final static int expectedPolynomialBitSize = 33;                            // 32, .., 0 resp. 0, .., 32
    //
    // 16 bit look-up table
    final static int lookup16bit = 16;                                          //
    final static int amount64Ki = 1 << 16;                                      // 2^16 = 65_536
    final static int lutSizeFor16bit = ((amount64Ki/12) + ((amount64Ki%12) > 0  ?  1  :  0 )) *12;
    //
    // 24 bit look-up table
    final static int lookup24bit = 24;                                          //
    final static int amount24Mi = 1 << 24;                                      // 2^24 = 16_777_216
    
    final static int[][] lut = new int[originalPolynomial.length][];            // Look-Up Table
    final static int[] lutA = lut[A];                                           // Look-Up Table for polynomial "A"
    final static int[] lutB = lut[B];                                           // Look-Up Table for polynomial "B"
    final static int[] lutC = lut[C];                                           // Look-Up Table for polynomial "C"
    final static int[] lutD = lut[D];                                           // Look-Up Table for polynomial "D"
    
    
    
    final static byte[] readBuffer = new byte[lutSizeFor16bit];                 // read buffer for file access
    
}//class

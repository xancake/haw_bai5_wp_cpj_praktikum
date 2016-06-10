package a4.demo;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/*
 *------------------------------------------------------------------------------
 * INF WP B3 AZ4 A4 SS16
 * 
 * VCS: git@BitBucket.org:schaefers/WPPPJ-CRC.git
 *                                                Michael Schaefers  2016/05/17
 *------------------------------------------------------------------------------
 */
/**
 * Die Klasse {@link DemoVerySimpleSlowSequentialProcessor} ist eine Demo-Klasse
 * zur Veranschaulichung der CRC-Berechnung. Die Klasse versucht mit h&ouml;chster
 * Priorit&auml;t die Berechnung <strong>einfach</strong> umzusetzen. In der
 * Konsequenz ist die Berechnung <u>nicht</u> optimiert und sowohl langsam als
 * auch unn&ouml;tig aufwendig.
 */
public class DemoVerySimpleSlowSequentialProcessor {
    
    public long computeCRC( final File file ){
        
        final int amount64KiB = 65536;
        final int moreThanEnough = 16;                                          // 7 would be the worst case number
        final byte[] readBuffer = new byte[amount64KiB +moreThanEnough];
        
        long bytesStillToCompute = (7 + file.length()) & ~0b11L;
        bytesStillToCompute = ( (bytesStillToCompute/12) + ( (bytesStillToCompute%12)>0 ? 1 : 0 )) *12;
        
        int rc;                                                                 // Return Code
        
        try(
            final FileInputStream fis  = new FileInputStream( file );
        ){
            if( 4 <= file.length() ){
                // NO "ugly" special case
                
                
                // handle first block (of file)
                
                rc = fis.read( readBuffer, 0, amount64KiB );
                
                // fill up with zeros at the end of buffer just in case they are required
                for( int i=rc;  i<rc+moreThanEnough;  i++ ){  readBuffer[i] = 0x00; }
                
                // cs is/was initialized with zero
                handleChunk( readBuffer, rc, 0 );                               // ATTENTION(!): the last 4 bytes are NOT computed yet - but it's the way it is meant to be
                bytesStillToCompute = bytesStillToCompute -rc;
                
                while( rc == amount64KiB ){
                    // the last chunk was a whole block => hence, there is some left to read
                    
                    // read next chunk
                    rc = fis.read( readBuffer, 0, amount64KiB );
                    final int positionAfterLastByteRead = (-1 != rc) ? rc : 0;
                    
                    // fill up with zeros at the end of buffer just in case they are required
                    for( int i=positionAfterLastByteRead;  i<positionAfterLastByteRead+7;  i++ ){  readBuffer[i] = 0x00; }
                    
                    handleChunk( readBuffer, rc, 0 );                           // NOTE: the last 4 bytes are NOT computed yet
                    bytesStillToCompute = bytesStillToCompute -rc;
                    
                }//while
                //=> the last chunk was NOT(!) a whole block
                
                
                int startPosition;
                if( -1 != rc ){
                    // there are some byte in the buffer, but some trailing zero bytes are missing
                    
                    startPosition = rc;
                }else{
                    // end of file reached - file size was multiple of 64KiB
                    //-> but 4 trailing zero bytes are still missing to finish computation of CRC
                    startPosition = 0;
                }//if
                
                for( int i=0; i<bytesStillToCompute; i++ ){ readBuffer[startPosition+i]=0; }
                handleChunk( readBuffer,  (int)( bytesStillToCompute + startPosition ),  startPosition );
                
                
            //##################################################################
            }else{
                //=> the very special case
                
                // handle the only block (of file)
                rc = fis.read( readBuffer, 0, amount64KiB );
                
                switch ( rc ){
                    case -1: // file is empty => signature is zero
                        cs = 0L;
                        return cs;
                      //break;
                    
                    case 1: // file contains only 1 byte
                        cs =         0L
                        |  (readBuffer[0] & 0xff);                              // ..__.._.._.._B0  <- Byte 0
                      break;
                    
                    case 2: // file contains only 2 bytes
                        cs =           0L
                        |  (readBuffer[0] & 0xff)                               // ..__.._.._.._B0  <- Byte 0
                        | ((readBuffer[1] & 0xff) <<  8);                       // ..__.._.._B1_..  <- Byte 1
                     break;
                    
                    case 3: // file contains only 3 bytes
                        cs =           0L
                        |  (readBuffer[0] & 0xff)                               // ..__.._.._.._B0  <- Byte 0
                        | ((readBuffer[1] & 0xff) <<  8)                        // ..__.._.._B1_..  <- Byte 1
                        | ((readBuffer[2] & 0xff) << 16);                       // ..__.._B2_.._..  <- Byte 2
                      break;
                    
                    case 4: // file contains 4 bytes
                        cs =           0L
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
                //   in case of 1-4 bytes => 8 bytes have to be computed
                
                for( int i=0; i<8; i++ ){
                    // handle current byte
                    for( int bitPosition=0;  bitPosition<8;  bitPosition++ ){
                        if( 0b1 == (0b1 & cs)){
                            // MSB of cs is set
                            
                            cs ^= poly;
                        }//if
                        cs = cs >>> 1;
                    }//for
                }//for
                
            }//if
            
        }catch( final FileNotFoundException ex ){
            // TODO: react wise and clever ;-)
            ex.printStackTrace();
        }catch( final IOException ex ){
            // TODO: react wise and clever ;-)
            ex.printStackTrace();
        }//try
        
        return cs;
    }//method()
    
    
    
    private void handleChunk( final byte[] buffer,  final int numberOfBytes,  final int startPosition ){
        //
        for( int bufferIndex=startPosition; bufferIndex<numberOfBytes; bufferIndex++ ){
            
            // put next byte at end (according to our "signature thinking" the least significant position)
            cs |= ((0xffL & buffer[bufferIndex]) << 32);
            
            // handle current byte
            for( int bitPosition=0;  bitPosition<8;  bitPosition++ ){
                if( 0b1 == (0b1 & cs)){
                    // MSB of cs is set
                    
                    cs ^= poly;
                }//if
                cs = cs >>> 1;
            }//for
        }//for
        //
        // ATTENTION: the "last 4 byte" are not fully computed yet  (at least related to numberOfBytes)
    }//method()
    
    
    
    
    
    /**
     * 
     */
    public DemoVerySimpleSlowSequentialProcessor(
        final long thePolynomial_LlrM                                                // the very polynomial with MSB on rigth side (bit position 0)
    ){
        if(( 0b1 != (0b1 & thePolynomial_LlrM )) || (1 == thePolynomial_LlrM )){
            throw new IllegalArgumentException( "INVALID polynomial" );
        }//if
        //=> at least polynomial copes with simplest requirements
        
        
        this.poly = thePolynomial_LlrM;
        this.cs = 0;
    }//constructor()
    
    
    
    
    
    final long poly;                                                            // the very polynomial with MSB on rigth side (bit position 0)  and on left side LSB
    long cs;                                                                    // Current Signature
    
}//class

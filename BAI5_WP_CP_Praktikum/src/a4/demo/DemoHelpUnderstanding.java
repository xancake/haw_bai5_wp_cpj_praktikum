package a4.demo;
// VCS: git@bitbucket.org:schaefers/wpppj-crc.git


import java.util.Random;

import a4.example.Utility;


/*
 *------------------------------------------------------------------------------
 * INF WP B3 AZ4 A4 SS16
 * 
 * VCS: git@BitBucket.org:schaefers/WPPPJ-CRC.git
 *                                                Michael Schaefers  2016/05/26
 *------------------------------------------------------------------------------
 */
// Demo f�r 4Bit CRC (bzw. 5Bit Prim-Polynome)
@SuppressWarnings("unused")
public class DemoHelpUnderstanding {
    
    public static void main( final String... unused ){
        
        // generate message           value-LlrM               value-MlrL_         byte-order
        final long message_B3oooooooooooooo_LlrM =                   0xcdL;     // CD
        final long message_C7E3oooooooooooo_LlrM =                 0xc7e3L;     // e3-c7
        final long message_45C37Eoooooooooo_LlrM =              0x7e_c3a2L;     // a2-c3-7e
        final long message_FFFFFFFFoooooooo_LlrM =            0xffff_ffffL;     // ff-ff-ff-ff
        final long message_80C4A2E691oooooo_LlrM =        0x89__6745_2301L;     // 01-23-45-67-89
        final long message_D5B3F7D5B3F7oooo_LlrM =      0xEFCD__ABEF_CDABL;     // ab-cd-ef-ab-cd-ef
        final long message_00000000000000oo_LlrM =   0x00_0000__0000_0000L;     // 00-00-00-00-00-00-00
        final long message_7F3B5D196E2A4C08_LlrM = 0x1032_5476__98BA_DCFEL;     // FE-DC-BA-98-76-54-32-10
        //
        //~~~~~~~~~~~~~~~~~~~VVVVVVVVVVVVVVVVVVVVVVVVVVVVV~~~~~~~~~~~~~~~~~~~~~~
        final long message = message_B3oooooooooooooo_LlrM;
        
        
        // set the very polynomial
        final int primPolynomialFor4Bit_0x13_LlrM = 0b11001;                    // MSB on left side and on right side LSB
        final int primPolynomialFor4Bit_0x19_LlrM = 0b10011;                    // MSB on left side and on right side LSB
        //
        final int primPolynomialFor4Bit_0x13_MlrL = 0b10011;                    // MSB on left side and on right side LSB
        final int primPolynomialFor4Bit_0x19_MlrL = 0b11001;                    // MSB on left side and on right side LSB
        //
        final int primPolynomialFor4Bit_MlrL = primPolynomialFor4Bit_0x13_MlrL;
        final int primPolynomialFor4Bit_LlrM = mirrorPolynomial( primPolynomialFor4Bit_MlrL );
        //
        //~~~~~~~~~~~~~~~~~~~VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV~~~~~~~~~~~~~~~~~~~~
        final int thePolynomial = primPolynomialFor4Bit_LlrM;                   // polynomial with LSB on left side and on right side MSB
        final int adaptedPrimPoly = ( 0xf & ( thePolynomial >>> 1 ));           // "Adapted PRIMe POLYnomial" bereits ein shift - "MSB" (das durch Spiegelung LSB ist) fehlt / fliegt raus
        assert adaptedPrimPoly != 0x00 : "illegal state";
        
        
        final int mask1bit = 0b0000_0001;
        final int mask2bit = 0b0000_0011;
        final int mask3bit = 0b0000_0111;
        final int mask4bit = 0b0000_1111;
        
        
        
        // debug settings -> enable/disable printing of information
        final boolean detailedPrinting1bit = true;
        final boolean detailedPrinting2bit = true;
        final boolean detailedPrinting3bit = true;
        final boolean detailedPrinting4bit = true;
        
        
        
        
        
        //######################################################################
        //###
        //###   setup LUTs
        //###   =====
        //###
        //
        int currentSignature = 0;
        
        
        // 1 bit LUT setup
      //currentSignature = 0;                                                   // current signature (for computation of look up table)
        final int[] lut1bit = new int[2];                                       // Tabelle ueber jede Kombination von 1 Nachrichten-Bits
        for( int indx=2; --indx>=0; ){
            currentSignature = indx;                                            // 2 bits maximum wegen max Index - nur zur Sicherheit/unn�tig
            for( int untouchedBits=1; --untouchedBits>=0; ){                    // Jedes  Bit ( das Eine ;-) verrechnen
                if( 0x01 == (currentSignature & 0x01) ){
                    currentSignature ^= thePolynomial;                          // 5 bits ;  "HIER" muss das ganze Polynom verwendet werden
                }//if
                currentSignature >>>= 1;                                        // 4 bits ;  eigentlich: currentSignature = (currentSignature>>>1) | (newBit &0x1)<<4 => die neuen Bits m�ssen (nachher) noch reingeXORt werden
            }//for
            lut1bit[indx] = currentSignature;
        }//for
        
        
        // 2 bit LUT setup
      //currentSignature = 0;                                                   // current signature (for computation of look up table)
        final int[] lut2bit = new int[4];                                       // Tabelle ueber jede Kombination von 2 Nachrichten-Bits
        for( int indx=4; --indx>=0; ){
            currentSignature = indx;                                            // 2 bits maximum wegen max Index - nur zur Sicherheit/unn�tig
            for( int untouchedBits=2; --untouchedBits>=0; ){                    // Jedes Bit (der Beiden) verrechnen
                if( 0x01 == (currentSignature & 0x01) ){
                    currentSignature ^= thePolynomial;                          // 5 bits ;  "HIER" muss das ganze Polynom verwendet werden
                }//if
                currentSignature >>>= 1;                                        // 4 bits ;  eigentlich: currentSignature = (currentSignature>>>1) | (newBit &0x1)<<4 => die neuen Bits m�ssen (nachher) noch reingeXORt werden
            }//for
            lut2bit[indx] = currentSignature;
        }//for
        
        
        // 3 bit LUT setup
        /* OK
        currentSignature = 0;                                                   // current signature (for computation of look up table)
        final int[] lut3bit = new int[8];                                       // Tabelle ueber jede Kombination von 4 Nachrichten-Bits
        for( int indx=lut3bit.length; --indx>=0; ){
            currentSignature = indx;                                            // 3 bits maximum wegen max Index - nur zur Sicherheit/unn�tig
            for( int untouchedBits=3; --untouchedBits>=0; ){                    // Jedes Bit (der Beiden) verrechnen
                if( 0x01 == (currentSignature & 0x01) ){
                    currentSignature ^= thePolynomial;                          // 5 bits ;  "HIER" muss das ganze Polynom verwendet werden
                }//if
                currentSignature >>>= 1;                                        // 4 bits ;  eigentlich: currentSignature = (currentSignature>>>1) | (newBit &0x1)<<4 => die neuen Bits m�ssen (nachher) noch reingeXORt werden
            }//for
            lut3bit[indx] = currentSignature;
        }//for
        */
      //currentSignature = 0;                                                   // current signature (for computation of look up table)
        final int[] lut3bit = new int[8];                                       // Tabelle ueber jede Kombination von 2 Nachrichten-Bits
        for( int indx=8; --indx>=0; ){
            currentSignature = indx;                                            // 3 bits maximum wegen max Index - nur zur Sicherheit/unn�tig
            for( int untouchedBits=3; --untouchedBits>=0; ){                    // Jedes der 4 Bits verrechnen
                currentSignature                                                // eigentlich: currentSignature = ...^(newBit &0x1)<<4  =>  die neuen Bits m�ssen (nachher) noch reingeXORt werden
                    = ( 0x01 == (currentSignature & 0x01) )
                    ? (currentSignature >>> 1) ^ adaptedPrimPoly
                    :  currentSignature >>> 1;
            }//for
            lut3bit[indx] = currentSignature;
        }//for
        
        
        // 4 bit LUT setup
        /* OK
        currentSignature = 0;                                                   // current signature (for computation of look up table)
        final int[] lut4bit = new int[16];                                      // Tabelle ueber jede Kombination von 4 Nachrichten-Bits
        for( int indx=lut4bit.length; --indx>=0; ){
            currentSignature = indx;                                            // 4 bits maximum wegen max Index - nur zur Sicherheit/unn�tig
            for( int untouchedBits=4; --untouchedBits>=0; ){                    // Jedes Bit (der Beiden) verrechnen
                if( 0x01 == (currentSignature & 0x01) ){
                    currentSignature ^= thePolynomial;                          // 5 bits ;  "HIER" muss das ganze Polynom verwendet werden
                }//if
                currentSignature >>>= 1;                                        // 4 bits ;  eigentlich: currentSignature = (currentSignature>>>1) | (newBit &0x1)<<4 => die neuen Bits m�ssen (nachher) noch reingeXORt werden
            }//for
            lut4bit[indx] = currentSignature;
        }//for
        */
      //currentSignature = 0;                                                   // current signature (for computation of look up table)
        final int[] lut4bit = new int[16];                                      // Tabelle ueber jede Kombination von 16 Nachrichten-Bits
        for( int indx=lut4bit.length; --indx>=0; ){
            currentSignature = indx;                                            // 4 bits maximum wegen max Index - nur zur Sicherheit/unn�tig
            for( int untouchedBits=4; --untouchedBits>=0; ){                    // Jedes der 4 Bits verrechnen
                currentSignature                                                // eigentlich: currentSignature = ...^(newBit &0x1)<<4  =>  die neuen Bits m�ssen (nachher) noch reingeXORt werden
                    = ( 0x01 == (currentSignature & 0x01) )
                    ? (currentSignature >>> 1) ^ adaptedPrimPoly
                    :  currentSignature >>> 1;
            }//for
            lut4bit[indx] = currentSignature;
        }//for
        
        
        
        
        
        //######################################################################
        //###
        //###   Der eigentliche Test
        //###   ====================
        //###
        //
        // generate some random data ( MSB on right side resp. LSB..MSB)
      //final long rndData = new Random().nextLong();
      //final long rndData = 0x1234_5678__9afe_00cdL;                            // fixed data with MSB on right side resp. LSB..MSB
        final long theData = message;
        
        // print message/data
        //
        // print what is done
        System.out.printf( "Polynomial in HEX: 0x%x  ->  Bits decoded with MSB on left side and on right side LSB", mirrorPolynomial( thePolynomial ) );
        if(( thePolynomial < 0b10000 ) || ( 0b1_1111 < thePolynomial )){
            System.out.printf( " -> ATTENTION: The polynomial is expected to have 5 bits, but it does NOT(!)" );
        }//if
        System.out.printf( "\n" );
        System.out.printf( "Data in HEX: 0x%08x  ->  Bits decoded with MSB on left side and on right side LSB\n", theData );
        System.out.printf( "\n\n" );
        //
        // print it in a "readable" form
        System.out.printf( "Data as stream of bits - original bits decoded with MSB on right side on left side LSB. " );
        System.out.printf( "Hence, data-MSB becomes LSB and Data-LSB becomes MSB:\n" );
        System.out.printf( "%s / %s\n",  toBitString(theData,64), toBitString(thePolynomial,5) );
        
        
        
        long theDataNorm = theData;
        long theDataLUT1 = theData;
        long theDataLUT2 = theData;
        long theDataLUT3 = theData;
        long theDataLUT4 = theData;
        //
        int csNorm = (int)( theDataNorm & 0x1f );                               // NO LUT at all
        theDataNorm >>>= 5;
        //
        int csLUT1 = (int)( theDataLUT1 & 0xf );                                // 1bit LUT
        theDataLUT1 >>>= 4;
        //
        int csLUT2 = (int)( theDataLUT2 & 0xf );                                // 2bit LUT
        theDataLUT2 >>>= 4;
        //
        int csLUT3 = (int)( theDataLUT3 & 0xf );                                // 3bit LUT
        theDataLUT3 >>>= 4;
        //
        int csLUT4 = (int)( theDataLUT4 & 0xf );                                // 4bit LUT
        theDataLUT4 >>>= 4;
        //
        //
        // 1.)
        // Auch die letzten Bits m�ssen verrechnet werden.
        // Wir m�ssen entsprechend der Pimpolynoml�nge(-1) Nullen anf�gen und
        // u.U. noch weitere Nullen um ein Vielfaches von der Pimpolynoml�nge(-1) zu erreichen.
        // Also f�r 5Bit Primpolynom bzw. 4Bit CRC:
        // (Bitl�nge + 7) & ~0b11
        //
        // 2.)
        // Es sollen alle das gleiche Ergebnis liefern(!)
        // 4, 3, 2, 1 teilen 12 => Bitanzahl muss ein Vielfaches von 12 sein
        // 
        //
        // F�r 64 Bit  und  4 Bit CRC:
        // 1.)  72 <- (64+7) & ~0b11    um alle Bits zu verarbeiten
        // 2.)  72 <- 72                da 72 bereits Vielfaches von 12 ist
        // => 72
        // 
        for( int bitCnt=0; bitCnt<72; bitCnt++ ){                               // 4*3=12 => n*12=...,60,72,... => 72
            
            //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
            // "normal" way respectively bit by bit - MSB on "right" side and on "left" side LSB
            // ============
            if( 0 != (csNorm & 0x01) ){                                         // <<<<
                csNorm ^= thePolynomial;                                        // <<<<
                //
                System.out.printf( "%s%s\n",  Utility.generateWhiteSpace(bitCnt), toBitString(thePolynomial,5) );
            }else{
                System.out.printf( "%s.....\n",  Utility.generateWhiteSpace(bitCnt) );
            }//if                                                               // <<<<
            System.out.printf( "%s-----\n",  Utility.generateWhiteSpace(bitCnt) );
            //
            csNorm = (csNorm >>> 1) | ((int)(theDataNorm&0x1) << 4);            // <<<<
            theDataNorm >>>= 1;                                                 // <<<<
            //
            int currentCRC = csNorm & 0xf;
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            
            
            
            //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
            // lookup table 1->4 - MSB on "right" side
            // ==============
            final int indexLUT1 = csLUT1 & mask1bit;                            // 1bit look-up, also nur 1bit betrachten
            final int valueLUT1 = lut1bit[ indexLUT1 ];                         // in der LUT "fehlt" MSB bzw. MSB ist jetzt "verarbeitet" ;  xor "alte Signatur" fehlt in LUT => ToDo
            csLUT1 = (csLUT1 >>> 1 ) ^ valueLUT1;                               // 1 da 1bit look-up, also 1 verarbeitet
            csLUT1 = csLUT1 ^ (int)( (theDataLUT1 & mask1bit) << 3 );           // das neue DatenBit fehlt noch und muss ergaenzt werden ;  3 = 4-1
            theDataLUT1 >>>= 1;                                                 // 1 da 1bit look-up, also 1 verarbeitet
            //
            // print step if requested
            if( detailedPrinting1bit ) System.out.printf( "%s~%s  <--- CRC using 1bit lookup table\n",  Utility.generateWhiteSpace(bitCnt), toBitString(csLUT1,4) );
            //
            // check result of step
            assert currentCRC == csLUT1 : String.format( "%s != %s", toBitString(currentCRC,4), toBitString(csLUT1,4) );
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            
            
            
            //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
            // lookup table 2->4 - MSB on "right" side
            // ==============
            if( 1 == (bitCnt&0x01) ){                                           // Nur wenn dran ;-)
                final int indexLUT2 = csLUT2 & mask2bit;                        // 2bit look-up, also nur 2bit betrachten
                final int valueLUT2 = lut2bit[ indexLUT2 ];                     // xor "alte Signatur" "fehlt" in LUT => muss nun gemacht werden
                csLUT2 = (csLUT2 >>> 2) ^ valueLUT2;                            // 2 da 2bit look-up, also 2 verarbeitet   ( xor "alte Signatur" )
                csLUT2 = csLUT2 ^ (int)( (theDataLUT2 & mask2bit) << 2 );       // 2 = 4-2
                theDataLUT2 >>>= 2;                                             // 2 da 2bit look-up, also 2 verarbeitet
                //
                // print step if requested
                if( detailedPrinting2bit ) System.out.printf( "%s~%s  <--- CRC using 2bit lookup table\n",  Utility.generateWhiteSpace(bitCnt), toBitString(csLUT2,4) );
                //
                // check result of step
                assert currentCRC == csLUT2 : String.format( "%s != %s", toBitString(currentCRC,4), toBitString(csLUT2,4) );
            }//if
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^            
            
            
            
            //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
            // lookup table 3->4 - MSB on "right" side
            // ==============
            if( 2 == bitCnt%3 ){                                                // Nur wenn dran ;-)
                /* Ausfuehrlich / "Langsam"
                final int indexLUT3 = csLUT3 & mask3bit;                        // 3bit look-up, also nur 3bit betrachten
                final int valueLUT3 = lut3bit[ indexLUT3 ];                     // xor "alte Signatur" "fehlt" in LUT => muss nun gemacht werden
                csLUT3 = (csLUT3 >>> 3) ^ valueLUT3;                            // 3 da 3bit look-up, also 3 verarbeitet   ( xor "alte Signatur" )
                csLUT3 = csLUT3 ^ (int)( (rndDataLUT3 & mask3bit) << 1 );       // 1 = 4-3
                rndDataLUT3 >>>= 3;                                             // 3 da 3bit look-up, also 3 verarbeitet
                */
                csLUT3 = (csLUT3 >>> 3) ^ lut3bit[csLUT3 & mask3bit] ^ (int)( (theDataLUT3 & mask3bit) << 1 );
                theDataLUT3 >>>= 3;
                //
                // print step if requested
                if( detailedPrinting3bit ) System.out.printf( "%s~%s  <--- CRC using 3bit lookup table\n",  Utility.generateWhiteSpace(bitCnt), toBitString(csLUT3,4) );
                //
                // check result of step
                assert currentCRC == csLUT3 : String.format( "%s != %s", toBitString(currentCRC,4), toBitString(csLUT3,4) );
            }//if
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^            
            
            
            
            //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
            // lookup table 4->4 - MSB on "right" side
            // ==============
            if( 3 == (bitCnt&0x03) ){                                           // Nur wenn dran ;-)
                /* Ausfuehrlich / "Langsam"
                final int indexLUT4 = csLUT4 & mask4bit;                        // 4bit look-up, also nur 4bit betrachten
                final int valueLUT4 = lut4bit[ indexLUT4 ];                     // xor "alte Signatur" "fehlt" in LUT => muss nun gemacht werden
                csLUT4 = (csLUT4 >>> 4) ^ valueLUT4;                            // 4 da 4bit look-up, also 4 verarbeitet   ( xor "alte Signatur" - unnoetig, da bereits "weg")
                csLUT4 = csLUT4 ^(int)( (rndDataLUT4 & mask4bit) << 0 );        // 0 = 4-4
                rndDataLUT4 >>>= 4;                                             // 4 da 4bit look-up, also 4 verarbeitet
                
                * kuerzer
                csLUT4 = (csLUT4 >>> 4) ^ lut4bit[csLUT4 & mask4bit] ^ (int)( (rndDataLUT4 & mask4bit) << 0 );
                rndDataLUT4 >>>= 4;
                */
                //moeglichst kurz
                csLUT4 = lut4bit[csLUT4] ^ (int)(theDataLUT4 & mask4bit);
                theDataLUT4 >>>= 4;
                //
                // print step if requested
                if( detailedPrinting4bit ) System.out.printf( "%s~%s  <--- CRC using 4bit lookup table\n",  Utility.generateWhiteSpace(bitCnt), toBitString(csLUT4,4) );
                //
                // check result of step
                assert currentCRC == csLUT4 : String.format( "%s != %s", toBitString(currentCRC,4), toBitString(csLUT4,4) );
            }//if
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            
            
            
            System.out.printf(
                "%s%s       CRC having %d bit(s) computed [after step#%d]\n",
                Utility.generateWhiteSpace(bitCnt+1),
                toBitString(csNorm,4),
                bitCnt+1,
                bitCnt+1
            );
            System.out.printf(
                "%s%s\n",
                Utility.generateWhiteSpace(bitCnt+1),
                toBitString(csNorm,5)
            );
        }//for
        
    }//method()
    
    
    
    
    
    
    
    
    
    
    //--------------------------------------------------------------------------
    //
    //  SUPPORT STUFF
    //
    
    public static int toBitInt( final String bitVectorAsString ){
        int bitVectorUnderConstruction = 0;
        for( int i=0; i<bitVectorAsString.length(); i++ ){
            bitVectorUnderConstruction <<= 1;
            if( '1' == bitVectorAsString.charAt( i )){
                bitVectorUnderConstruction |= 0b1;
            }//if
        }//for
        return bitVectorUnderConstruction;
    }//method()
    
    public static String toBitString( final long bitVector_LlrM, final int numberOfBits ){
        final StringBuffer sb = new StringBuffer();
        long tmp = bitVector_LlrM;
        for( int i=numberOfBits; --i>=0; ){
            if( (tmp & 0b1) == 0b1 ){
                sb.append( "1" );
            }else{
                sb.append( "0" );
            }//if
            tmp >>>= 1;
        }//for
        return sb.toString();
    }//method()
    
    public static int mirrorPolynomial( int polynomial ){
        int resu = 0;
        while( 0 != polynomial ){
            resu = (resu << 1) | ( polynomial & 0b1 );
            polynomial >>>= 1;
        }//while
        return resu;
    }//method()
    
}//class

package a4.demo;


import java.io.File;
import java.io.IOException;

import a4.example.Utility;


/*
 *------------------------------------------------------------------------------
 * INF WP B3 AZ4 A4 SS16
 * 
 * VCS: git@BitBucket.org:schaefers/WPPPJ-CRC.git
 *                                                Michael Schaefers  2016/05/17
 *------------------------------------------------------------------------------
 */
public class DemoVerySimpleSlowSequentialProcessorStarter {
    
    public static void main( final String... unused ){
        final long poly32bitNo1 = 0x1000000afL;
        final long poly32bitNo2 = 0x100400007L;
        final long poly32bitNo3 = 0x104c11db7L;
        final long poly32bitNo4 = 0x127673637L;
        final long poly5bit     = 0x13;
        
        
        
        final String path2TestPicEmpty = "C:\\TRIAL\\TestPics\\testPicEmpty.jpg";
        final String path2TestPic1byte = "C:\\TRIAL\\TestPics\\testPic0xCD.jpg";
        final String path2TestPic2byte = "C:\\TRIAL\\TestPics\\testPic0xE3C7.jpg";
        final String path2TestPic3byte = "C:\\TRIAL\\TestPics\\testPic0xA2C37E.jpg";
        final String path2TestPic4byte = "Z:\\git\\haw_bai5_wp_cp_ln\\BAI5_WP_CP_Praktikum\\res\\img\\wallpaper1.jpg";
        final String path2TestPic5byte = "C:\\TRIAL\\TestPics\\testPic0x0123456789.jpg";
        final String path2TestPic6byte = "C:\\TRIAL\\TestPics\\testPic0xABCDEFABCDEF.jpg";
        final String path2TestPic7byte = "C:\\TRIAL\\TestPics\\testPic0x00000000000000.jpg";
        final String path2TestPic8byte = "C:\\TRIAL\\TestPics\\testPic0xFEDCBA9876543210.jpg";
        final String path2TestPic9byte = "C:\\TRIAL\\TestPics\\testPic0x0123456789ABCDEF01.jpg";
        final String path2TestPic10byte = "C:\\TRIAL\\TestPics\\testPic0x0123456789ABCDEF0123.jpg";
        final String path2TestPic11byte = "C:\\TRIAL\\TestPics\\testPic0x0123456789ABCDEF012345.jpg";
        final String path2TestPic12byte = "C:\\TRIAL\\TestPics\\testPic0x0123456789ABCDEF01234567.jpg";
        final String path2TestPic13byte = "C:\\TRIAL\\TestPics\\testPic0x0123456789ABCDEF0123456789.jpg";
        final String path2TestPic14byte = "C:\\TRIAL\\TestPics\\testPic0x0123456789ABCDEF0123456789AB.jpg";
        final String path2TestPic15byte = "C:\\TRIAL\\TestPics\\testPic0x0123456789ABCDEF0123456789ABCD.jpg";
        final String path2TestPic16byte = "C:\\TRIAL\\TestPics\\testPic0x0123456789ABCDEF0123456789ABCDEF.jpg";
        final String path2TestPic65544byte = "C:\\TRIAL\\TestPics\\testPic65544bytes.jpg";
        //
        final String path2Pic1 = "C:\\TRIAL\\TestPics\\pic1.JPG";
        final String path2Pic2 = "C:\\TRIAL\\TestPics\\Al_Capone_cell.jpg";
        
        
        
        
        
        //######################################################################
        //###
        //###   HERE
        //###
        //
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~VVVVVVVVVVVVVVVV~~~~~~~~~~~~~~~~~~~~~~
        final File theTestFile = new File( path2TestPic4byte );
        //
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~VVVVVVVV~~~~~~~~~~~~~~~~
        final long thePolynomial_LlrM = Utility.mirror( poly32bitNo1 );
        
        
        
        
        
        try{
            final long fileLength = theTestFile.length();
            System.out.printf( "Computing signature for file: %s\n",  theTestFile.getCanonicalPath() );
            System.out.printf( "File length :         %20d [byte]\n",  fileLength );
            long numberOfBytesToBeComputed = ((fileLength+7) & ~0b11);
            numberOfBytesToBeComputed = ((numberOfBytesToBeComputed/12) + ((numberOfBytesToBeComputed%12)>0 ? 1 : 0)) *12;
            System.out.printf( "Bytes to be computed: %20d [byte]\n",  numberOfBytesToBeComputed );
        }catch( final IOException ex ){
            // TODO react wise and clever
            ex.printStackTrace();
        }//if
        System.out.printf( "File is interpreted that way, that the first bit (bit positon 0) of the first byte is the MSB. " );
        System.out.printf( "The polynomial used is: 0x%x\n",  Utility.mirror( thePolynomial_LlrM ));
        System.out.printf( "\n\n" );
        
        
        
        
        
        // setup
        final DemoVerySimpleSlowSequentialProcessor processor = new DemoVerySimpleSlowSequentialProcessor( thePolynomial_LlrM );
        //
        // do computation
        final long signatureOfFile = processor.computeCRC( theTestFile );
        
        
        
        
        
        System.out.printf( "The signature is:\n" );
        System.out.printf( "=================\n" );
        System.out.printf( "\n" );
        System.out.printf( "%39x [16 MlrL]\n", signatureOfFile );
        System.out.printf( "%39d [10 MlrL]\n", signatureOfFile );
        System.out.printf( "\n" );
        System.out.printf( "%39s [ 2 MlrL]\n",          Utility.bitFieldStringOfValue_MlrL( signatureOfFile, 32 ));
        System.out.printf( "%39s [ 2 LlrM] <<<---\n",   Utility.bitFieldStringOfValue_LlrM( signatureOfFile, Utility.numberOfBitsNeededForCoding( thePolynomial_LlrM ) -1 ));
    }//method()
    
}//class

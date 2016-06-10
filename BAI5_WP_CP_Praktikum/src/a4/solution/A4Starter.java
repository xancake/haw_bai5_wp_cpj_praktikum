package a4.solution;

import java.util.Collection;

import a4.api.Item_I;
import a4.api.SignatureProcessor_I;
import a4.example.Utility;

public class A4Starter {
	private static final String IMAGE_FOLDER = "img";
	private static final String IMAGE_FILTER = ".*\\.(([Jj][Pp][Ee]?[Gg])|([Pp][Nn][Gg]))$";
	
    private static final long polynomialA = 0x1000000afL;
    private static final long polynomialB = 0x100400007L;
    private static final long polynomialC = 0x104c11db7L;
    private static final long polynomialD = 0x127673637L;
	
	public static void main(String[] args) {
		SignatureProcessor_I processor = new ParallelSignatureProcessor(polynomialA, polynomialB, polynomialC, polynomialD);
		
        final long timeStarted = System.nanoTime();
		Collection<Item_I> items = processor.computeSignatures(IMAGE_FOLDER, IMAGE_FILTER);
        final long timeFinished = System.nanoTime();
        
        System.out.printf("After %s:\n", Utility.nanoSecondBasedTimeToString(timeFinished - timeStarted));
        System.out.printf("\n");
        System.out.printf("1000000af 100400007 104c11db7 127673637 __file_size | file_name...\n");
        System.out.printf("-vvvvvvvv--vvvvvvvv--vvvvvvvv--vvvvvvvv-------------+----------~~~\n");
        for(final Item_I elem : items) {
            System.out.printf(
                " %08x  %08x  %08x  %08x%12d : %s\n",
                elem.getSignature(0),
                elem.getSignature(1),
                elem.getSignature(2),
                elem.getSignature(3),
                elem.getFileSize(),
                elem.getFileName()
            );
        }
	}
}

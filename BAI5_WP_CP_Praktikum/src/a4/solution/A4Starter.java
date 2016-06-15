package a4.solution;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import a4.api.Item_I;
import a4.api.SignatureProcessor_I;
import a4.example.Utility;

public class A4Starter {
	private static final String SOURCE_FOLDER = "img";
	private static final String FILTER_IMAGE  = ".*\\.(([Jj][Pp][Ee]?[Gg])|([Pp][Nn][Gg]))$";
	private static final String FILTER_TEXT   = ".*\\.([Tt][Xx][Tt])$";
	
	private static final Long[] POLINOMIALS = new Long[] { 0x1000000afL, 0x100400007L, 0x104c11db7L, 0x127673637L };
	
	public static void main(String[] args) {
		SignatureProcessor_I processor = new ParallelSignatureProcessor(true, POLINOMIALS);
		
		final long timeStarted = System.nanoTime();
		Collection<Item_I> items = processor.computeSignatures(SOURCE_FOLDER, FILTER_TEXT);
		final long timeFinished = System.nanoTime();
		
		System.out.printf("\nCompleted after %s:\n", Utility.nanoSecondBasedTimeToString(timeFinished - timeStarted));
		System.out.printf("\n");
		System.out.printf(     timesN("%09x ", POLINOMIALS.length) + "__file_size | file_name...\n", (Object[])POLINOMIALS);
		System.out.printf(timesN("-vvvvvvvv-", POLINOMIALS.length) + "------------+----------~~~\n");
		
		List<Item_I> itemsList = new LinkedList<Item_I>(items);
		Collections.sort(itemsList, (item1, item2) -> item1.getFileName().compareTo(item2.getFileName()));
		for(Item_I elem : itemsList) {
			System.out.printf(
					timesN(" %08x ", POLINOMIALS.length) + "%11d : %s\n",
					elem.getSignature(0),
					elem.getSignature(1),
					elem.getSignature(2),
					elem.getSignature(3),
					elem.getFileSize(),
					elem.getFileName()
			);
		}
	}
	
	private static String timesN(String text, int n) {
		String string = "";
		for(int i=0; i<n; i++) {
			string += text;
		}
		return string;
	}
}

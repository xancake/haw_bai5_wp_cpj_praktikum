package a4.solution;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import a4.api.Item_I;
import a4.api.SignatureProcessor_I;
import a4.example.Utility;
import a4.solution.processor.ParallelSignatureProcessor;

public class A4Starter {
	private static final String SOURCE_FOLDER = "img"; 
	private static final String FILTER_ALL    = ".*$";
	private static final String FILTER_IMAGE  = ".*\\.(([Jj][Pp][Ee]?[Gg])|([Pp][Nn][Gg]))$";
	private static final String FILTER_TEXT   = ".*\\.([Tt][Xx][Tt])$";
	
	private static final Long[] POLINOMIALS = new Long[] { 0x1000000afL, 0x100400007L, 0x104c11db7L, 0x127673637L };
	
	public static void main(String[] args) {
		String sourceFolder;
		switch(args.length) {
			case 0: sourceFolder = SOURCE_FOLDER; break;
			case 1: sourceFolder = args[0]; break;
			default: printUsage(); return;
		}
		
		measure(new ParallelSignatureProcessor(true, POLINOMIALS), sourceFolder, FILTER_IMAGE, POLINOMIALS);
	}
	
	private static void printUsage() {
		System.out.println(
				"A4Starter [PFAD]\n" +
				"\n" +
				"  PFAD  - Der Pfad, unter dem von allen Dateien der CRC berechnet werden soll (optional)"
		);
	}
	
	private static void measure(SignatureProcessor_I processor, String sourceFolder, String fileFilter, Long... polinomials) {
		final long timeStarted = System.nanoTime();
		Collection<Item_I> items = processor.computeSignatures(sourceFolder, fileFilter);
		final long timeFinished = System.nanoTime();
		
		System.out.printf("\nCompleted after %s:\n", Utility.nanoSecondBasedTimeToString(timeFinished - timeStarted));
		System.out.printf("\n");
		System.out.printf(     timesN("%09x ", polinomials.length) + "__file_size | file_name...\n", (Object[])polinomials);
		System.out.printf(timesN("-vvvvvvvv-", polinomials.length) + "------------+----------~~~\n");
		
		List<Item_I> itemsList = new LinkedList<Item_I>(items);
		Collections.sort(itemsList, (item1, item2) -> item1.getFileName().compareTo(item2.getFileName()));
		for(Item_I elem : itemsList) {
			for(int i=0; i<polinomials.length; i++) {
				System.out.printf(" %08x ", elem.getSignature(i));
			}
			System.out.printf("%11d : %s\n",elem.getFileSize(),elem.getFileName());
		}
	}
	
	/**
	 * Erzeugt einen String, der den übergebenen String n-mal hintereinander konkateniert enthält.
	 * @param text Der Ausgangs-String
	 * @param n Wie oft der Ausgangs-String wiederholt werden soll
	 * @return Ein String der den übergebenen String n-mal hintereinander konkateniert enthält
	 */
	private static String timesN(String text, int n) {
		String string = "";
		for(int i=0; i<n; i++) {
			string += text;
		}
		return string;
	}
}

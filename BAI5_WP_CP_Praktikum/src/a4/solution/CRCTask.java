package a4.solution;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.zip.Checksum;
import a4.api.Item;
import a4.api.Item_I;
import a4.example.Utility;

public class CRCTask implements Callable<Item_I> {
	private File _file;
	private List<Long> _polinome;
	
	public CRCTask(File file, List<Long> polinome) {
		_file = Objects.requireNonNull(file);
		_polinome = Objects.requireNonNull(polinome);
	}
	
	@Override
	public Item_I call() throws Exception {
		List<Checksum> checksums = new LinkedList<>();
		for(Long polinom : _polinome) {
			checksums.add(new SchaefersChecksum(polinom));
		}
		
		try(final BufferedInputStream in = new BufferedInputStream(new FileInputStream(_file))) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			
			while((bytesRead = in.read(buffer)) != -1) {
				for(int i=0; i<_polinome.size(); i++) {
					checksums.get(i).update(buffer, 0, bytesRead);
				}
			}
		}
		
		// Nullen ranhängen, damit die Datei wirklich komplett verarbeitet wurde
		for(int i=0; i<_polinome.size(); i++) {
			int bytesNeeded = (Utility.numberOfBitsNeededForCoding(_polinome.get(i))-1)/8;
			long messageLength = _file.length() + bytesNeeded;
			// Extra Bytes Needed muss ein vielfaches von 12 sein, damit die Vergleichbarkeit
			// mit Lookuptables der Länge 1, 2, 3 und 4 Byte ermöglicht wird
			int extraBytesNeeded = bytesNeeded + (int)(12-messageLength%12);
			checksums.get(i).update(new byte[extraBytesNeeded], 0, extraBytesNeeded);
		}
		
		// Checksummen holen
		int[] signatures = new int[_polinome.size()];
		for(int i=0; i<_polinome.size(); i++) {
			signatures[i] = (int)checksums.get(i).getValue();
		}
		
		return new Item(_file.getName(), _file.length(), signatures);
	}
}

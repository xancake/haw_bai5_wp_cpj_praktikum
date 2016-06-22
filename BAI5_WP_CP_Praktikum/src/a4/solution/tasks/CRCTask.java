package a4.solution.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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
	private List<Checksum> _checksums;
	
	public CRCTask(File file, List<Long> polinome, List<Checksum> initialChecksums) {
		if(polinome.size() != initialChecksums.size()) {
			throw new IllegalArgumentException("Es müssen genauso viele Checksums vorgegeben werden wie Polinome.");
		}
		
		_file = Objects.requireNonNull(file);
		_polinome = Objects.requireNonNull(polinome);
		_checksums = Objects.requireNonNull(initialChecksums);
	}
	
	@Override
	public Item_I call() throws Exception {
		// Realen Inhalt der Datei verarbeiten
		try(final BufferedInputStream in = new BufferedInputStream(new FileInputStream(_file))) {
			byte[] buffer = new byte[2400]; // Sollte ein vielfaches von 12 sein (wegen Lookup-Tables)
			int bytesRead;
			
			while((bytesRead = in.read(buffer)) != -1) {
				for(int i=0; i<_polinome.size(); i++) {
					_checksums.get(i).update(buffer, 0, bytesRead);
				}
			}
		}
		
		// TODO: Die Nullen sollten direkt dem Buffer angehängt werden, wenn die Datei durch ist
		// Nullen ranhängen, damit die Datei wirklich komplett verarbeitet wurde
		for(int i=0; i<_polinome.size(); i++) {
			int bytesNeeded = (Utility.numberOfBitsNeededForCoding(_polinome.get(i))-1)/8; // durch 8, um bits in bytes umzurechnen
			long messageLength = _file.length() + bytesNeeded;
			// Extra Bytes Needed muss ein vielfaches von 12 sein, damit die Vergleichbarkeit mit
			// Lookuptables der Länge 1, 2, 3 und 4 Byte ermöglicht wird (kleinster gemeinsamer Nenner)
			int extraBytesNeeded = bytesNeeded + (int)(12-messageLength%12);
			_checksums.get(i).update(new byte[extraBytesNeeded], 0, extraBytesNeeded);
		}
		
		return new Item(_file.getName(), _file.length(), fetchSignatures(_checksums));
	}
	
	private static int[] fetchSignatures(List<Checksum> checksums) {
		int[] signatures = new int[checksums.size()];
		for(int i=0; i<checksums.size(); i++) {
			signatures[i] = (int)checksums.get(i).getValue();
		}
		return signatures;
	}
}

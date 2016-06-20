package a4.solution.tasks;

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
import a4.solution.LookupTable;
import a4.solution.LookupTableChecksum;

public class CRCLookupTask implements Callable<Item_I> {
	private File _file;
	private List<LookupTable> _lookupTables;
	
	public CRCLookupTask(File file, List<LookupTable> lookupTables) {
		_file = Objects.requireNonNull(file);
		_lookupTables = Objects.requireNonNull(lookupTables);
	}
	
	@Override
	public Item_I call() throws Exception {
		List<Checksum> checksums = new LinkedList<>();
		for(LookupTable lookupTable : _lookupTables) {
			checksums.add(new LookupTableChecksum(lookupTable));
		}
		
		// Realen Inhalt der Datei verarbeiten
		try(final BufferedInputStream in = new BufferedInputStream(new FileInputStream(_file))) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			
			while((bytesRead = in.read(buffer)) != -1) {
				for(int i=0; i<_lookupTables.size(); i++) {
					checksums.get(i).update(buffer, 0, bytesRead);
				}
			}
		}
		
		// Nullen ranhängen, damit die Datei wirklich komplett verarbeitet wurde
		for(int i=0; i<_lookupTables.size(); i++) {
			LookupTable lookupTable = _lookupTables.get(i);
			
			int bytesNeeded = (Utility.numberOfBitsNeededForCoding(lookupTable.getPolinom())-1)/8; // durch 8, um bits in bytes umzurechnen
			long messageLength = _file.length() + bytesNeeded;
			// Extra Bytes Needed muss ein vielfaches von 12 sein, damit die Vergleichbarkeit mit
			// Lookuptables der Länge 1, 2, 3 und 4 Byte ermöglicht wird (kleinster gemeinsamer Nenner)
			int extraBytesNeeded = bytesNeeded + (int)(12-messageLength%12);
			
			checksums.get(i).update(new byte[extraBytesNeeded], 0, extraBytesNeeded);
		}
		
		// Checksummen holen
		int[] signatures = new int[checksums.size()];
		for(int i=0; i<checksums.size(); i++) {
			signatures[i] = (int)checksums.get(i).getValue();
		}
		
		return new Item(_file.getName(), _file.length(), signatures);
	}
}

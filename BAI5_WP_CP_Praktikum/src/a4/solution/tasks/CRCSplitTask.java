package a4.solution.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.Checksum;

import a4.api.Item;
import a4.api.Item_I;
import a4.example.Utility;
import a4.solution.SchaefersChecksum;

public class CRCSplitTask implements Callable<Item_I> {
	private File _file;
	private List<Long> _polinome;
	
	public CRCSplitTask(File file, List<Long> polinome) {
		_file = Objects.requireNonNull(file);
		_polinome = Objects.requireNonNull(polinome);
	}
	
	@Override
	public Item_I call() throws Exception {
		List<Holder> holders = new LinkedList<>();
		for(Long polinom : _polinome) {
			holders.add(new Holder(polinom, new SchaefersChecksum(polinom), Executors.newSingleThreadExecutor()));
		}
		
		// Realen Inhalt der Datei verarbeiten
		try(final BufferedInputStream in = new BufferedInputStream(new FileInputStream(_file))) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			
			while((bytesRead = in.read(buffer)) != -1) {
				byte[] bufferCopy = Arrays.copyOf(buffer, buffer.length);
				for(Holder holder : holders) {
					holder.submit(bufferCopy, 0, bytesRead);
				}
			}
		}
		
		// Nullen ranhängen, damit die Datei wirklich komplett verarbeitet wurde
		List<Future<Long>> ergebnisse = new ArrayList<>();
		for(Holder holder : holders) {
			int bytesNeeded = (Utility.numberOfBitsNeededForCoding(holder._polinom)-1)/8;
			long messageLength = _file.length() + bytesNeeded;
			// Extra Bytes Needed muss ein vielfaches von 12 sein, damit die Vergleichbarkeit
			// mit Lookuptables der Länge 1, 2, 3 und 4 Byte ermöglicht wird
			int extraBytesNeeded = bytesNeeded + (int)(12-messageLength%12);
			ergebnisse.add(holder.submit(new byte[extraBytesNeeded], 0, extraBytesNeeded));
		}
		
		// Checksummen holen
		int[] signatures = new int[ergebnisse.size()];
		for(int i=0; i<ergebnisse.size(); i++) {
			signatures[i] = ergebnisse.get(i).get().intValue();
		}
		
		return new Item(_file.getName(), _file.length(), signatures);
	}
	
	private static class Holder {
		private final Long _polinom;
		private final Checksum _checksum;
		private final ExecutorService _executor;
		
		private Holder(long polinom, Checksum checksum, ExecutorService executor) {
			_polinom = polinom;
			_checksum = checksum;
			_executor = executor;
		}
		
		public Future<Long> submit(byte[] buffer, int offset, int length) {
			return _executor.submit(new Callable<Long>() {
				@Override
				public Long call() throws Exception {
					_checksum.update(buffer, offset, length);
					return _checksum.getValue();
				}
			});
		}
	}
}

package a4.solution.processor;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.Checksum;

import a4.api.Item_I;
import a4.api.SignatureProcessor_I;
import a4.solution.LookupTable;
import a4.solution.LookupTableChecksum;
import a4.solution.tasks.CRCTask;

public class LookupTableSignatureProcessor implements SignatureProcessor_I {
	private boolean _recursive;
	private int _lookupTableSizeByte;
	private List<Long> _polinome;

	public LookupTableSignatureProcessor(boolean recursive, int lookupTableSizeByte, Long... polinome) {
		this(recursive, lookupTableSizeByte, Arrays.asList(polinome));
	}

	public LookupTableSignatureProcessor(boolean recursive, int lookupTableSizeByte, List<Long> polinome) {
		_polinome = Objects.requireNonNull(polinome);
		_lookupTableSizeByte = lookupTableSizeByte;
		_recursive = recursive;
	}
	
	@Override
	public Collection<Item_I> computeSignatures(String pathToRelatedFiles, String filter) {
		try {
			File path = new File(pathToRelatedFiles);
			if(!path.isAbsolute()) {
				// Versuch die Pfadangabe relativ vom Klassenpfad aufzulösen, falls es sich um eine relative Pfadangabe handelt
				path = new File(ClassLoader.getSystemResource(pathToRelatedFiles).toURI());
			}
			
			if (!path.exists()) {
				throw new IllegalArgumentException(String.format("INVALID path: %s\n", pathToRelatedFiles));
			}
			
			// Lookup-Table(s) berechnen
			List<LookupTable> lookupTables = generateLookupTables(_polinome);
			
			// Aufsetzen des Thread-Pools
			int availableProcessors = Runtime.getRuntime().availableProcessors();
			ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);
			System.out.println("Threadpool mit maximal " + availableProcessors + " Threads gestartet");
			
			// Starten der Verarbeitung und Aufgeben der Tasks
			Collection<Future<Item_I>> futures = verarbeite(path, executorService, lookupTables, (file) -> (_recursive && file.isDirectory()) || file.getName().matches(filter));
			
			// Beenden des Thread-Pools
			executorService.shutdown();
			while(!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
				System.out.println("Thread-Pool noch nicht beendet, wir warten weiter...");
			}
			
			// Sammeln der Ergebnisse
			Collection<Item_I> items = new LinkedList<>();
			for(Future<Item_I> future : futures) {
				items.add(future.get());
			}
			return items;
		} catch (InterruptedException e) {
			// TODO: überlegen, was sinnvoll wäre
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			// TODO: überlegen, was sinnvoll wäre
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			// Sollte nicht auftreten können, da der ClassLoader bereits 'null' zurückgibt,
			// falls es keine Datei zu dem Pfad gibt (was auch auf ungültige Pfadangaben zutreffen sollte).
			// Exception aber trotzdem werfen, damit nichts verschluckt wird 
			throw new RuntimeException(e);
		}
	}
	
	private List<LookupTable> generateLookupTables(List<Long> polinome) {
		try {
			// Aufsetzen des Thread-Pools
			int availableProcessors = Runtime.getRuntime().availableProcessors();
			ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);
			System.out.println("Threadpool mit maximal " + availableProcessors + " Threads gestartet");
			
			// Starten der Verarbeitung und Aufgeben der Tasks
			Collection<Future<LookupTable>> futures = new LinkedList<>();
			for(Long polinom : polinome) {
				futures.add(executorService.submit(() -> new LookupTable(polinom, _lookupTableSizeByte)));
			}
			
			// Beenden des Thread-Pools
			executorService.shutdown();
			while(!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
				System.out.println("Thread-Pool noch nicht beendet, wir warten weiter...");
			}
			
			List<LookupTable> lookupTables = new ArrayList<>();
			for(Future<LookupTable> future : futures) {
				lookupTables.add(future.get());
			}
			return lookupTables;
		} catch (InterruptedException e) {
			// TODO: überlegen, was sinnvoll wäre
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			// TODO: überlegen, was sinnvoll wäre
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Verarbeitet den übergebenen Pfad. Handelt es sich bei dem Pfad um eine Datei wird dafür ein CRC-Berechnungstask
	 * an dem übergebenen {@link ExecutorService} aufgegeben. Handelt es sich um ein Verzeichnis wird diese Methode
	 * rekursiv für alle Pfade unter diesem Pfad aufgerufen, die dem übergebenen {@link FileFilter} entsprechen.
	 * @param path Der zu verarbeitende Pfad
	 * @param executorService Der {@link ExecutorService}
	 * @param filter Der {@link FileFilter}
	 * @return Eine Collection aller {@link Future}-Objekte der Tasks, die dem {@link ExecutorService} aufgegeben wurden
	 */
	private Collection<Future<Item_I>> verarbeite(File path, ExecutorService executorService, List<LookupTable> lookupTables, FileFilter filter) {
		Collection<Future<Item_I>> futures = new LinkedList<>();
		if(path.isFile()) {
			List<Checksum> checksums = new LinkedList<Checksum>();
			for(int i=0; i<lookupTables.size(); i++) {
				checksums.add(new LookupTableChecksum(lookupTables.get(i)));
			}
			futures.add(executorService.submit(new CRCTask(path, _polinome, checksums)));
		} else {
			File[] filesOfDirectory = path.listFiles(filter);
			for(File file : filesOfDirectory) {
				futures.addAll(verarbeite(file, executorService, lookupTables, filter));
			}
		}
		return futures;
	}
}

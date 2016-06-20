package a4.solution.processor;

import java.io.File;
import java.io.FileFilter;
import java.lang.invoke.MethodHandles.Lookup;
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

import a4.api.Item_I;
import a4.api.SignatureProcessor_I;
import a4.solution.LookupTable;
import a4.solution.tasks.CRCLookupTask;
import a4.solution.tasks.CRCTask;

public class LookupTableSignatureProcessor implements SignatureProcessor_I {
	private boolean _recursive;
	private List<Long> _polinome;

	public LookupTableSignatureProcessor(boolean recursive, Long... polinome) {
		this(recursive, Arrays.asList(polinome));
	}

	public LookupTableSignatureProcessor(boolean recursive, List<Long> polinome) {
		_polinome = Objects.requireNonNull(polinome);
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
			List<LookupTable> lookupTables = new ArrayList<>();
			for(Long polinom : _polinome) {
				lookupTables.add(new LookupTable(polinom, 8));
			}
			
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
			futures.add(executorService.submit(new CRCLookupTask(path, lookupTables)));
		} else {
			File[] filesOfDirectory = path.listFiles(filter);
			for(File file : filesOfDirectory) {
				futures.addAll(verarbeite(file, executorService, lookupTables, filter));
			}
		}
		return futures;
	}
}

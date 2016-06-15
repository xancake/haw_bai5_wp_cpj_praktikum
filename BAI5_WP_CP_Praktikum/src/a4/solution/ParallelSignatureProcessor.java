package a4.solution;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
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

public class ParallelSignatureProcessor implements SignatureProcessor_I {
	private boolean _recursive;
	private List<Long> _polinome;

	public ParallelSignatureProcessor(boolean recursive, Long... polinome) {
		this(recursive, Arrays.asList(polinome));
	}

	public ParallelSignatureProcessor(boolean recursive, List<Long> polinome) {
		_polinome = Objects.requireNonNull(polinome);
		_recursive = recursive;
	}

	@Override
	public Collection<Item_I> computeSignatures(String pathToRelatedFiles, String filter) {
		try {
			File directory = new File(pathToRelatedFiles);
			if(!directory.isAbsolute()) {
				directory = new File(ClassLoader.getSystemResource(pathToRelatedFiles).toURI());
			}
			
			if (!directory.exists()) {
				throw new IllegalArgumentException(String.format("INVALID path: %s\n", pathToRelatedFiles));
			}
			
			int availableProcessors = Runtime.getRuntime().availableProcessors();
			ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);
			System.out.println("Threadpool mit maximal " + availableProcessors + " Threads gestartet");
			
			Collection<Future<Item_I>> futures = verarbeite(directory, executorService, (file) -> (_recursive && file.isDirectory()) || file.getName().matches(filter));
			
			executorService.shutdown();
			while(!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
				System.out.println("Thread-Pool noch nicht beendet, wir warten weiter...");
			}
			
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
			// e.printStackTrace();
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			// TODO: überlegen, was sinnvoll wäre
			// e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private Collection<Future<Item_I>> verarbeite(File path, ExecutorService executorService, FileFilter filter) {
		Collection<Future<Item_I>> futures = new LinkedList<>();
		if(path.isFile()) {
			futures.add(executorService.submit(new CRCTask(path, _polinome)));
		} else {
			File[] filesOfDirectory = path.listFiles(filter);
			for(File file : filesOfDirectory) {
				futures.addAll(verarbeite(file, executorService, filter));
			}
		}
		return futures;
	}
}

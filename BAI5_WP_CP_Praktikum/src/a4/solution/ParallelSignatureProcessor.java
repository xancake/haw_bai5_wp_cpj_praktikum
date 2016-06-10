package a4.solution;

import java.io.File;
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
	private List<Long> _polinome;

	public ParallelSignatureProcessor(Long... polinome) {
		this(Arrays.asList(polinome));
	}

	public ParallelSignatureProcessor(List<Long> polinome) {
		_polinome = Objects.requireNonNull(polinome);
	}

	@Override
	public Collection<Item_I> computeSignatures(String pathToRelatedFiles, String filter) {
		try {
			final File directory = new File(ClassLoader.getSystemResource(pathToRelatedFiles).toURI());
			if (!directory.exists() || !directory.isDirectory()) {
				throw new IllegalArgumentException(String.format("INVALID path: %s\n", pathToRelatedFiles));
			}
	
			int availableProcessors = Runtime.getRuntime().availableProcessors();
			System.out.println("Available Processors: " + availableProcessors);
			ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);
	
			File[] filesOfDirectory = directory.listFiles((dir, name) -> name.matches(filter));
			List<Future<Item_I>> futures = new LinkedList<>();
			for(File file : filesOfDirectory) {
				Future<Item_I> future = executorService.submit(new SignatureTask(file, _polinome));
				futures.add(future);
			}
			
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
}

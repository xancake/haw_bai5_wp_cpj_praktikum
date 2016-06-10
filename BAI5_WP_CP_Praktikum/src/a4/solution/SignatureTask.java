package a4.solution;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import a4.api.Item;
import a4.api.Item_I;

public class SignatureTask implements Callable<Item_I> {
	private File _file;
	private List<Long> _polinome;
	
	public SignatureTask(File file, List<Long> polinome) {
		_file = Objects.requireNonNull(file);
		_polinome = Objects.requireNonNull(polinome);
	}
	
	@Override
	public Item_I call() throws Exception {
		//TODO: berechne 
		
		
		
		return new Item(_file.getName(), _file.length(), new int[_polinome.size()]);
	}
}

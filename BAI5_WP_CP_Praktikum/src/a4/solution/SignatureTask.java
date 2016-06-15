package a4.solution;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import a4.api.Item;
import a4.api.Item_I;
import a4.example.Utility;

public class SignatureTask implements Callable<Item_I> {
	private File _file;
	private List<Long> _polinome;
	
	public SignatureTask(File file, List<Long> polinome) {
		_file = Objects.requireNonNull(file);
		_polinome = Objects.requireNonNull(polinome);
	}
	
	@Override
	public Item_I call() throws Exception {
		final int[] signatures = new int[_polinome.size()];
		final long[] temp = new long[_polinome.size()];
		
		int extraBytes = (Utility.numberOfBitsNeededForCoding(_polinome.get(0))-1)/8;
		try(final Message message = new Message(new FileInputStream(_file), extraBytes)) {
			while(message.hasNextBit()) {
				long bit = message.getNextBit();
				long bitNachVorn = (bit << 32);
				
				for(int i=0; i<_polinome.size(); i++) {
					if(0b1 == (temp[i] & 0b1)) {
						temp[i] ^= _polinome.get(i);
					}
					temp[i] = (temp[i] >>> 1) | bitNachVorn;
				}
			}
		}
		
		for(int i=0; i<_polinome.size(); i++) {
			signatures[i] = (int)temp[i];
		}
		
		return new Item(_file.getAbsolutePath(), _file.length(), signatures);
	}
}

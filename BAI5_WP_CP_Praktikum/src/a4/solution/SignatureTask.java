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
		
		int polinomLaenge = Utility.numberOfBitsNeededForCoding(_polinome.get(0));
		try(final Message message = new Message(new FileInputStream(_file), polinomLaenge-1)) {
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
		
		return new Item(_file.getName(), _file.length(), signatures);

//		final long fileSize = _file.length();
//		long bytesToCompute = (7 + fileSize) & ~0b11L;
//		bytesToCompute = (bytesToCompute/12 + (bytesToCompute%12 > 0 ? 1 : 0 )) * 12;
//		
//		final int[] signatures = new int[_polinome.size()];
//		
//		try(final BufferedInputStream in = new BufferedInputStream(new FileInputStream(_file))) {
//			byte[] buffer = new byte[1];
//			int bytesRead;
//			while((bytesRead = in.read(buffer)) != -1) {
//				for(int i=0; i<_polinome.size(); i++) {
//					signatures[i] = computeCheckSum(signatures[i], _polinome.get(i), buffer, 0, bytesRead);
//				}
//			}
//			
//			for(int i=0; i<_polinome.size(); i++) {
//				long polinom = _polinome.get(i);
//				int bytesNeeded = Utility.numberOfBitsNeededForCoding(polinom);
//				signatures[i] = computeCheckSum(signatures[i], polinom, new byte[bytesNeeded], 0, bytesNeeded);
//			}
//		}
//		
//		return new Item(_file.getName(), fileSize, signatures);
	}
	
	private int computeCheckSum(int checksum, long polinom, byte[] buffer, int start, int bytes) {
        for(int bufferIndex=start; bufferIndex<bytes; bufferIndex++) {
            
            // put next byte at end (according to our "signature thinking" the least significant position)
            checksum |= ((0xffL & buffer[bufferIndex]) << 32);
            
            // handle current byte
            for(int bitPosition=0;  bitPosition<8;  bitPosition++) {
                if(0b1 == (0b1 & checksum)) {
                    // MSB of checksum is set
                	checksum ^= polinom;
                }
                checksum = checksum >>> 1;
            }
        }
		return checksum;
	}
}

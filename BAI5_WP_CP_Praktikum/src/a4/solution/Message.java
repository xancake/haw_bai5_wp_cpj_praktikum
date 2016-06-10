package a4.solution;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Message implements AutoCloseable {
	private InputStream _input;
	private int _extraZeros;
	private byte _current;
	private int _locationInCurrent;
	private boolean _endReached;
	
	public Message(InputStream input, int extraZeros) {
		_input = new BufferedInputStream(Objects.requireNonNull(input));
		_extraZeros = extraZeros;
		_locationInCurrent = -1;
	}
	
	public boolean hasNextBit() {
		return _extraZeros > 0;
	}
	
	public int getNextBit() throws IOException {
		if(_endReached) {
			return getExtraZero();
		} else if(_locationInCurrent == -1) {
			byte[] b = new byte[1];
			int returnCode = _input.read(b);
			
			if(returnCode == -1) {
				_endReached = true;
				return getExtraZero();
			}
			
			_current = b[0];
			_locationInCurrent = 7;
		}
		return (_current >>> _locationInCurrent--) & 0b1;
	}
	
	private int getExtraZero() {
		if(_extraZeros > 0) {
			_extraZeros--;
			return 0;
		} else {
			throw new IllegalStateException("Die Nachricht ist zuende!");
		}
	}

	@Override
	public void close() throws Exception {
		_input.close();
	}
	
	public static void main(String... args) throws Exception {
		byte[] baseMessage = new byte[] {(byte)0x41, (byte)0xFF, (byte)0xAF};
		try(Message message = new Message(new ByteArrayInputStream(baseMessage), 4)) {
			int bits = 0;
			while(message.hasNextBit()) {
				System.out.print(message.getNextBit());
				bits++;
				if(bits%8 == 0)
					System.out.print(" ");
			}
		}
	}
}

package a4.solution;

import java.util.zip.Checksum;

import a4.example.Utility;

public class SchaefersChecksum implements Checksum {
	private long _polinom;
	private long _crc;
	
	public SchaefersChecksum(long polinom) {
		_polinom = Utility.mirror(polinom);
	}
	
	@Override
	public long getValue() {
		return _crc & 0xffffffff;
	}
	
	@Override
	public void reset() {
		_crc = 0;
	}
	
	@Override
	public void update(int b) {
		// put next byte at end (according to our "signature thinking" the least significant position)
		_crc |= ((0xffL & b) << 32);
		
		// handle current byte
		for(int bitPosition=0; bitPosition<8; bitPosition++) {
			if(0b1 == (0b1 & _crc)) {
				_crc ^= _polinom;
			}
			_crc >>>= 1;
		}
//		System.out.printf("%8x%n", _crc);
	}
	
	@Override
	public void update(byte[] buffer, int offset, int length) {
		for(int bufferIndex=offset; bufferIndex<length; bufferIndex++) {
			update(buffer[bufferIndex]);
		}
	}
}

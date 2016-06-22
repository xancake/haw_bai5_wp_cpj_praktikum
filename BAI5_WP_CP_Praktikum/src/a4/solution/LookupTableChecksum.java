package a4.solution;

import java.util.Objects;
import java.util.zip.Checksum;

public class LookupTableChecksum implements Checksum {
	private LookupTable _lookupTable;
	private int _crc;
	
	public LookupTableChecksum(LookupTable lookupTable) {
		_lookupTable = Objects.requireNonNull(lookupTable);
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
		int valueLUT = _lookupTable.lookup((int)_crc);
		_crc = (_crc >>> _lookupTable.getSize()) ^ valueLUT ^ (b << (32-_lookupTable.getSize()));
	}
	
	@Override
	public void update(byte[] b, int off, int len) {
		int bytes = 0;
		for(int bufferIndex=off; bufferIndex<len; bufferIndex++) {
			bytes = bytes | b[bufferIndex] << 8*(bufferIndex%(_lookupTable.getSize()/8));
			if((bufferIndex+1) % (_lookupTable.getSize()/8) == 0) {
				update(bytes);
				bytes = 0;
			}
			// TODO: auch verarbeiten, wenn die letzten bytes des Buffers nicht mehr _lookupTable.getSize() Byte erreichen
		}
	}
}

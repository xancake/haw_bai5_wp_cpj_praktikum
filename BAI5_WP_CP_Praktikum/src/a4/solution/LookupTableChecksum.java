package a4.solution;

import java.util.Objects;
import java.util.zip.Checksum;

public class LookupTableChecksum implements Checksum {
	private LookupTable _lookupTable;
	private long _crc;
	
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
		_crc = (_crc >>> _lookupTable.getSize()) ^ valueLUT ^ (b << _lookupTable.getSize());
		System.out.printf("%8x | %8x%n", valueLUT, _crc);
	}
	
	@Override
	public void update(byte[] b, int off, int len) {
		for(int bufferIndex=off; bufferIndex<len; bufferIndex++) {
			update(b[bufferIndex]);
		}
	}
}

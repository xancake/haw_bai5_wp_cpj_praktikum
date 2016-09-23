package a4.solution;

import a4.example.Utility;

public class Polinom {
	private long _polinom;
	
	public Polinom(long polinom) {
		_polinom = polinom;
	}
	
	public int getLength() {
		return Utility.numberOfBitsNeededForCoding(_polinom);
	}
	
	public Polinom mirror() {
		return new Polinom(Utility.mirror(_polinom));
	}
	
	public long toLong() {
		return _polinom;
	}
	
	@Override
	public String toString() {
		return "0x" + Long.toHexString(_polinom);
	}
}

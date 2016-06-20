package a4.solution;

public class LookupTable {
	private long _polinom;
	private int _size;
	private int _bitMask;
	private int[] _lookup;
	
	public LookupTable(long polinom, int size) {
		if(size > 32) {
			throw new IllegalArgumentException("Diese Lookup-Tabelle unterstützt keine Einträge größer als 32 bit!");
		}
		
		_polinom = polinom;
		_size = size;
		
		for(int i=size; i>0; i--) {
			_bitMask = (_bitMask << 1) + 1;
		}
		
		_lookup = new int[(int)(Math.pow(2, size))];
		for(int i=0; i<_lookup.length; i++) {
			long signature = i;
			// Jedes Bit der Signatur muss verarbeitet werden, das ist abhängig von der größe der Lookup-Tabelle
			for(int stillToDo=_size; stillToDo>=0; stillToDo--) {
				// da mit dem Original-Polynom gearbeitet wird, muss erst verknüpft und dann geSHIFTet werden - quasi die "Original-Idee"
				if(0b1 == (signature & 0b1)) {
					signature ^= _polinom;
				}
				signature >>>= 1;
			}
			
			_lookup[i] = (int)signature;
		}
	}
	
	/**
	 * Gibt das Polinom zurück, für das die Lookup-Tabelle aufgebaut wurde.
	 * @return Das Polinom, zu dem die Lookup-Tabelle aufgebaut wurde
	 */
	public long getPolinom() {
		return _polinom;
	}
	
	/**
	 * Gibt die Größe der Einträge in der Lookup-Tabelle in Bit wieder.
	 * @return Die Größe der Einträge in Bit
	 */
	public int getSize() {
		return _size;
	}
	
	public int lookup(int value) {
		int lookupIndex = value & _bitMask;
		return _lookup[lookupIndex];
	}
}

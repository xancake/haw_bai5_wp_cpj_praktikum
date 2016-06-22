package a4.solution;

import a4.example.Utility;

public class LookupTable {
	private long _polinom;
	private int _size;
	private int _bitMask;
	private int[] _lookup;
	
	/**
	 * Erzeugt eine neue Lookup-Tabelle für das übergebene Polinom. Die Einträge in der Lookup-Tabelle
	 * haben eine Größe von {@code size} Bit.
	 * @param polinom Das Polinom
	 * @param size Die Größe der Einträge in der Lookup-Tabelle in Byte,
	 *        darf nicht größer als 4 sein da intern mit Integern für die Lookup-Tabelle gearbeitet wird
	 *        und die Lookup-Tabelle bei einer Größe von 4 bereits mehrere Gigabyte Speicher benötigt ({@code 2^(size*8)} Bits)
	 * @throws IllegalArgumentException Wenn {@code size <= 0} oder {@code size > 4}
	 */
	public LookupTable(long polinom, int size) {
		if(size <= 0) {
			throw new IllegalArgumentException("Die Größe der Einträge in der Lookup-Tabelle muss größer sein als 0!");
		}
		if(size > 4) {
			throw new IllegalArgumentException("Diese Lookup-Tabelle unterstützt keine Einträge größer als 4 Byte (32 bit)!");
		}
		
		_polinom = Utility.mirror(polinom);
		_size = size*8;
		_bitMask = createBitMask(_size);
		
		_lookup = new int[(int)(Math.pow(2, _size))];
		for(int i=_lookup.length-1; i>=0; i--) {
			long signature = i;
			// Jedes Bit der Signatur muss verarbeitet werden, das ist abhängig von der größe der Lookup-Tabelle
			for(int stillToDo=_size; stillToDo>0; stillToDo--) {
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
	
	/**
	 * Ermittelt den Lookup-Table-Wert für den übergebenen Wert.
	 * @param value Der Wert dessen Lookup-Table-Wert ermittelt werden soll
	 * @return Der Lookup-Table-Wert zu dem übergebenen Wert
	 */
	public int lookup(int value) {
		int lookupIndex = value & _bitMask;
		return _lookup[lookupIndex];
	}
	
	/**
	 * Berechnet eine Bitmaske für die übergebene Anzahl an Bits.
	 * @param bits Die Anzahl der Bits der zu berechnenden Bitmaske
	 * @return Eine bitmaske mit {@code bits} Einsen beginnend an dem least-significant-bit
	 */
	private static int createBitMask(int bits) {
		int bitmask = 0;
		for(int i=bits; i>0; i--) {
			bitmask = (bitmask << 1) + 1;
		}
		return bitmask;
	}
}

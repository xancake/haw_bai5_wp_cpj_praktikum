package a1.p3;

import java.util.Objects;

/**
 * Ein Kunde. Kunden gehen zum {@link FriseurSalon} um sich die Haare schneiden zu lassen.
 */
public class Kunde extends Thread {
	private FriseurSalon _salon;
	
	/**
	 * Instanziiert einen Kunden.
	 * @param salon Der Friseursalon, zu dem der Kunde gehen möchte
	 */
	public Kunde(FriseurSalon salon) {
		_salon = Objects.requireNonNull(salon);
		setName("Kunde " + getId());
	}
	
	@Override
	public void run() {
		zumFriseurGehen();
	}
	
	/**
	 * Kunde geht zum Friseursalon.
	 */
	private void zumFriseurGehen() {
		_salon.betreten(this);
	}
}

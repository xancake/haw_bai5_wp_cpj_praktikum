package a1.p3;

import java.util.Objects;

/**
 * Ein Kunde. Kunden gehen zum {@link FriseurSalon} um sich die Haare schneiden zu lassen.
 */
public class Kunde extends Thread {
	private FriseurSalon _salon;
	private int _haarLaenge;
	private int _wunschHaarLaenge;
	
	/**
	 * Instanziiert einen Kunden.
	 * @param salon Der Friseursalon, zu dem der Kunde gehen möchte
	 * @param haarLaenge Die aktuelle Haarlänge des Kunden
	 * @param wunschHaarLaenge Die Wunschhaarlänge des Kunden
	 */
	public Kunde(FriseurSalon salon, int haarLaenge, int wunschHaarLaenge) {
		if(wunschHaarLaenge > haarLaenge) {
			throw new IllegalArgumentException("Die Wunschhaarlänge kann nicht größer sein, als die tatsächliche Haarlänge.");
		}
		_salon = Objects.requireNonNull(salon);
		setName("Kunde " + getId());
		_haarLaenge = haarLaenge;
		_wunschHaarLaenge = wunschHaarLaenge;
	}
	
	@Override
	public void run() {
		System.out.println("[" + getName() + "] betritt den Salon!");
		try {
			if(_salon.getWarteraum().anstellen(this)) {
				System.out.println("[" + getName() + "] stellt sich der Warteschlange an!");
				_salon.getFriseur().aufwecken();
				try {
					synchronized(this) {
						while(!isZufrieden()) {
							wait();
						}
					}
					System.out.println("[" + getName() + "] hat die Haare geschnitten bekommen und ist jetzt zufrieden!");
				} catch (InterruptedException e) {
					System.out.println("[" + getName() + "] wird jetzt rausgeschmissen!");
				}
			} else {
				System.out.println("[" + getName() + "] konnte sich nicht der Warteschlange anstellen!");
			}
		} catch(InterruptedException e) {}
		System.out.println("[" + getName() + "] verlässt den Salon wieder!");
	}
	
	/**
	 * Schneidet die Haare um die übergebene Länge ab.
	 * Benachrichtigt den Kunden ({@link #notify()})
	 * @param amount Die Länge um die die Haare geschnitten werden
	 */
	public synchronized void haareSchneiden(int amount) {
		_haarLaenge = _haarLaenge - amount;
		notify();
	}
	
	/**
	 * Gibt zurück, ob der Kunde mit seiner aktuellen Haarlänge zufrieden ist.
	 * Das heißt die Haarlänge ist {@code <=} seiner Wunschhaarlänge.
	 * @return {@code true} wenn der Kunde mit seiner aktuellen Haarlänge zufrieden ist
	 */
	public synchronized boolean isZufrieden() {
		return _haarLaenge <= _wunschHaarLaenge;
	}
}

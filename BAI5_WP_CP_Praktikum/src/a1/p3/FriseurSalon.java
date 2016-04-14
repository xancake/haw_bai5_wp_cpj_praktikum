package a1.p3;

import java.util.concurrent.TimeUnit;

/**
 * Ein Friseursalon. In dem Friseursalon arbeitet ein {@link Friseur}.
 * {@link Kunde}n können den Friseursalon betreten um sich die Haare schneiden zu lassen.
 * Da es nur einen Friseur gibt, könnte es sein, dass ein Kunde entsprechend in
 * eine Warteschlange kommt.
 */
public class FriseurSalon {
	private Warteraum _warteraum;
	private Friseur _friseur;
	private volatile boolean _istGeoeffnet;
	
	/**
	 * Instanziiert einen neuen Salon. Dabei wird auch ein Friseur erzeugt.
	 * @param wartePlaetze Die Anzahl der Warteplätze, die der Salon hat
	 * @param timeUnit Die Zeiteinheit in der die Dauer gemessen wird
	 * @param haareSchneidenDauer Die Dauer in der angegebenen Zeiteinheit, die für das Haareschneiden benötigt wird
	 */
	public FriseurSalon(int wartePlaetze, TimeUnit timeUnit, int haareSchneidenDauer) {
		_warteraum = new Warteraum(wartePlaetze);
		_friseur = new Friseur(_warteraum, timeUnit, haareSchneidenDauer);
	}
	
	/**
	 * Gibt den Friseur des Salons zurück. Blockiert, bis der Salon geöffnet wurde.
	 * @return Der Friseur des Salons
	 * @throws InterruptedException Wenn der aufrufende Thread beim Warten interrupted wurde
	 * @see #istGeoffnet()
	 * @see #oeffne()
	 */
	public synchronized Friseur getFriseur() throws InterruptedException {
		while(!_istGeoeffnet) {
			wait();
		}
		return _friseur;
	}
	
	/**
	 * Gibt den Warteraum des Salons zurück. Blockiert, bis der Laden geöffnet wurde.
	 * @return Der Warteraum des Salons
	 * @throws InterruptedException Wenn der aufrufende Thread beim Warten interrupted wurde
	 * @see #istGeoffnet()
	 * @see #oeffne()
	 */
	public synchronized Warteraum getWarteraum() throws InterruptedException {
		while(!_istGeoeffnet) {
			wait();
		}
		return _warteraum;
	}
	
	/**
	 * Gibt zurück, ob der Salon geöffnet ist oder nicht.
	 * @return {@code true} wenn der Salon geöffnet ist, ansonsten {@code false}
	 */
	public synchronized boolean istGeoffnet() {
		return _istGeoeffnet;
	}
	
	/**
	 * Öffnet den Friseursalon.
	 */
	public synchronized void oeffne() {
		System.out.println("[SALON] wird geöffnet!");
		_friseur.start();
		_istGeoeffnet = true;
		notify();
		System.out.println("[SALON] ist nun geöffnet!");
	}
	
	/**
	 * Schließt den Friseursalon.
	 */
	public synchronized void schliesse() {
		System.out.println("[SALON] wird nun geschlossen!");
		_istGeoeffnet = false;
		_friseur.interrupt();
		try {
			_friseur.join();
		} catch (InterruptedException e) {}
		System.out.println("[SALON] ist nun geschlossen!");
	}
}

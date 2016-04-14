package a1.p3;

import java.util.Objects;

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
	 * Instanziiert einen neuen Salon.
	 * @param wartePlaetze Die Anzahl der Warteplätze, die der Salon hat
	 */
	public FriseurSalon(int wartePlaetze) {
		_warteraum = new Warteraum(wartePlaetze);
	}
	
	public synchronized Friseur getFriseur() throws InterruptedException {
		while(!_istGeoeffnet) {
			wait();
		}
		return _friseur;
	}
	
	public synchronized Warteraum getWarteraum() throws InterruptedException {
		while(!_istGeoeffnet) {
			wait();
		}
		return _warteraum;
	}
	
	/**
	 * Öffnet den Friseursalon.
	 * @param friseur Der Friseur der seinen Dienst in dem Salon verrichtet
	 */
	public synchronized void oeffne(Friseur friseur) {
		_friseur = Objects.requireNonNull(friseur);
		_istGeoeffnet = true;
		notify();
		System.out.println("Der Salon ist nun geöffnet!");
	}
	
	/**
	 * Schließt den Friseursalon.
	 */
	public synchronized void schliesse() {
		_istGeoeffnet = false;
		_warteraum.alleRausschmeissen();
		System.out.println("Der Salon ist nun geschlossen!");
	}
}

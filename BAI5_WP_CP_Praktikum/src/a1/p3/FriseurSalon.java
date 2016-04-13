package a1.p3;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * Ein Friseursalon. In dem Friseursalon arbeitet ein {@link Friseur}.
 * {@link Kunde}n können den Friseursalon betreten um sich die Haare schneiden zu lassen.
 * Da es nur einen Friseur gibt, könnte es sein, dass ein Kunde entsprechend in
 * eine Warteschlange kommt.
 */
public class FriseurSalon {
	private int _warteraumPlatz;
	private Queue<Kunde> _warteraum;
	private Friseur _friseur;
	private volatile boolean _istGeoeffnet;

	/**
	 * Instanziiert einen neuen Salon.
	 * @param wartePlaetze Die Anzahl der Warteplätze, die der Salon hat
	 */
	public FriseurSalon(int wartePlaetze) {
		_warteraumPlatz = wartePlaetze;
		_warteraum = new LinkedList<>();
	}

	/**
	 * Lässt einen Kunden den Salon betreten. Wenn der Friseursalon freie Warteplätze hat,
	 * dann stellt sich der Kunde dort an und benachrichtigt den Friseur. Wenn keine freien
	 * Plätze vorhanden sind, verlässt er den Salon wieder ohne sich die Haare schneiden zu
	 * lassen.
	 * @param kunde Der Kunde
	 */
	public synchronized void betreten(Kunde kunde) {
		if (_istGeoeffnet) {
			StringBuffer sb = new StringBuffer();
			sb.append(kunde.getName()).append(" betritt den Salon");
			if (_warteraum.size() < _warteraumPlatz) {
				sb.append(" und stellt sich an!");
				_warteraum.offer(kunde);
				_friseur.aufwecken();
			} else {
				sb.append(" und verl�sst ihn wieder, da kein Platz frei ist!");
			}
			System.out.println(sb);
		} else {
			System.out.println(" Der Salon ist geschlossen "+ kunde.getName() + " geht wieder!");
		}
	}
	
	/**
	 * Prüft, ob die Warteschlange des Salons leer ist oder nicht.
	 * @return {@code true} wenn die Warteschlange leer ist, ansonsten {@code false}
	 */
	public synchronized boolean istWarteschlangeLeer() {
		return _warteraum.isEmpty();
	}
	
	/**
	 * Gibt den nächsten Kunden aus der Warteschlange zurück und entfernt ihn aus dieser.
	 * @return Der nächste Kunde
	 */
	public synchronized Kunde naechsterKunde() {
		return _warteraum.poll();
	}
	
	/**
	 * Öffnet den Friseursalon.
	 * @param friseur Der Friseur der seinen Dienst in dem Salon verrichtet
	 */
	public synchronized void oeffne(Friseur friseur) {
		_friseur = Objects.requireNonNull(friseur);
		_istGeoeffnet = true;
		_friseur.start();
		System.out.println("Der Salon ist nun geöffnet!");
	}
	
	/**
	 * Schließt den Friseursalon.
	 * <p>Dabei wird dem Friseur auch mitgeteilt, dass er seine Arbeit niederlegen kann.
	 */
	public synchronized void schliesse() {
		_istGeoeffnet = false;
		_friseur.interrupt();
		_warteraum.clear();
		System.out.println("Der Salon ist nun geschlossen!");
	}
}

package a1.p3;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Ein Friseur. Friseure schneiden {@link Kunde}n in einem {@link FriseurSalon} die Haare.
 * Der Friseur kennt nur den Warteraum des Salons und "holt sich" daher immer den nächsten
 * Kunden, dem er die Haare schneiden soll.
 */
public class Friseur extends Thread {
	private Warteraum _warteraum;
	private TimeUnit _timeUnit;
	private int _haareSchneidenDauer;
	
	/**
	 * Instanziiert einen neuen Friseur. Es kann konfiguriert werden, wielange
	 * der Friseur zum Schneiden der Haare benötigt.
	 * @param warteraum Der Warteraum des {@link FriseurSalon}s, in dem der Friseur arbeitet
	 * @param timeUnit Die Zeiteinheit in der die Dauer gemessen wird
	 * @param haareSchneidenDauer Die Dauer in der angegebenen Zeiteinheit, die für das Haareschneiden benötigt wird
	 */
	public Friseur(Warteraum warteraum, TimeUnit timeUnit, int haareSchneidenDauer) {
		_warteraum = Objects.requireNonNull(warteraum);
		_timeUnit = Objects.requireNonNull(timeUnit);
		_haareSchneidenDauer = haareSchneidenDauer;
		setName("Friseur");
	}
	
	@Override
	public void run() {
		try {
			while(!isInterrupted()) {
				Kunde kunde = aufKundenWarten();
				haareSchneiden(kunde);
			}
		} catch(InterruptedException e) {}
		_warteraum.alleRausschmeissen();
	}
	
	/**
	 * Weckt den Friseur auf, falls er gerade schläft weil keine Kunden da waren.
	 * @see #aufKundenWarten()
	 */
	public synchronized void aufwecken() {
		notify();
	}
	
	/**
	 * Wartet auf einen Kunden und schläft solange. Der Friseur kann dann geweckt werden,
	 * wenn ein neuer Kunde die Haare geschnitten bekommen möchte.
	 * @see #aufwecken()
	 */
	private synchronized Kunde aufKundenWarten() throws InterruptedException {
		System.out.println("[" + getName() + "] wartet auf neue Kunden!");
		while(_warteraum.istLeer()) {
			wait();
		}
		return _warteraum.naechster();
	}
	
	/**
	 * Schneidet dem übergebenen Kunden die Haare.
	 * Im Zweck der Simulation wird das Haareschneiden per zeitlichem Sleep dargestellt.
	 * @param kunde Der Kunde dessen Haare geschnitten werden sollen
	 */
	private void haareSchneiden(Kunde kunde) {
		System.out.println("[" + getName() + "] schneidet " + kunde.getName() + " die Haare!");
		while(!kunde.isZufrieden()) {
			try {
				_timeUnit.sleep(_haareSchneidenDauer);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			kunde.haareSchneiden(5);
		}
		System.out.println("[" + getName() + "] hat " + kunde.getName() + " die Haare geschnitten!");
	}
}

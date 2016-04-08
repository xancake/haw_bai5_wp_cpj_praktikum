package a1.p3;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Ein Friseur. Friseure schneiden {@link Kunde}n im {@link FriseurSalon} die Haare.
 */
public class Friseur extends Thread {
	private FriseurSalon _salon;
	private TimeUnit _timeUnit;
	private int _haareSchneidenDauer;
	
	/**
	 * Instanziiert einen neuen Friseur. Der Friseur "meldet sich" selbst�ndig bei dem
	 * {@link FriseurSalon} f�r den er arbeitet an. Es kann konfiruriert werden, wielange
	 * der Friseur zum Schneiden der Haare ben�tigt.
	 * @param salon Der Salon in dem der Friseur arbeitet
	 * @param timeUnit Die Zeiteinheit in der die Dauer gemessen wird
	 * @param haareSchneidenDauer Die Dauer in der angegebenen Zeiteinheit, die f�r das Haareschneiden ben�tigt wird
	 */
	public Friseur(FriseurSalon salon, TimeUnit timeUnit, int haareSchneidenDauer) {
		_salon = Objects.requireNonNull(salon);
		_timeUnit = Objects.requireNonNull(timeUnit);
		_haareSchneidenDauer = haareSchneidenDauer;
		setName("Friseur");
	}
	
	@Override
	public void run() {
		while(!isInterrupted()) {
			aufKundenWarten();
			haareSchneiden(_salon.naechsterKunde());
		}
	}
	
	/**
	 * Weckt den Friseur auf, falls er gerade schl�ft weil keine Kunden da waren.
	 * @see #aufKundenWarten()
	 */
	public synchronized void aufwecken() {
		notify();
	}
	
	/**
	 * Wartet auf einen Kunden und schl�ft solange. Der Friseur kann dann geweckt werden,
	 * wenn ein neuer Kunde die Haare geschnitten bekommen m�chte.
	 * @see #aufwecken()
	 */
	private synchronized void aufKundenWarten() {
		while(_salon.istWarteschlangeLeer()) {
			try {
				System.out.println(getName() + " wartet auf neue Kunden!");
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	/**
	 * Schneidet dem �bergebenen Kunden die Haare.
	 * Im Zweck der Simulation wird das Haareschneiden per zeitlichem Sleep dargestellt.
	 * @param kunde Der Kunde dessen Haare geschnitten werden sollen
	 */
	private void haareSchneiden(Kunde kunde) {
		System.out.println(getName() + " schneidet " + kunde.getName() + " die Haare!");
		try {
			_timeUnit.sleep(_haareSchneidenDauer);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		System.out.println(getName() + " hat " + kunde.getName() + " die Haare geschnitten!");
	}
}

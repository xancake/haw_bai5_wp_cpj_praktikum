package a1.p3;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Ein Generator für {@link Kunde}n. Der Generator generiert in einem festgelegten Zeitintervall Kunden,
 * die dann eigenständig den {@link FriseurSalon} betreten wollen.
 */
public class KundenGenerator extends Thread {
	private FriseurSalon _salon;
	private TimeUnit _timeUnit;
	private int _generatorIntervall;
	
	public KundenGenerator(FriseurSalon salon, TimeUnit timeUnit, int generatorIntervall) {
		if(generatorIntervall < 0) {
			throw new IllegalArgumentException("Der Generatorintervall muss positiv sein!");
		}
		_salon = Objects.requireNonNull(salon);
		_timeUnit = Objects.requireNonNull(timeUnit);
		_generatorIntervall = generatorIntervall;
		setName("Kundengenerator");
	}
	
	@Override
	public void run() {
		while(!isInterrupted()) {
			int haarLaenge = (int)(Math.random() * 100);
			int zielLaenge = haarLaenge - (int)(Math.random() * haarLaenge);
			Kunde kunde = new Kunde(_salon, haarLaenge, zielLaenge);
			kunde.start();
			try {
				_timeUnit.sleep(_generatorIntervall);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}

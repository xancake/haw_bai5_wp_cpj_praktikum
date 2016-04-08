package a1.p3;

import java.util.concurrent.TimeUnit;

public class A1P3Starter {
	public static void main(String[] args) {
		FriseurSalon salon = new FriseurSalon(10);
		Friseur friseur = new Friseur(salon, TimeUnit.SECONDS, 5);
		KundenGenerator generator = new KundenGenerator(salon, TimeUnit.SECONDS, 2);
		
		salon.oeffne(friseur);
		generator.start();
		try {
			TimeUnit.SECONDS.sleep(30);
		} catch (InterruptedException e) {}
		salon.schliesse();
		generator.interrupt();
	}
}

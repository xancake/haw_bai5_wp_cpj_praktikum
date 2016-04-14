package a1.p3;

import java.util.concurrent.TimeUnit;

public class A1P3Starter {
	public static void main(String[] args) {
		FriseurSalon salon = new FriseurSalon(10);
		Friseur friseur = new Friseur(salon, TimeUnit.MILLISECONDS, 500);
		KundenGenerator generator = new KundenGenerator(salon, TimeUnit.SECONDS, 1);
		
		friseur.start();
		generator.start();
		try {
			TimeUnit.SECONDS.sleep(15);
		} catch (InterruptedException e) {}
		friseur.interrupt();
		generator.interrupt();
	}
}

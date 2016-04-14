package a1.p3;

import java.util.concurrent.TimeUnit;

public class A1P3Starter {
	public static void main(String[] args) {
		FriseurSalon salon = new FriseurSalon(10, TimeUnit.MILLISECONDS, 300);
		KundenGenerator generator = new KundenGenerator(salon, TimeUnit.SECONDS, 1);
		
		salon.oeffne();
		generator.start();
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {}
		salon.schliesse();
		generator.interrupt();
	}
}

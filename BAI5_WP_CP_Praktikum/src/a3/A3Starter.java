package a3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import _untouchable_.shipPart5.TestAndEnvironment_A;

public class A3Starter extends TestAndEnvironment_A {
	public static void main(String... args) {
		new A3Starter().letThereBeLife();
	}
	
	@Override
	public void doTest(
			Integer requestedNumberOfSmurfs,
			Integer requestedNumberOfShips,
			Integer requestedNumberOfLandings,
			Integer requestedMaximumNumberOfSmurfsPerShip,
			Integer requestedMaximumNumberOfShipsPerLanding
	) {
		Insel insel = new Insel(requestedNumberOfLandings, requestedMaximumNumberOfShipsPerLanding);
		
		List<Landing> haltestellen = new ArrayList<>(insel.getLandings());
		
		List<Ship> schiffe = new ArrayList<>();
		for(int i=0; i<requestedNumberOfShips; i++) {
			Ship schiff = new Ship(i, requestedNumberOfShips, new LinkedList<>(haltestellen), (i%2==0 ? Richtung.IM_UHRZEIGERSINN : Richtung.GEGEN_UHRZEIGERSINN));
			schiffe.add(schiff);
			new Thread(schiff).start();
			
			if(i%2==0) {
				haltestellen.add(haltestellen.remove(0));
				haltestellen.add(haltestellen.remove(0));
			}
			Collections.reverse(haltestellen);
		}
		
		List<Thread> schlumpfThreads = new ArrayList<>();
		for(int i=0; i<requestedNumberOfSmurfs; i++) {
			Smurf schlumpf = new Smurf(i, insel);
			Thread schlumpfThread = new Thread(schlumpf);
			schlumpfThreads.add(schlumpfThread);
			schlumpfThread.start();
			try {
				Smurf.waitUntilNextArrival();
			} catch(InterruptedException cannotHappen) {}
		}
		
		schlumpfThreads.forEach(schlumpfThread -> {
			try {
				schlumpfThread.join();
			} catch(InterruptedException cannotHappen) {}
		});
		
		schiffe.forEach(schiff -> schiff.terminate());
	}
	
	public static final boolean DEBUG_SMURF = true;
	public static final boolean DEBUG_SHIP  = true;
	
	@Override
	public Integer getWantedNumberOfSmurfs() {
		return 200;
	}
	
	@Override
	public Integer getWantedNumberOfShips() {
		return 6;
	}
	
	@Override
	public Integer getWantedNumberOfLandings() {
		return 6;
	}
	
	@Override
	public Integer getWantedMaximumNumberOfSmurfsPerShip() {
		return 97;
	}
	
	@Override
	public Integer getWantedMaximumNumberOfShipsPerLanding() {
		return 2;
	}
	
	@Override
	public String getAuthor() {
		return "Lars und Robert";
	}
}

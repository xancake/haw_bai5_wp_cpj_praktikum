package a2;

import java.util.Objects;
import _untouchable_.busPart4.SSI;
import _untouchable_.busPart4.Smurf_A;

public class Smurf extends Smurf_A implements Runnable {
	private SmurfWorld _world;
	private int _id;
	
	public Smurf(SmurfWorld world, int id) {
		_world = Objects.requireNonNull(world);
		_id = id;
	}
	
	@Override
	public void run() {
		// An der ersten Haltestelle des Schedules beginnen
		SSI ssi = schedule.next();
		BusStop currentBusStop = _world.getBusStop(ssi.getPlanedPosition());
		
		while(schedule.hasNext()) {
			BusStop targetBusStop = _world.getBusStop(ssi.getPlanedPosition());
			
			if(currentBusStop.getLocation() != targetBusStop.getLocation()) {
				// Müssen noch hinfahren
				try {
					// Solange in der Schleife verweilen, bis wir einem Bus beigetreten sind, der in unsere Richtung fährt
					Bus currentBus = null;
					while(currentBus == null) {
						currentBus = currentBusStop.inBusSteigen(this, ssi.getPlanedPosition());
					}
					
					// Solange wir nicht am Ziel sind, müssen wir mit dem Bus fahren
					beThere(currentBus);
					while(targetBusStop.getLocation() != currentBusStop.getLocation()) {
						synchronized(currentBus) {
							currentBus.wait();
							currentBusStop = currentBus.getCurrentBusStop();
						}
					}
					
					currentBus.leaveBus(this);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Sind an der Zielhaltestelle
			try {
				currentBusStop = targetBusStop;
				takeTimeForDoingStuffAtCurrentPosition(currentBusStop.getLocation(), ssi);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			// nächsten Schedule-Eintrag
			ssi = schedule.next();
		}
		lastDeed();
	}
	
	@Override
	public int identify() {
		return _id;
	}
	
	@Override
	public boolean getDebugState() {
		return Debug.DEBUG_SMURF;
	}
}

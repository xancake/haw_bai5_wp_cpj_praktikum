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
		BusStop currentBusStop = null;
		while(schedule.hasNext()) {
			SSI ssi = schedule.next();
			BusStop targetBusStop = _world.getBusStop(ssi.getPlanedPosition());
			
			// An der ersten Haltestelle des Schedules beginnen
			if(currentBusStop == null) {
				currentBusStop =  targetBusStop;
			}
			
			if(currentBusStop.getLocation() != targetBusStop.getLocation()) {
				// Müssen noch hinfahren
				try {
					// Solange in der Schleife verweilen, bis wir einem Bus beigetreten sind, der in unsere Richtung fährt
					Bus currentBus = null;
					while(currentBus == null) {
						currentBus = currentBusStop.ermittleBusNach(ssi.getPlanedPosition());
						if(currentBus!=null && !currentBus.tryEnterBus(this)) {
							currentBus = null;
						}
//						currentBus = currentBusStop.inBusSteigen(this, ssi.getPlanedPosition());
					}
					
					// Solange wir nicht am Ziel sind, müssen wir mit dem Bus fahren
					synchronized(currentBus) {
						beThere(currentBus);
						while(targetBusStop.getLocation() != currentBusStop.getLocation()) {
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

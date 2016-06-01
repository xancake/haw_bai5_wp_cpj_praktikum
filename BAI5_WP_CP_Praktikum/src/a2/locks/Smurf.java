package a2.locks;

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
		try {
			// An der ersten Haltestelle des Schedules beginnen
			SSI ssi = schedule.next();
			BusStop currentBusStop = _world.getBusStop(ssi.getPlanedPosition());
			takeTimeForDoingStuffAtCurrentPosition(currentBusStop.getLocation(), ssi);
			
			while(schedule.hasNext()) {
				ssi = schedule.next();
				BusStop targetBusStop = _world.getBusStop(ssi.getPlanedPosition());
				
				Bus currentBus = currentBusStop.betreteBusNach(this, targetBusStop.getLocation());
				
				beThere(currentBus);
				currentBus.awaitArrivalAt(targetBusStop);
				currentBusStop = targetBusStop;
				
				currentBusStop.verlasseBus(this, currentBus);
				
				takeTimeForDoingStuffAtCurrentPosition(currentBusStop.getLocation(), ssi);
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			lastDeed();
		}
	}
	
	@Override
	public int identify() {
		return _id;
	}
	
	@Override
	public boolean getDebugState() {
		return A2LocksStarter.DEBUG_SMURF;
	}
}

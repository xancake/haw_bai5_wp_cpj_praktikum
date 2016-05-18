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
		BusStop currentBusStop = null;
		try {
			while(schedule.hasNext()) {
				SSI ssi = schedule.next();
				BusStop targetBusStop = _world.getBusStop(ssi.getPlanedPosition());
				
				// An der ersten Haltestelle des Schedules beginnen
				if(currentBusStop == null) {
					currentBusStop =  targetBusStop;
				}

				// Müssen noch hinfahren
				if(currentBusStop.getLocation() != targetBusStop.getLocation()) {
					Bus currentBus = currentBusStop.betreteBusNach(this, targetBusStop.getLocation());
					
					// Im Bus sein und warten, bis die Zielhaltestelle erreicht wurde
					beThere(currentBus);
					currentBus.awaitArrivalAt(targetBusStop);
					currentBusStop = targetBusStop;
					
					currentBusStop.verlasseBus(this, currentBus);
				}
				
				// Sind an der Zielhaltestelle
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

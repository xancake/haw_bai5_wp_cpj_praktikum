package a2.locks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import a2.Debug;
import a2.Direction;
import _untouchable_.busPart4.Bus_A;

public class Bus extends Bus_A implements Runnable {
	private Lock _lock = new ReentrantLock();
	private Map<Integer, Condition> _stopConditions;
	
	private Lock _passengerLock = new ReentrantLock();
	private int _freeSeats;
	
	private SmurfWorld _world;
	private BusStop _currentStop;
	private int _startBusStop;
	private Direction _direction;
	
	private int _id;
	private boolean _run;
	
	public Bus(SmurfWorld world, int startBusStop, Direction direction, int id, int seats) {
		_world = Objects.requireNonNull(world);
		if((startBusStop == _world.getFirstStop() && Direction.LEFT.equals(direction))
				|| (startBusStop == _world.getLastStop() && Direction.RIGHT.equals(direction))) {
			throw new IllegalArgumentException("Bus darf nicht in Richtung " + direction + " fahren wenn er an " + startBusStop + " startet");
		}
		if(seats < 1) {
			throw new IllegalArgumentException("Im Bus muss mindestens ein Sitzplatz vorhanden sein");
		}
		_stopConditions = new HashMap<Integer, Condition>();
		for(int i=0; i<_world.getBusStopCount(); i++) {
			_stopConditions.put(i, _lock.newCondition());
		}
		_freeSeats = seats;
		_startBusStop = startBusStop;
		_direction = direction;
		_id = id;
	}
	
	@Override
	public void run() {
		_run = true;
		try {
			_currentStop = null;
			BusStop nextStop = _world.getBusStop(_startBusStop);
			
			while(_run) {
				_currentStop = nextStop;
				_currentStop.anfahren(this);
				
				// Passagiere benachrichtigen, dass eine Haltestelle erreicht wurde
				try {
					_lock.lock();
					_stopConditions.get(_currentStop.getLocation()).signalAll();
				} finally {
					_lock.unlock();
				}
				
				takeTimeForStopoverAt(_currentStop.getLocation());

				_currentStop.abfahren(this);
				
				nextStop = _direction.getNextBusStop(_world, _currentStop);
				_currentStop = null;
				
				// Richtung umdrehen, wenn wir uns am Ende befinden
				if(nextStop.getLocation() == _world.getFirstStop() || nextStop.getLocation() == _world.getLastStop()) {
					_direction = _direction.swap();
				}
				
				takeTimeForBusRideTo(nextStop.getLocation());
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			lastDeed();
		}
	}
	
	public boolean tryEnterBus(Smurf smurf) {
		try {
			_passengerLock.lock();
			
			boolean hasFreeSeats = _freeSeats > 0;
			if(hasFreeSeats) {
				_freeSeats--;
				smurf.enter(this);
			}
			return hasFreeSeats;
		} finally {
			_passengerLock.unlock();
		}
	}
	
	public void leaveBus(Smurf smurf) {
		try {
			_passengerLock.lock();
			
			_freeSeats++;
			smurf.leave(this);
		} finally {
			_passengerLock.unlock();
		}
	}
	
	public void awaitArrivalAt(BusStop stop) throws InterruptedException {
		try {
			_lock.lock();
			_stopConditions.get(stop.getLocation()).await();
		} finally {
			_lock.unlock();
		}
	}
	
	public Direction getDirection() {
		return _direction;
	}
	
	@Override
	public void terminate() {
		_run = false;
	}
	
	@Override
	public int identify() {
		return _id;
	}
	
	@Override
	public boolean getDebugState() {
		return Debug.DEBUG_BUS;
	}
}

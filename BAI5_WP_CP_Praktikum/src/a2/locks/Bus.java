package a2.locks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import a2.Debug;
import _untouchable_.busPart4.Bus_A;

public class Bus extends Bus_A implements Runnable {
	private Lock _lock = new ReentrantLock();
	private Map<Integer, Condition> _stopConditions;
	
	private Lock _passengerLock = new ReentrantLock();
	private Condition _passengerCondition = _passengerLock.newCondition();
	private Semaphore _passengers;
	
	private SmurfWorld _world;
	private BusStop _currentStop;
	private int _startBusStop;
	private int _direction;
	
	private int _id;
	private boolean _run;
	
	public Bus(SmurfWorld world, int startBusStop, int direction, int id, int seats) {
		_world = Objects.requireNonNull(world);
		if((startBusStop == _world.getFirstStop() && direction < 0) || (startBusStop == _world.getLastStop() && direction > 0)) {
			throw new IllegalArgumentException("Bus darf nicht in Richtung " + direction + " wenn er an " + startBusStop + " startet");
		}
		if(seats < 1) {
			throw new IllegalArgumentException("Im Bus muss mindestens ein Sitzplatz vorhanden sein");
		}
		_stopConditions = new HashMap<Integer, Condition>();
		for(int i=0; i<_world.getBusStopCount(); i++) {
			_stopConditions.put(i, _lock.newCondition());
		}
		_passengers = new Semaphore(seats);
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
				
				int nextStopLocation = _currentStop.getLocation() + _direction;
				nextStop = _world.getBusStop(nextStopLocation);
				if(nextStopLocation == _world.getFirstStop() || nextStopLocation == _world.getLastStop()) {
					_direction *= -1; // Richtung umdrehen, wenn wir uns am Ende befinden
				}
				
				try {
					_passengerLock.lock();
					_currentStop.abfahren(this);
					_currentStop = null;
					// Schlümpfe die einsteigen wollen kicken
					_passengerCondition.signalAll();
				} finally {
					_passengerLock.unlock();
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
			
			// Solange der Bus an der Haltestelle ist und keine freien Plätze hat warten
			while(_currentStop != null && _passengers.availablePermits() <= 0) {
				_passengerCondition.await();
			}
			
			// Bus ist bereits abgefahren!
			if(_currentStop == null) {
				return false;
			}
			
			_passengers.acquire();
			smurf.enter(this);
			
			return true;
		} catch(InterruptedException e) {
			return false;
		} finally {
			_passengerLock.unlock();
		}
	}
	
	public void leaveBus(Smurf smurf) {
		try {
			_passengerLock.lock();
			
			_passengers.release();
			smurf.leave(this);
			
			_passengerCondition.signal();
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
	
	public int getDirection() {
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

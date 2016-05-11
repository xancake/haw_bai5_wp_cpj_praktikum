package a2.locks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import a2.Debug;
import a2.Direction;
import _untouchable_.busPart4.Bus_A;

public class Bus extends Bus_A implements Runnable {
	private Lock _lock = new ReentrantLock();
	private Map<BusStop, Condition> _stopConditions;
	
	private Lock _passengerLock = new ReentrantLock();
	
	private int _id;
	private int _freeSeats;
	private Stack<BusStop> _fahrplan;
	private Stack<BusStop> _fahrplanZurueck;
	private Direction _direction;
	
	private boolean _run;
	
	public Bus(int id, int seats, Stack<BusStop> fahrplan, Direction direction) {
		if(seats < 1) {
			throw new IllegalArgumentException("Im Bus muss mindestens ein Sitzplatz vorhanden sein");
		}
		
		_id = id;
		_freeSeats = seats;
		_fahrplan = Objects.requireNonNull(fahrplan);
		_fahrplanZurueck = new Stack<BusStop>();
		_direction = direction;
		
		_stopConditions = new HashMap<BusStop, Condition>();
		for(BusStop stop : _fahrplan) {
			_stopConditions.put(stop, _lock.newCondition());
		}
	}
	
	@Override
	public void run() {
		_run = true;
		try {
			
			BusStop currentStop = null;
			BusStop nextStop = _fahrplan.pop();
			
			while(_run) {
				_fahrplanZurueck.add(nextStop);
				currentStop = nextStop;
				currentStop.anfahren(this);
				
				// Passagiere benachrichtigen, dass eine Haltestelle erreicht wurde
				try {
					_lock.lock();
					_stopConditions.get(currentStop).signalAll();
				} finally {
					_lock.unlock();
				}
				
				takeTimeForStopoverAt(currentStop.getLocation());

				currentStop.abfahren(this);
				currentStop = null;
				nextStop = _fahrplan.pop();
				
				// Richtung umdrehen, wenn wir uns am Ende befinden
				if(_fahrplan.isEmpty()) {
					Stack<BusStop> temp = _fahrplan;
					_fahrplan = _fahrplanZurueck;
					_fahrplanZurueck = temp;
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
			_stopConditions.get(stop).await();
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

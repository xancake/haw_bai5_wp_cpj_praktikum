package a2.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BusStop {
	private Lock _lock = new ReentrantLock();
	private Condition _busArrives = _lock.newCondition();
	private Condition _busLeaves = _lock.newCondition();
	
	private int _location;
	private int _maxBusses;
	private List<Bus> _bussesAtBusStop;
	
	public BusStop(int location, int maxBusses) {
		_location = location;
		_maxBusses = maxBusses;
		_bussesAtBusStop = new ArrayList<Bus>();
	}
	
	public void anfahren(Bus bus) throws InterruptedException {
		try {
			_lock.lock();
			
			while(_bussesAtBusStop.size() >= _maxBusses) {
				_busLeaves.await();
			}
			
			bus.stopAt(_location);
			_bussesAtBusStop.add(bus);
			
			_busArrives.signalAll();
		} finally {
			_lock.unlock();
		}
	}
	
	public void abfahren(Bus bus) {
		try {
			_lock.lock();
			
			_bussesAtBusStop.remove(bus);
			bus.startFrom(_location);
			
			_busLeaves.signal();
		} finally {
			_lock.unlock();
		}
	}
	
	public Bus ermittleBusNach(int targetLocation) throws InterruptedException {
		try {
			_lock.lock();
			
			while(_bussesAtBusStop.isEmpty()) {
				_busArrives.await();
			}
			
			// Einen Bus ermitteln, der nach targetLocation fährt
			int direction = _location > targetLocation ? -1 : 1;
			for (Bus bus : _bussesAtBusStop) {
				if (bus.getDirection() == direction && bus.hasFreeSeats()) {
					return bus;
				}
			}
			
			// Kein Bus fährt nach targetLocation
			return null;
		} finally {
			_lock.unlock();
		}
	}
	
	public int getLocation() {
		return _location;
	}
}

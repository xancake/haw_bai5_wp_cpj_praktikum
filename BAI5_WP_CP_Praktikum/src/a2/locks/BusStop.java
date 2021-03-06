package a2.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import a2.Direction;

public class BusStop {
	private Lock _lock = new ReentrantLock();
	private Condition _busToTheLeftArrives = _lock.newCondition();
	private Condition _busToTheLeftLeaves = _lock.newCondition();
	private Condition _busToTheRightArrives = _lock.newCondition();
	private Condition _busToTheRightLeaves = _lock.newCondition();
	
	private int _location;
	private int _maxBusses;
	private List<Bus> _bussesToTheLeft;
	private List<Bus> _bussesToTheRight;
	
	public BusStop(int location, int maxBusses) {
		_location = location;
		_maxBusses = maxBusses;
		_bussesToTheLeft = new ArrayList<Bus>();
		_bussesToTheRight = new ArrayList<Bus>();
	}
	
	public void anfahren(Bus bus) throws InterruptedException {
		try {
			_lock.lock();
			
			while(_bussesToTheLeft.size() + _bussesToTheRight.size() >= _maxBusses) {
				getLeaveCondition(bus.getDirection()).await();
			}
			
			bus.stopAt(_location);
			getBusList(bus.getDirection()).add(bus);
			
			int freeSeats = bus.getFreeSeats();
			for(int i=0; i<freeSeats; i++) {
				getArriveCondition(bus.getDirection()).signal();
			}
		} finally {
			_lock.unlock();
		}
	}
	
	public void abfahren(Bus bus) {
		try {
			_lock.lock();
			
			getBusList(bus.getDirection()).remove(bus);
			bus.startFrom(_location);
			
			getLeaveCondition(bus.getDirection()).signal();
		} finally {
			_lock.unlock();
		}
	}
	
	public Bus betreteBusNach(Smurf smurf, int targetLocation) throws InterruptedException {
		Direction direction = _location > targetLocation ? Direction.LEFT : Direction.RIGHT;
		try {
			_lock.lock();

			List<Bus> busList = getBusList(direction);
			while(true) {
				while(busList.isEmpty()) {
					getArriveCondition(direction).await();
				}
				
				for(Bus bus : busList) {
					if(bus.tryEnterBus(smurf)) {
						return bus;
					}
				}
				
				getArriveCondition(direction).await();
			}
		} finally {
			_lock.unlock();
		}
	}
	
	public void verlasseBus(Smurf smurf, Bus bus) {
		try {
			_lock.lock();
			
			bus.leaveBus(smurf);
			
			getArriveCondition(bus.getDirection()).signal();
		} finally {
			_lock.unlock();
		}
	}
	
	public int getLocation() {
		return _location;
	}
	
	private List<Bus> getBusList(Direction direction) {
		return Direction.RIGHT.equals(direction) ? _bussesToTheRight : _bussesToTheLeft;
	}
	
	private Condition getLeaveCondition(Direction direction) {
		return Direction.RIGHT.equals(direction) ? _busToTheRightLeaves : _busToTheLeftLeaves;
	}
	
	private Condition getArriveCondition(Direction direction) {
		return Direction.RIGHT.equals(direction) ? _busToTheRightArrives : _busToTheLeftArrives;
	}
}

package a2.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BusStop {
	private Lock _lock = new ReentrantLock();
	private Condition _busToTheLeftArrives = _lock.newCondition();
	private Condition _busToTheLeftLeaves = _lock.newCondition();
	private Condition _busToTheLeftPassengers = _lock.newCondition();
	private Condition _busToTheRightArrives = _lock.newCondition();
	private Condition _busToTheRightLeaves = _lock.newCondition();
	private Condition _busToTheRightPassengers = _lock.newCondition();
	
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
			
			List<Bus> busList = getBusList(bus.getDirection());
			while(busList.size() >= _maxBusses) {
				getLeaveCondition(bus.getDirection()).await();
			}
			
			bus.stopAt(_location);
			busList.add(bus);
			
			getArriveCondition(bus.getDirection()).signalAll();
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
		int direction = _location > targetLocation ? -1 : 1;
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
				getPassengersCondition(direction).await();
			}
		} finally {
			_lock.unlock();
		}
	}
	
	public void verlasseBus(Smurf smurf, Bus bus) throws InterruptedException {
		try {
			_lock.lock();
			
			bus.leaveBus(smurf);
			getPassengersCondition(bus.getDirection()).signal();
		} finally {
			_lock.unlock();
		}
	}
	
	public int getLocation() {
		return _location;
	}
	
	private List<Bus> getBusList(int direction) {
		return direction>0 ? _bussesToTheRight : _bussesToTheLeft;
	}
	
	private Condition getLeaveCondition(int direction) {
		return direction>0 ? _busToTheRightLeaves : _busToTheLeftLeaves;
	}
	
	private Condition getArriveCondition(int direction) {
		return direction>0 ? _busToTheRightArrives : _busToTheLeftArrives;
	}
	
	private Condition getPassengersCondition(int direction) {
		return direction>0 ? _busToTheRightPassengers : _busToTheLeftPassengers;
	}
}

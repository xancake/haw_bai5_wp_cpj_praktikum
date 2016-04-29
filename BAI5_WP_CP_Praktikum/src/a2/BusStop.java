package a2;

import java.util.ArrayList;
import java.util.List;

public class BusStop {
	private int _location;
	private int _maxBusses;
	private List<Bus> _bussesAtBusStop;
	
	public BusStop(int location, int maxBusses) {
		_location = location;
		_maxBusses = maxBusses;
		_bussesAtBusStop = new ArrayList<Bus>();
	}
	
	public void anfahren(Bus bus) throws InterruptedException {
		synchronized(this) {
			while(_bussesAtBusStop.size() >= _maxBusses) {
				wait();
			}
			synchronized(_bussesAtBusStop) {
				bus.stopAt(_location);
				_bussesAtBusStop.add(bus);
				_bussesAtBusStop.notifyAll();
			}
		}
	}
	
	public void abfahren(Bus bus) {
		synchronized(this) {
			synchronized(_bussesAtBusStop) {
				_bussesAtBusStop.remove(bus);
				bus.startFrom(_location);
			}
			notify();
		}
	}
	
	public Bus ermittleBusNach(int targetLocation) throws InterruptedException {
		synchronized(_bussesAtBusStop) {
			while(_bussesAtBusStop.isEmpty()) {
				_bussesAtBusStop.wait();
			}
			
			int direction = _location > targetLocation ? -1 : 1;
			
			// Einen Bus ermitteln, der nach targetLocation fährt
			for (Bus bus : _bussesAtBusStop) {
				if (bus.getDirection() == direction) {
					return bus;
				}
			}
			
			// Kein Bus fährt nach targetLocation
			return null;
		}
	}
	
	public Bus inBusSteigen(Smurf smurf, int targetLocation) throws InterruptedException {
		synchronized(_bussesAtBusStop) {
			while(_bussesAtBusStop.isEmpty()) {
				_bussesAtBusStop.wait();
			}
			
			// Bus auswählen und einsteigen wenn Platz frei
			for (Bus bus : _bussesAtBusStop) {
				int direction = _location > targetLocation ? -1 : 1;
				if (bus.getDirection() == direction) {
					if(bus.tryEnterBus(smurf)) {
						return bus;
					}
				}
			}
			
			// Konnte bei keinem Bus einsteigen
			return null;
		}
	}
	
	public int getLocation() {
		return _location;
	}
}

package a2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BusStop {
	private int _location;
	private List<Bus> _bussesAtBusStop;
	
	public BusStop(int location) {
		_location = location;
		_bussesAtBusStop = new ArrayList<Bus>();
	}
	
	public void anfahren(Bus bus) {
		
		synchronized(_bussesAtBusStop) {
			bus.stopAt(_location);
			_bussesAtBusStop.add(bus);
			_bussesAtBusStop.notifyAll();
		}
		
		
	}
	
	public void abfahren(Bus bus) {
		
		synchronized(_bussesAtBusStop) {
			_bussesAtBusStop.remove(bus);
			bus.startFrom(_location);
		}
		
	}
	
	public void aufBusWarten(Smurf smurf) throws InterruptedException {
		synchronized(_bussesAtBusStop) {
			while(_bussesAtBusStop.isEmpty()) {
				_bussesAtBusStop.wait();
			}
		}
	}
	
	public int getLocation() {
		return _location;
	}
	
	public List<Bus> getBussesAtBusStop(){
		return Collections.unmodifiableList(_bussesAtBusStop);
	}
}

package a2;

import java.util.ArrayList;
import java.util.List;

public class SmurfWorld {
	private List<BusStop> _busStops;
	
	public SmurfWorld(int busStopCount) {
		_busStops = new ArrayList<BusStop>();
		for(int i=0; i<busStopCount; i++) {
			BusStop stop = new BusStop(i);
			_busStops.add(stop);
		}
	}
	
	public BusStop getBusStop(int location) {
		return _busStops.get(location);
	}
	
	public int getBusStopCount() {
		return _busStops.size();
	}
	
	public int getFirstStop() {
		return 0;
	}
	
	public int getLastStop() {
		return getBusStopCount()-1;
	}
}

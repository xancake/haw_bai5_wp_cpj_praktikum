package a2.locks;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class SmurfWorld {
	private List<BusStop> _fahrplanNachRechts;
	private List<BusStop> _fahrplanNachLinks;
	
	public SmurfWorld(int busStopCount, int maxBussesPerStop) {
		_fahrplanNachRechts = new LinkedList<BusStop>();
		_fahrplanNachLinks = new LinkedList<BusStop>();
		for(int i=0; i<busStopCount; i++) {
			BusStop stop = new BusStop(i, maxBussesPerStop);
			_fahrplanNachRechts.add(stop);
			_fahrplanNachLinks.add(0, stop);
		}
	}
	
	public Stack<BusStop> getFahrplanNachRechts() {
		Stack<BusStop> fahrplan = new Stack<BusStop>();
		fahrplan.addAll(_fahrplanNachLinks);
		return fahrplan;
	}
	
	public Stack<BusStop> getFahrplanNachLinks() {
		Stack<BusStop> fahrplan = new Stack<BusStop>();
		fahrplan.addAll(_fahrplanNachRechts);
		return fahrplan;
	}
	
	public BusStop getBusStop(int location) {
		return _fahrplanNachRechts.get(location);
	}
}

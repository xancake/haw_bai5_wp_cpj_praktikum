package a2;

import a2.locks.BusStop;
import a2.locks.SmurfWorld;

public enum Direction {
	LEFT(-1),
	RIGHT(1);
	
	private int _direction;
	
	private Direction(int direction) {
		_direction = direction;
	}
	
	public BusStop getNextBusStop(SmurfWorld world, BusStop currentStop) {
		return world.getBusStop(currentStop.getLocation() + _direction);
	}
	
	public Direction swap() {
		return LEFT == this ? RIGHT : LEFT;
	}
}

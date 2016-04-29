package a2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import _untouchable_.busPart4.Bus_A;

public class Bus extends Bus_A implements Runnable {
	private SmurfWorld _world;
	
	private BusStop _currentStop;
	private int _startBusStop;
	private int _direction;
	
	private int _id;
	private int _seats;
	private boolean _run;
	
	private List<Smurf> _passengers;
	
	public Bus(SmurfWorld world, int startBusStop, int direction, int id, int seats) {
		_world = Objects.requireNonNull(world);
		if((startBusStop == _world.getFirstStop() && direction < 0)
				|| (startBusStop == _world.getLastStop() && direction > 0)) {
			throw new IllegalArgumentException("Bus darf nicht in Richtung " + direction + " wenn er an "
					+ startBusStop + " startet");
		}
		if(seats < 1) {
			throw new IllegalArgumentException("Im Bus muss mindestens ein Sitzplatz vorhanden sein");
		}
		_startBusStop = startBusStop;
		_direction = direction;
		_id = id;
		_seats = seats;
		_passengers = new ArrayList<Smurf>(_seats);
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
				synchronized(this) {
					notifyAll();
				}
				
				takeTimeForStopoverAt(_currentStop.getLocation());
				
				int nextStopLocation = _currentStop.getLocation() + _direction;
				nextStop = _world.getBusStop(nextStopLocation);
				if(nextStopLocation == _world.getFirstStop() || nextStopLocation == _world.getLastStop()) {
					_direction *= -1; // Richtung umdrehen, wenn wir uns am Ende befinden
				}

				synchronized(_passengers) {
					_currentStop.abfahren(this);
					_currentStop = null;
					// Schlümpfe die einsteigen wollen kicken
					_passengers.notifyAll();
				}
				
				takeTimeForBusRideTo(nextStop.getLocation());
			}
		} catch(InterruptedException e) {
		}
		lastDeed();
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
	
	public boolean tryEnterBus(Smurf smurf) {
		synchronized(_passengers) {
			try {
				while(!hasFreeSeats()) {
					_passengers.wait();
					// Bus ist bereits abgefahren!
					if(_currentStop == null) {
						return false;
					}
				}
				_passengers.add(smurf);
				smurf.enter(this);
				return true;
			} catch(InterruptedException e) {
				// TODO: Wirklich das Warten unterbrechen, wenn der Bus losfahren will
				return false;
			}
		}
	}
	
	public void leaveBus(Smurf smurf) {
		synchronized(_passengers) {
			_passengers.remove(smurf);
			smurf.leave(this);
			_passengers.notify();
		}
	}
	
	public boolean hasFreeSeats() {
		return _passengers.size() < _seats;
	}
	
	public int getDirection(){
		return _direction;
	}
	
	public BusStop getCurrentBusStop() {
		return _currentStop;
	}
}

package a3;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import _untouchable_.shipPart5.Ship_A;

public class Ship extends Ship_A implements Runnable {
	private Lock _lock = new ReentrantLock();
	private Map<Landing, Condition> _landingConditions;
	
	private int _id;
	private final int _sitzplaetze;
	private Queue<Landing> _fahrplan;
	private List<Smurf> _passagiere;
	
	private boolean _running;
	
	public Ship(int id, int sitzplaetze, Queue<Landing> fahrplan) {
		_id = id;
		_sitzplaetze = sitzplaetze;
		_fahrplan = Objects.requireNonNull(fahrplan);
		_passagiere = new LinkedList<>();
		_landingConditions = new HashMap<>();
		for(Landing landing : _fahrplan) {
			_landingConditions.put(landing, _lock.newCondition());
		}
	}
	
	@Override
	public void run() {
		_running = true;
		Landing currentLanding = null;
		Landing nextLanding = _fahrplan.poll();
		try {
			while(_running) {
				_fahrplan.offer(nextLanding);
				currentLanding = nextLanding;
				currentLanding.andocken(this);
				
				// Passagiere benachrichtigen, dass eine Haltestelle erreicht wurde
				try {
					_lock.lock();
					_landingConditions.get(currentLanding).signalAll();
				} finally {
					_lock.unlock();
				}
				
				takeTimeForBoardingAt(currentLanding.getId());
				
				try {
					_lock.lock();
					currentLanding.abfahren(this);
				} finally {
					_lock.unlock();
				}
				
				nextLanding = _fahrplan.poll();
				takeTimeForSailingTo(nextLanding.getId());
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			lastDeed();
		}
	}
	
	@Override
	public void terminate() {
		_running = false;
	}
	
	public boolean tryBetreten(Smurf smurf) {
		 try {
			 _lock.lock();
			 if(!_passagiere.contains(smurf) && _passagiere.size() < _sitzplaetze) {
				 _passagiere.add(smurf);
				 smurf.enter(this);
				 return true;
			 } else {
				 return false;
			 }
		 } finally {
			 _lock.unlock();
		 }
	}
	
	public void verlassen(Smurf smurf) {
		 try {
			 _lock.lock();
			 if(_passagiere.contains(smurf)) {
				 _passagiere.remove(smurf);
				 smurf.leave(this);
			 }
		 } finally {
			 _lock.unlock();
		 }
	}
	
	public void awaitAnkunft(Landing landing) throws InterruptedException {
		try {
			_lock.lock();
			_landingConditions.get(landing).await();
		} finally {
			_lock.unlock();
		}
	}
	
	@Override
	public int identify() {
		return _id;
	}
	
	@Override
	public boolean getDebugState() {
		return Debug.DEBUG_SHIP;
	}
}

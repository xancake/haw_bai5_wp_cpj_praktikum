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
	
	private final int _id;
	private final int _sitzplaetze;
	private Queue<Landing> _fahrplan;
	private final Richtung _richtung;
	private List<Smurf> _passagiere;
	private Landing _currentLanding;
	private boolean _running;
	
	public Ship(int id, int sitzplaetze, Queue<Landing> fahrplan, Richtung richtung) {
		_id = id;
		_sitzplaetze = sitzplaetze;
		_fahrplan = Objects.requireNonNull(fahrplan);
		_richtung = Objects.requireNonNull(richtung);
		_passagiere = new LinkedList<>();
		_landingConditions = new HashMap<>();
		for(Landing landing : _fahrplan) {
			_landingConditions.put(landing, _lock.newCondition());
		}
	}
	
	@Override
	public void run() {
		_running = true;
		_currentLanding = null;
		Landing nextLanding = _fahrplan.poll();
		try {
			while(_running) {
				_fahrplan.offer(nextLanding);
				_currentLanding = nextLanding;
				_currentLanding.andocken(this);
				
				// Passagiere benachrichtigen, dass eine Haltestelle erreicht wurde
				try {
					_lock.lock();
					_landingConditions.get(_currentLanding).signalAll();
				} finally {
					_lock.unlock();
				}
				
				takeTimeForBoardingAt(_currentLanding.getId());
				
				try {
					_lock.lock();
					_currentLanding.abfahren(this);
					_currentLanding = null;
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
	
	public boolean tryBetreten(Smurf schlumpf) {
		try {
			_lock.lock();
			
			if(_currentLanding == null) {
				throw new IllegalStateException("Schlumpf '" + schlumpf + "' hat versucht das Schiff '" + this + "' zu betreten, obwohl das Schiff gerade garnicht an einer Haltestelle ist.");
			}
			if(_passagiere.contains(schlumpf)) {
				throw new IllegalStateException("Schlumpf '" + schlumpf + "' hat versucht das Schiff '" + this + "' zu betreten, ist aber bereits darin!");
			}
			if(_passagiere.size() >= _sitzplaetze) {
				return false;
			}
			
			_passagiere.add(schlumpf);
			schlumpf.enter(this);
			return true;
		} finally {
			_lock.unlock();
		}
	}
	
	public void verlassen(Smurf schlumpf) {
		try {
			_lock.lock();
			
			if(_currentLanding == null) {
				throw new IllegalStateException("Schlumpf '" + schlumpf + "' hat versucht das Schiff '" + this + "' zu verlassen, obwohl das Schiff gerade garnicht an einer Haltestelle ist.");
			}
			if(!_passagiere.contains(schlumpf)) {
				throw new IllegalStateException("Schlumpf '" + schlumpf + "' hat versucht das Schiff '" + this + "' zu verlassen, ist aber garnicht darin!");
			}
			
			_passagiere.remove(schlumpf);
			schlumpf.leave(this);
		} finally {
			_lock.unlock();
		}
	}
	
	public void erwarteAnkunft(Landing landing) throws InterruptedException {
		try {
			_lock.lock();
			_landingConditions.get(landing).await();
		} finally {
			_lock.unlock();
		}
	}
	
	public Richtung getRichtung() {
		return _richtung;
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

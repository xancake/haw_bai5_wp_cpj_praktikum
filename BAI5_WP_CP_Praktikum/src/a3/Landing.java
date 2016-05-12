package a3;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Landing {
	private Lock _lock = new ReentrantLock();
	private Condition _schiffImUhrzeigersinnArrives = _lock.newCondition();
	private Condition _schiffImUhrzeigersinnLeaves = _lock.newCondition();
	private Condition _schiffImUhrzeigersinnPassengers = _lock.newCondition();
	private Condition _schiffGegenUhrzeigersinnArrives = _lock.newCondition();
	private Condition _schiffGegenUhrzeigersinnLeaves = _lock.newCondition();
	private Condition _schiffGegenUhrzeigersinnPassengers = _lock.newCondition();
	
	private int _id;
	private int _maxAnzahlSchiffeAnHaltestelle;
	private List<Ship> _schiffeImUhrzeigersinn;
	private List<Ship> _schiffeGegenUhrzeigersinn;
	private int _maxLandings;
	
	public Landing(int id, int maxAnzahlSchiffeAnHaltestelle, int maxLandings) {
		_id = id;
		_maxAnzahlSchiffeAnHaltestelle = maxAnzahlSchiffeAnHaltestelle;
		_schiffeImUhrzeigersinn = new LinkedList<>();
		_schiffeGegenUhrzeigersinn = new LinkedList<>();
		_maxLandings = maxLandings;
	}
	
	public int getId() {
		return _id;
	}
	
	public void andocken(Ship schiff) throws InterruptedException {
		try {
			_lock.lock();
			
			while(_schiffeImUhrzeigersinn.size() + _schiffeGegenUhrzeigersinn.size() >= _maxAnzahlSchiffeAnHaltestelle) {
				getLeaveCondition(schiff.getRichtung()).await();
			}
			
			schiff.dockAt(_id);
			getSchiffList(schiff.getRichtung()).add(schiff);
			
			getArriveCondition(schiff.getRichtung()).signalAll();
		} finally {
			_lock.unlock();
		}
	}
	
	public void abfahren(Ship schiff) {
		try {
			_lock.lock();
			
			getSchiffList(schiff.getRichtung()).remove(schiff);
			schiff.castOff(_id);
			
			getLeaveCondition(schiff.getRichtung()).signal();
		} finally {
			_lock.unlock();
		}
	}
	
	public Ship betreteSchiffNach(Landing ziel, Smurf schlumpf) throws InterruptedException {
		Richtung richtung = berechneRichtung(this, ziel, _maxLandings);
		try {
			_lock.lock();
			
			List<Ship> schiffeInRichtung = getSchiffList(richtung);
			while(true) {
				while(schiffeInRichtung.isEmpty()) {
					getArriveCondition(richtung).await();
				}
				
				for(Ship schiff : schiffeInRichtung) {
					if(schiff.tryBetreten(schlumpf)) {
						return schiff;
					}
				}
				
				getPassengersCondition(richtung).await();
			}
		} finally {
			_lock.unlock();
		}
	}
	
	public void verlasseSchiff(Ship schiff, Smurf schlumpf) {
		try {
			_lock.lock();
			
			schiff.verlassen(schlumpf);
			
			getPassengersCondition(schiff.getRichtung()).signal();
		} finally {
			_lock.unlock();
		}
	}
	
	private List<Ship> getSchiffList(Richtung richtung) {
		return Richtung.IM_UHRZEIGERSINN.equals(richtung) ? _schiffeImUhrzeigersinn : _schiffeGegenUhrzeigersinn;
	}
	
	private Condition getLeaveCondition(Richtung richtung) {
		return Richtung.IM_UHRZEIGERSINN.equals(richtung) ? _schiffImUhrzeigersinnLeaves : _schiffGegenUhrzeigersinnLeaves;
	}
	
	private Condition getArriveCondition(Richtung richtung) {
		return Richtung.IM_UHRZEIGERSINN.equals(richtung) ? _schiffImUhrzeigersinnArrives : _schiffGegenUhrzeigersinnArrives;
	}
	
	private Condition getPassengersCondition(Richtung richtung) {
		return Richtung.IM_UHRZEIGERSINN.equals(richtung) ? _schiffImUhrzeigersinnPassengers : _schiffGegenUhrzeigersinnPassengers;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _id;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Landing other = (Landing) obj;
		if (_id != other._id)
			return false;
		return true;
	}
	
	private static Richtung berechneRichtung(Landing start, Landing ziel, int maxLandings) {
		int medianLandings = maxLandings / Richtung.values().length;
		int imUhrzeigersinnVon;
		int imUhrzeigersinnBis;
		if(start.getId() < medianLandings) {
			imUhrzeigersinnVon = start.getId();
			imUhrzeigersinnBis = start.getId() + medianLandings;
		} else {
			imUhrzeigersinnVon = start.getId() - medianLandings;
			imUhrzeigersinnBis = start.getId();
		}
		return ziel.getId() >= imUhrzeigersinnVon && ziel.getId() < imUhrzeigersinnBis ? Richtung.IM_UHRZEIGERSINN : Richtung.GEGEN_UHRZEIGERSINN;
	}
}

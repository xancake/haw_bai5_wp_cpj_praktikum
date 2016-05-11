package a3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Landing {
	private Lock _lock = new ReentrantLock();
	private Condition _schiffNachLinksArrives = _lock.newCondition();
	private Condition _schiffNachLinksLeaves = _lock.newCondition();
	private Condition _schiffNachLinksPassengers = _lock.newCondition();
	private Condition _schiffNachRechtsArrives = _lock.newCondition();
	private Condition _schiffNachRechtsLeaves = _lock.newCondition();
	private Condition _schiffNachRechtsPassengers = _lock.newCondition();
	
	private int _id;
	private int _maxAnzahlSchiffeAnHaltestelle;
	private List<Ship> _schiffeNachLinks;
	private List<Ship> _schiffeNachRechts;
	
	public Landing(int id, int maxAnzahlSchiffeAnHaltestelle) {
		_id = id;
		_maxAnzahlSchiffeAnHaltestelle = maxAnzahlSchiffeAnHaltestelle;
		_schiffeNachLinks = new ArrayList<>();
		_schiffeNachRechts = new ArrayList<>();
	}
	
	public int getId() {
		return _id;
	}
	
	public void andocken(Ship schiff) {
		schiff.dockAt(_id);
		
	}
	
	public void abfahren(Ship schiff) {
		schiff.castOff(_id);
		
	}
	
	public Ship betreteSchiffNach(Landing ziel, Smurf schlumpf) {
		
		return null;
	}
	
	public void verlasseSchiff(Ship schiff, Smurf schlumpf) {
		
	}
	
	private List<Ship> getSchiffList(int direction) {
		return direction>0 ? _schiffeNachRechts : _schiffeNachLinks;
	}
	
	private Condition getLeaveCondition(int direction) {
		return direction>0 ? _schiffNachLinksLeaves : _schiffNachRechtsLeaves;
	}
	
	private Condition getArriveCondition(int direction) {
		return direction>0 ? _schiffNachLinksArrives : _schiffNachRechtsArrives;
	}
	
	private Condition getPassengersCondition(int direction) {
		return direction>0 ? _schiffNachLinksPassengers : _schiffNachRechtsPassengers;
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
}

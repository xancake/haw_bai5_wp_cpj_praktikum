package a1.p3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Warteraum {
	private int _maxPlaetze;
	private Queue<Kunde> _warteschlange;
	
	public Warteraum(int maxPlaetze) {
		_maxPlaetze = maxPlaetze;
		_warteschlange = new LinkedList<Kunde>();
	}
	
	public synchronized boolean anstellen(Kunde kunde) {
		boolean hatGenugPlatz = _warteschlange.size() < _maxPlaetze;
		if(hatGenugPlatz) {
			_warteschlange.offer(kunde);
		}
		return hatGenugPlatz;
	}
	
	public synchronized Kunde naechster() {
		return _warteschlange.poll();
	}
	
	public synchronized boolean istLeer() {
		return _warteschlange.isEmpty();
	}
	
	public synchronized void alleRausschmeissen() {
		List<Kunde> kunden = new ArrayList<Kunde>(_warteschlange);
		for(Kunde kunde : kunden) {
			kunde.interrupt();
		}
		for(Kunde kunde : kunden) {
			try {
				kunde.join();
			} catch (InterruptedException e) {}
		}
		_warteschlange.clear();
	}
}

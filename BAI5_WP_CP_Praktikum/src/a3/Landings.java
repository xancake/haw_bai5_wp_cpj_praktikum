package a3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Landings {
	private List<Landing> _landings;
	
	public Landings(int landings, int anzahlSchiffeProHaltestelle) {
		_landings = new ArrayList<>();
		for(int i=0; i<landings; i++) {
			_landings.add(new Landing(i, anzahlSchiffeProHaltestelle, landings));
		}
	}
	
	public Landing getLanding(int id) {
		return _landings.get(id);
	}
	
	public List<Landing> getLandings() {
		return Collections.unmodifiableList(_landings);
	}
}

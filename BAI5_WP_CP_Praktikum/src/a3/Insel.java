package a3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Insel {
	private List<Landing> _landings;
	
	public Insel(int landings, int anzahlSchiffeProHaltestelle) {
		_landings = new ArrayList<>();
		for(int i=0; i<landings; i++) {
			_landings.add(new Landing(i, anzahlSchiffeProHaltestelle));
		}
	}
	
	public Landing getLanding(int id) {
		return _landings.get(id);
	}
	
	public List<Landing> getLandings() {
		return Collections.unmodifiableList(_landings);
	}
}

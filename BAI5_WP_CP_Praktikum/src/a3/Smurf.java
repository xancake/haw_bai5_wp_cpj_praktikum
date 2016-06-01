package a3;

import java.util.Objects;

import _untouchable_.shipPart5.SSI;
import _untouchable_.shipPart5.Smurf_A;

public class Smurf extends Smurf_A implements Runnable {
	private int _id;
	private Landings _insel;
	
	public Smurf(int id, Landings insel) {
		_id = id;
		_insel = Objects.requireNonNull(insel);
	}
	
	@Override
	public void run() {
		try {
			// An der ersten Haltestelle des Schedules beginnen
			SSI ssi = schedule.next();
			Landing currentLanding = _insel.getLanding(ssi.getPlanedPosition());
			takeTimeForDoingStuffAtCurrentPosition(currentLanding.getId(), ssi);
			
			while(schedule.hasNext()) {
				ssi = schedule.next();
				Landing nextLanding = _insel.getLanding(ssi.getPlanedPosition());
				
				Ship currentSchiff = currentLanding.betreteSchiffNach(nextLanding, this);
				
				beThere(currentSchiff);
				currentSchiff.erwarteAnkunft(nextLanding);
				currentLanding = nextLanding;
				
				currentLanding.verlasseSchiff(currentSchiff, this);
				
				takeTimeForDoingStuffAtCurrentPosition(currentLanding.getId(), ssi);
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			lastDeed();
		}
	}
	
	@Override
	public int identify() {
		return _id;
	}
	
	@Override
	public boolean getDebugState() {
		return A3Starter.DEBUG_SMURF;
	}
}

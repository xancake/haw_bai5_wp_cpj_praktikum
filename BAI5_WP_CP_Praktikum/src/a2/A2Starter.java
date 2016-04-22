package a2;

import java.util.ArrayList;
import java.util.List;
import _untouchable_.busPart4.TestAndEnvironment_A;

public class A2Starter extends TestAndEnvironment_A {
	public static void main(String[] args) {
		A2Starter starter = new A2Starter();
		starter.letThereBeLife();
	}

	@Override
	public void doTest(
			Integer requestedNumberOfSmurfs,
			Integer requestedNumberOfBuses,
			Integer requestedNumberOfBusSTops,
			Integer requestedMaximumNumberOfSmurfsPerSBus,
			Integer requestedMaximumNumberOfBusesPerBusSTop
	) {
		SmurfWorld world = new SmurfWorld(requestedNumberOfBusSTops);
		
		List<Bus> busse = new ArrayList<Bus>(requestedNumberOfBuses);
		for(int i=0; i<requestedNumberOfBuses; i++) {
			int startBusStop = (i%2==0 ? world.getFirstStop() : world.getLastStop());
			int direction = world.getLastStop() == startBusStop ? -1 : 1;
			Bus bus = new Bus(world, startBusStop, direction, i, requestedMaximumNumberOfSmurfsPerSBus);
			new Thread(bus).start();
			busse.add(bus);
		}
		
		List<Smurf> schluempfe = new ArrayList<Smurf>();
		List<Thread> schlumpfThreads = new ArrayList<Thread>();
		for(int i=0; i<requestedNumberOfSmurfs; i++) {
			Smurf schlumpf = new Smurf(world, i);
			Thread schlumpfThread = new Thread(schlumpf);
			schlumpfThread.start();
			schluempfe.add(schlumpf);
			schlumpfThreads.add(schlumpfThread);
			try {
				Smurf.waitUntilNextArrival();
			} catch(InterruptedException e) {
				// Kann von außen nicht interrupted werden
			}
		}
		
		for(Thread schlumpfThread : schlumpfThreads) {
			try {
				schlumpfThread.join();
			} catch(InterruptedException e) {
				// Kann von außen nicht interrupted werden
			}
		}
		
//		try {
//			Thread.sleep(10000);
//		} catch(InterruptedException e) {
//			e.printStackTrace();
//		}
//		
		for(Bus bus : busse) {
			bus.terminate();
		}
	}

	@Override
	public Integer getWantedNumberOfSmurfs() {
		return 1000;
	}

	@Override
	public Integer getWantedNumberOfBuses() {
		return 2;
	}

	@Override
	public Integer getWantedNumberOfBusStops() {
		return 5;
	}

	@Override
	public Integer getWantedMaximumNumberOfSmurfsPerBus() {
		return 17;
	}

	@Override
	public Integer getWantedMaximumNumberOfBusesPerBusStop() {
		return Integer.MAX_VALUE;
	}

	@Override
	public String getAuthor() {
		return "Lars Nielsen & Robert Scheffel";
	}
}

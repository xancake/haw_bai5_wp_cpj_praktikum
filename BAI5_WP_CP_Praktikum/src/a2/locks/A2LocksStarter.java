package a2.locks;

import java.util.ArrayList;
import java.util.List;

import a2.Direction;
import _untouchable_.busPart4.TestAndEnvironment_A;

public class A2LocksStarter extends TestAndEnvironment_A {
	public static void main(String[] args) {
		A2LocksStarter starter = new A2LocksStarter();
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
		SmurfWorld world = new SmurfWorld(requestedNumberOfBusSTops, requestedMaximumNumberOfBusesPerBusSTop);
		
		List<Bus> busse = new ArrayList<Bus>(requestedNumberOfBuses);
		for(int i=0; i<requestedNumberOfBuses; i++) {
			int startBusStop = (i%2==0 ? world.getFirstStop() : world.getLastStop());
			Direction direction = world.getLastStop() == startBusStop ? Direction.LEFT : Direction.RIGHT;
			Bus bus = new Bus(world, startBusStop, direction, i, requestedMaximumNumberOfSmurfsPerSBus);
			busse.add(bus);
			new Thread(bus).start();
		}
		
		List<Thread> schlumpfThreads = new ArrayList<Thread>();
		for(int i=0; i<requestedNumberOfSmurfs; i++) {
			Smurf schlumpf = new Smurf(world, i);
			Thread schlumpfThread = new Thread(schlumpf);
			schlumpfThreads.add(schlumpfThread);
			schlumpfThread.start();
			try {
				Smurf.waitUntilNextArrival();
			} catch(InterruptedException cannotHappen) {}
		}
		
		for(Thread schlumpfThread : schlumpfThreads) {
			try {
				schlumpfThread.join();
			} catch(InterruptedException cannotHappen) {}
		}
		
		for(Bus bus : busse) {
			bus.terminate();
		}
	}

	@Override
	public Integer getWantedNumberOfSmurfs() {
		return 100;
	}

	@Override
	public Integer getWantedNumberOfBusses() {
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
	public Integer getWantedMaximumNumberOfBussesPerBusStop() {
		return Integer.MAX_VALUE;
	}

	@Override
	public String getAuthor() {
		return "Lars Nielsen & Robert Scheffel";
	}
}

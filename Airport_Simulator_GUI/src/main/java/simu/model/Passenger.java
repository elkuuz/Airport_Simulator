package simu.model;

import simu.framework.Clock;
import simu.framework.Trace;

/**
 *  in a simulator
 *
 * TODO: This is to be implemented according to the requirements of the simulation model (data!)
 */
public class Passenger {
	private double arrivalTime;
	private double removalTime;
	private int id;
	private static int i = 1;
	private static long sum = 0;
    private boolean isPriority;
    private boolean checkIn;
    private boolean luggage;
    private boolean euCitizen;


	/**
	 * Create a unique 
	 */
	public Passenger() {
	    id = i++;
	    this.isPriority = false;
        this.checkIn = true;
        this.luggage = true;
        this.euCitizen = true;
		arrivalTime = Clock.getInstance().getTime();
		Trace.out(Trace.Level.INFO, "New  #" + id + " arrived at  " + arrivalTime);
	}

	/**
	 * Give the time when  has been removed (from the system to be simulated)
	 * @return  removal time
	 */
	public double getRemovalTime() {
		return removalTime;
	}

    public boolean getIsPriority(){
        return isPriority;
    }

    public boolean isCheckIn() {
        return checkIn;
    }

    public boolean isLuggage() {
        return luggage;
    }

    public boolean isEuCitizen() {
        return euCitizen;
    }

    /**
	 * Mark the time when the  has been removed (from the system to be simulated)
	 * @param removalTime  removal time
	 */
	public void setRemovalTime(double removalTime) {
		this.removalTime = removalTime;
	}

	/**
	 * Give the time when the  arrived to the system to be simulated
	 * @return  arrival time
	 */
	public double getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * Mark the time when the  arrived to the system to be simulated
	 * @param arrivalTime  arrival time
	 */
	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * Get the (unique) Passenger id
	 * @return  id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Report the measured variables of the . In this case to the diagnostic output.
	 */
	public void reportResults() {
		Trace.out(Trace.Level.INFO, "\nPassenger " + id + " ready! ");
		Trace.out(Trace.Level.INFO, "Passenger "   + id + " arrived: " + arrivalTime);
		Trace.out(Trace.Level.INFO,"Passenger "    + id + " removed: " + removalTime);
		Trace.out(Trace.Level.INFO,"Passenger "    + id + " stayed: "  + (removalTime - arrivalTime));

		sum += (removalTime - arrivalTime);
		double mean = sum/id;
		System.out.println("Current mean of the Passenger service times " + mean);
	}
}

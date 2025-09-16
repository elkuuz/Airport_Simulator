package simu.model;

import eduni.distributions.ContinuousGenerator;
import eduni.distributions.Normal;
import simu.framework.*;
import eduni.distributions.Negexp;

import java.util.Random;

/**
 * Main simulator engine.
 * <p>
 * TODO: This is the place where you implement your own simulator
 * <p>
 * Demo simulation case:
 * Simulate three service points, customer goes through all three service points to get serviced
 * --> SP1 --> SP2 --> SP3 -->
 */
public class MyEngine extends Engine {
    private ArrivalProcess arrivalProcess;
    private ServicePoint[] servicePoints;
    private final int SERVICE_POINT_COUNT;
    public static final boolean TEXTDEMO = true;
    public static final boolean FIXEDARRIVALTIMES = false;
    public static final boolean FXIEDSERVICETIMES = false;

    /**
     * Service Points and random number generator with different distributions are created here.
     * We use exponent distribution for customer arrival times and normal distribution for the
     * service times.
     */
    public MyEngine(int count) {
        this.SERVICE_POINT_COUNT = count;
        servicePoints = new ServicePoint[SERVICE_POINT_COUNT];

        if (TEXTDEMO) {
            /* special setup for the example in text
             * https://github.com/jacquesbergelius/PP-CourseMaterial/blob/master/1.1_Introduction_to_Simulation.md
             */
            Random r = new Random();

            ContinuousGenerator arrivalTime = null;
            if (FIXEDARRIVALTIMES) {
                /* version where the arrival times are constant (and greater than service times) */

                // make a special "random number distribution" which produces constant value for the customer arrival times
                arrivalTime = new ContinuousGenerator() {
                    @Override
                    public double sample() {
                        return 10;
                    }

                    @Override
                    public void setSeed(long seed) {
                    }

                    @Override
                    public long getSeed() {
                        return 0;
                    }

                    @Override
                    public void reseed() {
                    }
                };
            } else
                // exponential distribution is used to model customer arrivals times, to get variability between programs runs, give a variable seed
                arrivalTime = new Negexp(10, Integer.toUnsignedLong(r.nextInt()));

            ContinuousGenerator serviceTime = null;
            if (FXIEDSERVICETIMES) {
                // make a special "random number distribution" which produces constant value for the service time in service points
                serviceTime = new ContinuousGenerator() {
                    @Override
                    public double sample() {
                        return 9;
                    }

                    @Override
                    public void setSeed(long seed) {
                    }

                    @Override
                    public long getSeed() {
                        return 0;
                    }

                    @Override
                    public void reseed() {
                    }
                };
            } else
                // normal distribution used to model service times
                serviceTime = new Normal(10, 6, Integer.toUnsignedLong(r.nextInt()));

            servicePoints[0] = new ServicePoint(serviceTime, eventList, EventType.CHECK_IN);
            servicePoints[1] = new ServicePoint(serviceTime, eventList, EventType.LUGGAGE_DROP);
            servicePoints[2] = new ServicePoint(serviceTime, eventList, EventType.LUGGAGE_DROP_PRIORITY);
            servicePoints[3] = new ServicePoint(serviceTime, eventList, EventType.SECURITY);
            servicePoints[4] = new ServicePoint(serviceTime, eventList, EventType.SECURITY_PRIORITY);
            servicePoints[5] = new ServicePoint(serviceTime, eventList, EventType.PASSPORT_CONTROL);
            servicePoints[6] = new ServicePoint(serviceTime, eventList, EventType.PASSPORT_CONTROL_PRIORITY);
            servicePoints[7] = new ServicePoint(serviceTime, eventList, EventType.GATE);

            arrivalProcess = new ArrivalProcess(arrivalTime, eventList, EventType.ARR1);
        } else {
            /* more realistic simulation case with variable customer arrival times and service times */
            servicePoints[0] = new ServicePoint(new Normal(10, 6), eventList, EventType.CHECK_IN);
            servicePoints[1] = new ServicePoint(new Normal(10, 6), eventList, EventType.LUGGAGE_DROP);
            servicePoints[2] = new ServicePoint(new Normal(10, 6), eventList, EventType.LUGGAGE_DROP_PRIORITY);
            servicePoints[3] = new ServicePoint(new Normal(10, 6), eventList, EventType.SECURITY);
            servicePoints[4] = new ServicePoint(new Normal(10, 6), eventList, EventType.SECURITY_PRIORITY);
            servicePoints[5] = new ServicePoint(new Normal(10, 6), eventList, EventType.PASSPORT_CONTROL);
            servicePoints[6] = new ServicePoint(new Normal(10, 6), eventList, EventType.PASSPORT_CONTROL_PRIORITY);
            servicePoints[7] = new ServicePoint(new Normal(10, 6), eventList, EventType.GATE);

            arrivalProcess = new ArrivalProcess(new Negexp(15, 5), eventList, EventType.ARR1);
            /*
            OLI VALMIIKSI KOODISSA MUKANA
			servicePoints[0] = new ServicePoint(new Normal(10, 6), eventList, EventType.DEP1);
			servicePoints[1] = new ServicePoint(new Normal(10, 10), eventList, EventType.DEP2);
			servicePoints[2] = new ServicePoint(new Normal(5, 3), eventList, EventType.DEP3);

			arrivalProcess = new ArrivalProcess(new Negexp(15, 5), eventList, EventType.ARR1);*/
        }
    }

    @Override
    protected void initialize() {    // First arrival in the system
        arrivalProcess.generateNextEvent();
    }

    @Override
    protected void runEvent(Event t) {  // B phase events
        Passenger a;

        switch ((EventType) t.getType()) {
            case ARR1 -> {
                Passenger p = new Passenger();
                if (p.isCheckIn()) {
                    servicePoints[0].addQueue(p);  //lisää checkin jonoon
                    arrivalProcess.generateNextEvent();
                } else if (p.getIsPriority()) {
                    if (p.isLuggage()) {
                        servicePoints[2].addQueue(p);  //lisää priority luggage jonoon
                        arrivalProcess.generateNextEvent();
                    } else {
                        servicePoints[4].addQueue(p);  //lisää priority security jonoon
                        arrivalProcess.generateNextEvent();
                    }
                } else {
                    if (p.isLuggage()) {
                        servicePoints[1].addQueue(p);   //normi luggagee
                        arrivalProcess.generateNextEvent();
                    } else {
                        servicePoints[3].addQueue(p);  //normi security
                        arrivalProcess.generateNextEvent();
                    }
                }
            }
            case CHECK_IN -> {
                Passenger p = servicePoints[0].removeQueue();
                if (p.getIsPriority()) {
                    if (p.isLuggage()) {
                        servicePoints[2].addQueue(p);  //lisää priority luggage jonoon
                    } else {
                        servicePoints[4].addQueue(p);  //lisää priority security jonoon
                    }
                } else {
                    if (p.isLuggage()) {
                        servicePoints[1].addQueue(p);   //normi luggagee
                    } else {
                        servicePoints[3].addQueue(p);  //normi security
                    }
                }
            }
            case LUGGAGE_DROP -> {
                simu.model.Passenger p = servicePoints[1].removeQueue();
                servicePoints[3].addQueue(p);  //normi security
            }


            case LUGGAGE_DROP_PRIORITY -> {
                simu.model.Passenger p = servicePoints[2].removeQueue();
                servicePoints[4].addQueue(p);  //lisää priority security jonoon
            }

            case SECURITY -> {
                simu.model.Passenger p = servicePoints[3].removeQueue();
                servicePoints[4].addQueue(p);  //lisää priority security jonoon
            }

            case SECURITY_PRIORITY -> {
                simu.model.Passenger p = servicePoints[4].removeQueue();
                if (p.isEuCitizen()) {
                    servicePoints[7].addQueue(p);  //gatelle
                } else {
                    servicePoints[6].addQueue(p);  //passport security
                }
            }

            case PASSPORT_CONTROL -> {
                simu.model.Passenger p = servicePoints[5].removeQueue();
                servicePoints[7].addQueue(p);  //gatelle
            }

            case PASSPORT_CONTROL_PRIORITY -> {
                simu.model.Passenger p = servicePoints[6].removeQueue();
                servicePoints[7].addQueue(p);  //gatelle
            }

            case GATE -> {
                simu.model.Passenger p = servicePoints[7].removeQueue();
                p.setRemovalTime(Clock.getInstance().getClock());
                p.reportResults();
            }
        }
    }

    @Override
    protected void tryCEvents() {
        for (ServicePoint p : servicePoints) {
            if (!p.isReserved() && p.isOnQueue()) {
                p.beginService();
            }
        }
    }

    @Override
    protected void results() {
        System.out.println("Simulation ended at " + Clock.getInstance().getClock());
        System.out.println("Results ... are currently missing");
    }
}

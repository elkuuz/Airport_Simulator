package simu.model;

import controller.IControllerMtoV;
import eduni.distributions.*;
import simu.framework.ArrivalProcess;
import simu.framework.Clock;
import simu.framework.Engine;
import simu.framework.Event;

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
    public static final boolean TEXTDEMO = false; // set false to get more realistic simulation case
    public static final boolean FIXEDARRIVALTIMES = false;
    public static final boolean FXIEDSERVICETIMES = false;

    /**
     * Service Points and random number generator with different distributions are created here.
     * We use exponent distribution for customer arrival times and normal distribution for the
     * service times.
     */
    public MyEngine(int count, IControllerMtoV controller) {
        super(controller);
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

            servicePoints[0] = new ServicePoint(serviceTime, eventList, EventType.CHECK_IN,2);
            servicePoints[1] = new ServicePoint(serviceTime, eventList, EventType.LUGGAGE_DROP,4);
            servicePoints[2] = new ServicePoint(serviceTime, eventList, EventType.LUGGAGE_DROP_PRIORITY,5);
            servicePoints[3] = new ServicePoint(serviceTime, eventList, EventType.SECURITY,6);
            servicePoints[4] = new ServicePoint(serviceTime, eventList, EventType.SECURITY_PRIORITY,7);
            servicePoints[5] = new ServicePoint(serviceTime, eventList, EventType.PASSPORT_CONTROL,1);
            servicePoints[6] = new ServicePoint(serviceTime, eventList, EventType.PASSPORT_CONTROL_PRIORITY,67);
            servicePoints[7] = new ServicePoint(serviceTime, eventList, EventType.GATE,4);

            arrivalProcess = new ArrivalProcess(arrivalTime, eventList, EventType.ARR1);
        } else {
            /* more realistic simulation case with variable customer arrival times and service times */
            servicePoints[0] = new ServicePoint(new LogNormal(2.3, 0.5), eventList, EventType.CHECK_IN,3);
            servicePoints[1] = new ServicePoint(new Gamma(2.0, 5.0), eventList, EventType.LUGGAGE_DROP,6);
            servicePoints[2] = new ServicePoint(new Gamma(2.0, 5.0), eventList, EventType.LUGGAGE_DROP_PRIORITY,5);
            servicePoints[3] = new ServicePoint(new TruncatedNormal(12, 6), eventList, EventType.SECURITY,4);
            servicePoints[4] = new ServicePoint(new Normal(8, 4), eventList, EventType.SECURITY_PRIORITY,2);
            servicePoints[5] = new ServicePoint(new LogNormal(2.1, 0.7), eventList, EventType.PASSPORT_CONTROL,3);
            servicePoints[6] = new ServicePoint(new LogNormal(2.1, 0.7), eventList, EventType.PASSPORT_CONTROL_PRIORITY,66);
            servicePoints[7] = new ServicePoint(new Normal(5, 1), eventList, EventType.GATE,5);

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
    protected void initialization() {
        arrivalProcess.generateNext();}

    @Override
    protected void runEvent(Event t) {  // B phase events
        Passenger a;

        switch ((EventType) t.getType()) {
            case ARR1 -> {
                Passenger p = new Passenger();
                if (p.isCheckIn()) {
                    servicePoints[0].addQueue(p);  //lisää checkin jonoon
                    arrivalProcess.generateNext();
                } else if (p.getIsPriority()) {
                    if (p.isLuggage()) {
                        servicePoints[2].addQueue(p);  //lisää priority luggage jonoon
                        arrivalProcess.generateNext();
                    } else {
                        servicePoints[4].addQueue(p);  //lisää priority security jonoon
                        arrivalProcess.generateNext();
                    }
                } else {
                    if (p.isLuggage()) {
                        servicePoints[1].addQueue(p);   //normi luggagee
                        arrivalProcess.generateNext();
                    } else {
                        servicePoints[3].addQueue(p);  //normi security
                        arrivalProcess.generateNext();
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
                Passenger p = servicePoints[1].removeQueue();
                servicePoints[3].addQueue(p);  //normi security
            }


            case LUGGAGE_DROP_PRIORITY -> {
                Passenger p = servicePoints[2].removeQueue();
                servicePoints[4].addQueue(p);  //lisää priority security jonoon
            }

            case SECURITY -> {
                Passenger p = servicePoints[3].removeQueue();
                servicePoints[4].addQueue(p);  //lisää priority security jonoon
            }

            case SECURITY_PRIORITY -> {
                Passenger p = servicePoints[4].removeQueue();
                if (p.isEuCitizen()) {
                    servicePoints[7].addQueue(p);  //gatelle
                } else {
                    servicePoints[6].addQueue(p);  //passport security
                }
            }

            case PASSPORT_CONTROL -> {
                Passenger p = servicePoints[5].removeQueue();
                servicePoints[7].addQueue(p);  //gatelle
            }

            case PASSPORT_CONTROL_PRIORITY -> {
                Passenger p = servicePoints[6].removeQueue();
                servicePoints[7].addQueue(p);  //gatelle
            }

            case GATE -> {
                Passenger p = servicePoints[7].removeQueue();
                p.setRemovalTime(Clock.getInstance().getTime());
                p.reportResults();
            }
        }
    }

    @Override
    protected void tryCEvents() {
        for (ServicePoint p : servicePoints) {
            for (int i=0; i<p.getLineCount(); i++){
                if (!p.isReserved(i) && p.isOnQueue()) {
                    p.beginService(i);
                }
            }}
    }

    @Override
    protected void results() {
        for (ServicePoint serv : servicePoints) {
            serv.findMinMaxLengths();
        }
        int index = 1;
        for (ServicePoint serv : servicePoints) {
            System.out.println("Queue nro." + index + " max lengths are: " + serv.getMaxLenght());
            System.out.println("Queue nro." + index + " Min lengths are: " + serv.getMinLength());
            System.out.println("Queue nro." + index + " average lengths are: " + serv.getAverageLength());
            index++;
        }
        System.out.println("Simulation ended at " + Clock.getInstance().getTime());
        System.out.println("Results ... are currently missing");
    }
}

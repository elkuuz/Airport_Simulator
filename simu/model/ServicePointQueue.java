package simu.model;

import java.util.ArrayList;
import java.util.LinkedList;

public class ServicePointQueue {
    private EventType evtType;
    private boolean reserved = false;
    private int queueMinLength;
    private int queueMaxLength;
    private LinkedList<Passenger> queue = new LinkedList<>();
    private final ArrayList<Integer> lengths = new ArrayList<>();

    public ServicePointQueue(EventType evtType) {
        this.evtType = evtType;
    }

    public void setReserved() {
        this.reserved = true;
    }

    public boolean isReserved() {
        return this.reserved;
    }

    public void addQueue(Passenger a) {
        queue.addFirst(a);
        lengths.add(queue.toArray().length);    //päivittää muuttuneen pituuden jonoon
        if (this.queueMaxLength == 0 || this.queueMaxLength < queue.toArray().length) {

        }
    }

    public Passenger removeQueue() {
        this.reserved = false;
        return queue.poll();
    }

    public ArrayList<Integer> getLengths() {
        return this.lengths;
    }


}

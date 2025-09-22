package simu.model;

import java.util.ArrayList;

public class QueueStatistics {
    private int queueCount = 0;
    private ArrayList<ServicePointQueue> queues = new ArrayList<>();  //Hallitsee jonoja joita pyydetty luomaan
    private EventType eventType;
    private final ArrayList<Integer> allQueueLengthts = new ArrayList<>();  //hallitsee jonojen pituuksia
    private int queueMaxLength = Integer.MIN_VALUE;
    private int queueMinLength = Integer.MAX_VALUE;
    private double averageLenght;

    public QueueStatistics(int queueCount, EventType evtType) {
        this.queueCount = queueCount;
        for (int i = 0; i < this.queueCount; i++) {
            queues.add(new ServicePointQueue(evtType));
        }
    }

    public void findMinMaxLengths() {
        int shortest = Integer.MAX_VALUE;
        int longest = Integer.MIN_VALUE;
        int sum = 0;
        for (Integer length : queueLenghtList) {
            sum += length;
            if (length < shortest) {
                shortest = length;
            } if (length > longest) {
                longest =  length;
            }
        }
        this.queueMaxLength = longest;
        this.queueMinLength = shortest;
        this.averageLenght = (double) sum / queueLenghtList.size();
    }

}

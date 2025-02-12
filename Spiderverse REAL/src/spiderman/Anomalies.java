package spiderman;

public class Anomalies {
    
    //Attributes
    String anomalyName;
    int timeToReturn;
    
    //Constructor
    public Anomalies(String name, int time) {
        anomalyName = name;
        timeToReturn = time;
    }

    //Getters
    public String getAnomalyName() {
        return anomalyName;
    }

    public int getTimeToReturn() {
        return timeToReturn;
    }
}

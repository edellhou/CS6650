package PartTwo;


import io.swagger.client.model.LiftRide;

import java.util.concurrent.ThreadLocalRandom;

public class Generator {
    private Integer liftTime ;

    private Integer liftID ;

    private LiftRide ride;
    private Integer resortID;
    private String seasonID;
    private String dayID;
    private Integer skierID;

    public Generator() {

    }

    public LiftRide getRide() {
        return ride;
    }

    public void setRide(LiftRide ride) {
        this.ride = ride;
    }

    public Integer getLiftTime() {
        return liftTime;
    }

    public void setLiftTime(Integer liftTime) {
        this.liftTime = liftTime;
    }

    public Integer getLiftID() {
        return liftID;
    }

    public void setLiftID(Integer liftID) {
        this.liftID = liftID;
    }

    public Integer getResortID() {
        return resortID;
    }

    public void setResortID(Integer resortID) {
        this.resortID = resortID;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public void setSeasonID(String seasonID) {
        this.seasonID = seasonID;
    }

    public String getDayID() {
        return dayID;
    }

    public void setDayID(String dayID) {
        this.dayID = dayID;
    }

    public Integer getSkierID() {
        return skierID;
    }

    public void setSkierID(Integer skierID) {
        this.skierID = skierID;
    }

    public void generate(){
        this.setLiftTime(ThreadLocalRandom.current().nextInt(1, 361));
        this.setLiftID(ThreadLocalRandom.current().nextInt(1, 41));
        this.setResortID(ThreadLocalRandom.current().nextInt(1, 11));
        this.setSeasonID("2022");
        this.setDayID("1");
        this.setSkierID(ThreadLocalRandom.current().nextInt(1, 100001));
        LiftRide newRide = new LiftRide();
        newRide.setTime(this.getLiftTime());
        newRide.setLiftID(this.getLiftID());
        this.setRide(newRide);

    }

}

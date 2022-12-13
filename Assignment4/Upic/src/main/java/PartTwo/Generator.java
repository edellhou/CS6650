package PartTwo;


import io.swagger.client.model.LiftRide;

import java.util.concurrent.ThreadLocalRandom;

public class Generator {


    private LiftRide ride;
    private Integer resortID;
    private String seasonID;
    private String dayID;
    private Integer skierID;

    public Generator(LiftRide body, Integer resortID, String seasonID, String dayID, Integer skierID) {
        this.resortID = resortID;
        this.seasonID = seasonID;
        this.dayID = dayID;
        this.skierID = skierID;
        this.ride = body;

    }

    public LiftRide getRide() {
        return ride;
    }

    public void setRide(LiftRide ride) {
        this.ride = ride;
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



}

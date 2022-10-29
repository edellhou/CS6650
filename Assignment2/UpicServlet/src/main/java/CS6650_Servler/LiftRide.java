package CS6650_Servler;

public class LiftRide {
    private Integer time ;

    private Integer liftID ;

    public LiftRide(Integer liftTime, Integer liftID) {
        this.time = liftTime;
        this.liftID = liftID;
    }

    public Integer getLiftTime() {
        return time;
    }

    public void setLiftTime(Integer liftTime) {
        this.time = liftTime;
    }

    public Integer getLiftID() {
        return liftID;
    }

    public void setLiftID(Integer liftID) {
        this.liftID = liftID;
    }
}

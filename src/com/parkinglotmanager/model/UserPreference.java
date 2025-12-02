package com.parkinglotmanager.model;


 //Stores per-user parking preferences and simple schedule info.
 //One UserPreference is associated with one User.
 
public class UserPreference {

    private int userID;
    private Integer preferredLotID;            // may be null
    private String preferredArrivalTime;       // "Morning", "Afternoon"
    private String classLocationDescription;   // free-form notes

    public UserPreference() {
    }

    public UserPreference(int userID) {
        this.userID = userID;
    }

    public UserPreference(int userID, Integer preferredLotID,
                          String preferredArrivalTime,
                          String classLocationDescription) {
        this.userID = userID;
        this.preferredLotID = preferredLotID;
        this.preferredArrivalTime = preferredArrivalTime;
        this.classLocationDescription = classLocationDescription;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Integer getPreferredLotID() {
        return preferredLotID;
    }

    public void setPreferredLotID(Integer preferredLotID) {
        this.preferredLotID = preferredLotID;
    }

    public String getPreferredArrivalTime() {
        return preferredArrivalTime;
    }

    public void setPreferredArrivalTime(String preferredArrivalTime) {
        this.preferredArrivalTime = preferredArrivalTime;
    }

    public String getClassLocationDescription() {
        return classLocationDescription;
    }

    public void setClassLocationDescription(String classLocationDescription) {
        this.classLocationDescription = classLocationDescription;
    }

    @Override
    public String toString() {
        return "UserPreference{" +
                "userID=" + userID +
                ", preferredLotID=" + preferredLotID +
                ", preferredArrivalTime='" + preferredArrivalTime + '\'' +
                ", classLocationDescription='" + classLocationDescription + '\'' +
                '}';
    }
}

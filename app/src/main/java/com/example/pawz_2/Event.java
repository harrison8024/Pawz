package com.example.pawz_2;

public class Event {
    public String title;
    public String uid;
    public String detail;
    public String date;
    public String time;
    public String location;
    public int interested;
    public int attending;
    public String locationID;
    public Double lat;
    public Double lon;

    public Event(){

    }

    public Event(String title, String uid, String detail, String date, String time,
                 String location, String locationID, Double lat, Double lon, int interested, int attending){

        this.title = title;
        this.uid = uid;
        this.detail = detail;
        this.date = date;
        this.time = time;
        this.location = location;
        this.locationID = locationID;
        this.lat = lat;
        this.lon = lon;
        this.interested = interested;
        this.attending = attending;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getDetail() {
        return detail;
    }

    public int getAttending() {
        return attending;
    }

    public int getInterested() {
        return interested;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public String getUid() {
        return uid;
    }

    public String getTime() {
        return time;
    }

    public void setAttending(int attending) {
        this.attending = attending;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setInterested(int interested) {
        this.interested = interested;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTime(String time) {
        this.time = time;
    }

}

package de.nutboyz.nutsmoothie.commons;

import android.location.Location;

/**
 * Location class.
 * @author Mats
 */
public class NutLocation {

    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private double distance;

    public NutLocation(String name) {
        this.name = name;
    }

    public NutLocation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public Location getLocation() {
        Location location = new Location("");
        location.setLongitude(this.longitude);
        location.setLatitude(this.latitude);
        return location;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("name: ");
//        sb.append(name);
//        sb.append(", latitude: ");
//        sb.append(latitude);
//        sb.append(", longitude: ");
//        sb.append(longitude);
//        sb.append(", locationid: ");
//        sb.append(id);
//        return sb.toString();
        return name;
    }
}

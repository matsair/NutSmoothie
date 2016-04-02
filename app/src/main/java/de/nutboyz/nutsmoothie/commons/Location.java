package de.nutboyz.nutsmoothie.commons;

/**
 * Location class.
 * @author Mats
 */
public class Location {

    private int id;
    private String name;
    private double latitude;
    private double longitude;

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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ");
        sb.append(name);
        sb.append(", latitude: ");
        sb.append(latitude);
        sb.append(", longitude: ");
        sb.append(longitude);
        sb.append(", locationid: ");
        sb.append(id);
        return sb.toString();
    }
}

package com.bradleyboxer.scavengerhunt.v3;

public class GeoLocation {

    private float longitude;
    private float latitude;
    private int radius;

    public GeoLocation(float latitude, float longitude, int radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public int getRadius() {
        return radius;
    }
}

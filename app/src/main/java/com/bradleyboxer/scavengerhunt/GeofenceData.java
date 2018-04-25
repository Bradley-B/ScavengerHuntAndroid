package com.bradleyboxer.scavengerhunt;

import java.io.Serializable;

/**
 * Created by Bradley on 4/14/2018.
 */

public class GeofenceData implements Serializable {
    double latitude = 0;
    double longitude = 0;
    float radius = 0; //meters
    String name;

    public GeofenceData(double latitude, double longitude, float radius, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.name = name;
    }
}

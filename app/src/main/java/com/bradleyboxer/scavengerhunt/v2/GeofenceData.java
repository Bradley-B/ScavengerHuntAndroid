package com.bradleyboxer.scavengerhunt.v2;

import java.io.Serializable;

/**
 * Created by Bradley on 4/14/2018.
 */

public class GeofenceData implements Serializable {
    double latitude = 0;
    double longitude = 0;
    float radius = 0; //meters
    private String name;

    //TODO merge with ClueSegment, as they are really the same thing

    public GeofenceData(double latitude, double longitude, float radius, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GeofenceData)) return false;
        GeofenceData other = (GeofenceData) obj;
        boolean name = other.getName().equals(this.getName());
        boolean coord = other.latitude == this.latitude && other.longitude == this.longitude;
        boolean radius = other.radius == this.radius;
        return name && coord && radius;
    }
}

package com.bradleyboxer.scavengerhunt.v3;

import android.location.Location;

public class GeofenceClue extends Clue {

    private final Location location;
    private final GeofenceManager geofenceManager;

    public GeofenceClue(final String hintText, final String solvedText, final Location location, final GeofenceManager geofenceManager) {
        super(hintText, solvedText);
        this.location = location;
        this.geofenceManager = geofenceManager;
    }

    @Override
    boolean isSolved() {
        return false;
    }

    @Override
    boolean isActive() {
        return false;
    }

    @Override
    public void solved() {

    }

    @Override
    public void activate() {

    }

}

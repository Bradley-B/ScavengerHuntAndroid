package com.bradleyboxer.scavengerhunt.v3;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

public class GeofenceClue extends Clue {

    private boolean active;
    private final Location location;
    private final GeofenceManager geofenceManager;

    public GeofenceClue(String name, String hintText, String solvedText, final Location location, final GeofenceManager geofenceManager) {
        super(name, hintText, solvedText, Type.GEOFENCE);
        this.location = location;
        this.geofenceManager = geofenceManager;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    boolean isSolved() {
        return solved;
    }

    @Override
    boolean isActive() {
        return active;
    }

    @Override
    public void solved() {
        solved = true;
    }

    @Override
    public void activate() {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(getName())
                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAccuracy()
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setNotificationResponsiveness(0)
                .build();
        geofenceManager.addGeofence(geofence);
        active = true;
    }

}

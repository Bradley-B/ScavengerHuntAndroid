package com.bradleyboxer.scavengerhunt.v3;

import com.google.android.gms.location.Geofence;

public class GeofenceClue extends Clue {

    private final GeoLocation location;
    private final GeofenceManager geofenceManager;

    public GeofenceClue(String name, String hintText, String solvedText, final GeoLocation location, final GeofenceManager geofenceManager) {
        super(name, hintText, solvedText, Type.GEOFENCE);
        this.location = location;
        this.geofenceManager = geofenceManager;
    }

    public GeoLocation getLocation() {
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
                        location.getRadius()
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setNotificationResponsiveness(0)
                .build();
        geofenceManager.addGeofence(geofence);
        active = true;
    }

}

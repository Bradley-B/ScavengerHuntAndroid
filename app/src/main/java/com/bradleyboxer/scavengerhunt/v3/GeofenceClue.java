package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import com.bradleyboxer.scavengerhunt.R;
import com.google.android.gms.location.Geofence;

import java.util.UUID;

public class GeofenceClue extends Clue {

    private final GeoLocation location;
    private transient GeofenceManager geofenceManager;

    public GeofenceClue(String name, String hintText, String solvedText, final GeoLocation location, GeofenceManager geofenceManager, UUID uuid) {
        super(name, hintText, solvedText, Type.GEOFENCE, uuid);
        this.location = location;
        setGeofenceManager(geofenceManager);
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setGeofenceManager(GeofenceManager geofenceManager) {
        this.geofenceManager = geofenceManager;
    }

    public boolean shouldBeSolved(Location location) {
        Location clueLoc = new Location("Bradley Boxer");
        clueLoc.setLatitude(getLocation().getLatitude());
        clueLoc.setLongitude(getLocation().getLongitude());
        float dist = clueLoc.distanceTo(location);

        return isActive() && dist < getLocation().getRadius();
    }

    @Override
    public void solved() {
        super.solved();
        geofenceManager.removeGeofence(getName());
    }

    @Override
    public void activate() {
        if(geofenceManager==null) {
            Log.e("GEOFENCE", "Cannot register geofences because the geofence manager is null." +
                    " Did you load the scavenger hunt from a file? Use setGeofenceManager().");
            throw new RuntimeException("Error registering geofences");
        }

        super.activate();
        Geofence geofence = new Geofence.Builder()
                .setRequestId(getUuid().toString())
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
    }

    @Override
    public Drawable getDrawableIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.map_search, context.getTheme());
    }

    @Override
    public Clue deepCopy() {
        GeofenceClue clue = new GeofenceClue(getName(), getHintText(), getSolvedText(), location.deepCopy(), null, getUuid());
        for(UUID child : getChildren()) {
            clue.addChild(child);
        }
        return clue;
    }
}

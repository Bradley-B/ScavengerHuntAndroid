package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bradleyboxer.scavengerhunt.R;
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
    public void activate() {
        super.activate();
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
    }

    @Override
    public Drawable getDrawableIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.map_search, context.getTheme());
    }

    @Override
    public Class getActivityClass() {
        return null;
    }

}

package com.bradleyboxer.scavengerhunt.v3;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import java.util.UUID;

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("GEOFENCE STATUS", "handling new intent");
        
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e("GEOFENCE STATUS", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event  can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            for(Geofence triggeringGeofence : triggeringGeofences) {
                // Get the transition details. Request id is the uuid of the geofence
                UUID geofenceId = UUID.fromString(triggeringGeofence.getRequestId());

                //mark the geofence as solved
                ScavengerHunt scavengerHunt = FileUtil.loadScavengerHunt(this);

                Clue clue = scavengerHunt.getClue(geofenceId);
                scavengerHunt.solveClue(geofenceId);

                FileUtil.saveScavengerHunt(scavengerHunt, this);

                // Send notification and log the transition details.
                Notifications.sendNotification(clue.getName(), this);
                Log.i("GEOFENCE STATUS", "Entering geofence: "+clue.getName());
            }

        } else {
            // Log the error.
            Log.e("GEOFENCE STATUS", "invalid geofence transition type ERROR");
        }
    }



}

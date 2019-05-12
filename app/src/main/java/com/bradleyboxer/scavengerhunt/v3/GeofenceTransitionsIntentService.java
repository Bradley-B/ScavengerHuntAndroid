package com.bradleyboxer.scavengerhunt.v3;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bradleyboxer.scavengerhunt.R;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Date;
import java.util.List;

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
                // Get the transition details. Request id is the name of the geofence
                String geofenceName = triggeringGeofence.getRequestId();

                //mark the geofence as solved
                ScavengerHunt scavengerHunt = FileUtil.loadScavengerHunt(this);
                scavengerHunt.solveClue(geofenceName);
                FileUtil.saveScavengerHunt(scavengerHunt, this);

                // Send notification and log the transition details.
                Notifications.sendNotification(geofenceName, this);
                Log.i("GEOFENCE STATUS", "Entering geofence: "+geofenceName);
            }

        } else {
            // Log the error.
            Log.e("GEOFENCE STATUS", "invalid geofence transition type ERROR");
        }
    }



}

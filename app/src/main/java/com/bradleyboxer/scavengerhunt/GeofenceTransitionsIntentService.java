package com.bradleyboxer.scavengerhunt;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
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
            Geofence triggeringGeofence = triggeringGeofences.get(0); // get only one of the triggering geofences

            // Get the transition details. Request id is the name of the geofence
            String geofenceName = triggeringGeofence.getRequestId();

            // Send notification and log the transition details.
            Clue discoveredClue = getTriggeredClue(geofencingEvent);
            discoveredClue.setDiscovered();
            sendNotification(discoveredClue);
            Log.i("GEOFENCE STATUS", "Entering geofence: "+geofenceName);
        } else {
            // Log the error.
            Log.e("GEOFENCE STATUS", "invalid geofence transition type ERROR");
        }
    }

    private Clue getTriggeredClue(GeofencingEvent event) {
        List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
        Location triggeringLocation = event.getTriggeringLocation();

        for(Clue clue : Clues.clues) {
            boolean name = false;
            for(Geofence geofence : triggeringGeofences) {
                name = name || geofence.getRequestId().equals(clue.getGeofenceClue().getName());
            }

            float[] distanceBetween = new float[1];
            Location.distanceBetween(triggeringLocation.getLatitude(), triggeringLocation.getLongitude(),
                    clue.getGeofenceClue().latitude, clue.getGeofenceClue().longitude, distanceBetween);

            boolean location = distanceBetween[0]<=clue.getGeofenceClue().radius;
            if(name && location) {
                return clue;
            }
        }
        return new Clue("if you see this message please contact the developer \n" +
                "and tell them the geofence service has gained sentience and to get their shotgun",
                new GeofenceData(0, 0, 9001, "something something about errors and machine uprisings"));
    }

    private void sendNotification(Clue clue) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel("geofenceNotif", name, NotificationManager.IMPORTANCE_HIGH);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        //edit intent with the clue data
        notificationIntent.putExtra("clue", clue);
        Log.i("GEOFENCE STATUS", "sending notification for: "+clue.getGeofenceClue().getName());

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "geofenceNotif");

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle("You've discovered: "+clue.getGeofenceClue().getName())
                .setContentText("Click here to read the clue!")
                .setContentIntent(notificationPendingIntent);

        // Set the Channel ID for Android O. //TODO this is now done in the builder constructor... is it necessary?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("geofenceNotif"); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

}

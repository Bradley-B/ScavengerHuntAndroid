package com.bradleyboxer.scavengerhunt.v2;

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

import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.bradleyboxer.scavengerhunt.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

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

        //logically, the triggered clue is the one closest to where you are

        List<Geofence> triggeringGeofences = event.getTriggeringGeofences();

        Location triggeringLocation = event.getTriggeringLocation();

        Clue triggeredClue = null;
        float minDistanceBetween = Float.MAX_VALUE;
        for(Clue clue : Clues.getSafeClueList(getFilesDir())) {
            float[] distanceBetween = new float[1];
            Location.distanceBetween(triggeringLocation.getLatitude(), triggeringLocation.getLongitude(),
                    clue.getGeofenceGeofenceData().latitude, clue.getGeofenceGeofenceData().longitude, distanceBetween);
            if(distanceBetween[0]<minDistanceBetween) {
                minDistanceBetween = distanceBetween[0];
                triggeredClue = clue;
            }
        }

        if (triggeredClue != null) {
            return triggeredClue;
        } else {
            Log.e("GEOFENCE", "triggered clue that does not exist. See GeofenceTransitionsIntentService.java getTriggeredClue()." +
                    " Triggering geofence: "+triggeringGeofences.get(0).getRequestId());
            return new Clue("There was an error getting the clue named \""+triggeringGeofences.get(0).getRequestId()+
                    "\". Please restart the app.",
                    new GeofenceData(0, 0, 20, "Error"));
        }

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
        Log.i("GEOFENCE STATUS", "sending notification for: "+clue.getGeofenceGeofenceData().getName());

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
                .setContentTitle("You've discovered: "+clue.getGeofenceGeofenceData().getName())
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

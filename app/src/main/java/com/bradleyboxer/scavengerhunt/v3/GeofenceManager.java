package com.bradleyboxer.scavengerhunt.v3;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class GeofenceManager {

    private Context context;
    private PendingIntent geofencePendingIntent;

    public GeofenceManager(Context context) {
        this.context = context;
    }

    public void addGeofence(Geofence geofence) {
        List<Geofence> list = new ArrayList<>();
        list.add(geofence);
        addGeofences(list);
    }

    public void addGeofences(List<Geofence> geofences) {
        try {
            GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
            geofencingClient.addGeofences(getGeofencingRequest(geofences), getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("GEOFENCE STATUS", "Geofence(s) added successfully");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("GEOFENCE STATUS", "Geofence(s) failed to add");
                            e.printStackTrace();
                        }
                    });

        } catch (SecurityException e) {
            Log.e("GEOFENCE STATUS", "Geofence(s) failed to add - SecurityException");
            e.printStackTrace();
        }
    }

    public void removeGeofence(String requestId) {
        List<String> list = new ArrayList<>();
        list.add(requestId);
        removeGeofences(list);
    }

    public void removeGeofences(List<String> requestIds) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        geofencingClient.removeGeofences(requestIds)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("GEOFENCE STATUS", "Geofence(s) removed successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("GEOFENCE STATUS", "Geofence(s) failed to remove");
                    }
                });
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}

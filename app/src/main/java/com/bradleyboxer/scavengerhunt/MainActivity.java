package com.bradleyboxer.scavengerhunt;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GeofencingClient mGeofencingClient;
    List<Geofence> mGeofenceList;
    PendingIntent mGeofencePendingIntent;
    List<GeofenceData> geoData;
    GeofenceData triggeringGeofence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupGeofence();

        Intent intent = getIntent();
        String clue = intent.getStringExtra("clueFound"); //name of the geofence
        if(clue!=null) {
            String textToDisplay;
            if(clue.equals("Clue 1")) { //to display at home
                textToDisplay = Clues.getClueOne();
            } else if(clue.equals("Clue 1.5")) { //to display at kid street
                textToDisplay = Clues.getClueOne2();
            } else if(clue.equals("Clue 2")) { //to display at kid street [after compass]
                textToDisplay = Clues.getClueTwo();
            } else if(clue.equals("Clue 2.5")) { //to display at duke estate
                textToDisplay = Clues.getClueTwo2();
            } else if(clue.equals("Clue 3")) { //to display at duke estate [after compass]
                textToDisplay = Clues.getClueThree();
            } else if(clue.equals("Clue 3.5")) { //to display at colonial park
                textToDisplay = Clues.getClueThree2();
            } else if(clue.equals("Clue 4")) { //to display at colonial park [after compass]
                textToDisplay = Clues.getClueFour();
            } else if(clue.equals("Clue 4.5")) { //to display at hawk watch
                textToDisplay = Clues.getClueFour2();
            } else if(clue.equals("Clue 5")) { //to display at hawk watch [after compass]
                textToDisplay = Clues.getClueFive();
            } else {
                textToDisplay = "Oh shit someone messed up and that someone was me";
            }
            Log.i("GEOFENCE STATUS", "opened notification for: " + clue);
            Log.i("GEOFENCE STATUS", "displaying text: "+textToDisplay);
            Log.i("button or something", ""+clue.contains(".5"));

            if(clue.contains(".5")) {
                findViewById(R.id.button).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.button).setVisibility(View.INVISIBLE);
            }

            ((TextView) findViewById(R.id.geofenceDisplay)).setText(textToDisplay);
            triggeringGeofence = getGeofenceData(clue);
        }

        //////////////////////////////////////////////////////////////////////////////////

        //try {
        //    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //    ((TextView) findViewById(R.id.testText)).setText("you are at ("+location.getLatitude()+", "+location.getLongitude()+") (accuracy: "+location.getAccuracy()+")");
        //} catch (SecurityException e) {e.printStackTrace();} catch (Exception ex) {}
    }

    private void setupGeofence() {
        if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            try {
                throw new RuntimeException("google play not available. better luck next time");
            } catch (RuntimeException e) {e.printStackTrace();}
        }

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        geoData = new ArrayList<>();
        geoData.add(new GeofenceData(40.590889, -74.667181, 200, "Clue 1")); //home)
        geoData.add(new GeofenceData(40.591897, -74.624395, 200, "Clue 1.5")); //kid street park general area
        geoData.add(new GeofenceData(40.544621, -74.624019, 1000, "Clue 2.5")); //duke estate general area
        geoData.add(new GeofenceData(40.509199, -74.568875, 500, "Clue 3.5")); //colonial park general area
        geoData.add(new GeofenceData(40.582799, -74.553179, 500, "Clue 4.5")); //hawk watch general area

        mGeofenceList = new ArrayList<>();
        for(GeofenceData data : geoData) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(data.name)
                    .setCircularRegion(
                            data.latitude,
                            data.longitude,
                            data.radius
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setNotificationResponsiveness(0)
                    .build());
        }


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(mGeofenceList), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        Log.i("GEOFENCE STATUS", "Geofences sucessfully added");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        Log.e("GEOFENCE STATUS", "Geofences not added ERROR");
                        Log.e("GEOFENCE STATUS", e.getMessage());
                    }
                });


    }

    /**
     * triggered for x.5 clues, launches compass activity
     * @param v
     */
    public void onClick(View v) {
        Intent intent = new Intent(this, CompassActivity.class);
        GeofenceData nextClueGeofence = getNextClueGeofence();

        intent.putExtra("long", nextClueGeofence.longitude);
        intent.putExtra("lat", nextClueGeofence.latitude);
        intent.putExtra("radius", nextClueGeofence.radius);
        intent.putExtra("name", nextClueGeofence.name);

        startActivity(intent);
    }

    private GeofenceData getNextClueGeofence() {
        if(triggeringGeofence.name.contains("1.5")) {
            return new GeofenceData(40.591363, -74.624316, 15, "Clue 2"); //kid street park specific area
        } else if(triggeringGeofence.name.contains("2.5")) {
            return new GeofenceData(40.549693, -74.632098, 10, "Clue 3"); //duke estate specific area
        } else if(triggeringGeofence.name.contains("3.5")) {
            return new GeofenceData(40.507904, -74.574650, 20, "Clue 4"); //colonial park specific area
        } else if(triggeringGeofence.name.contains("4.5")) {
            return new GeofenceData(40.582283, -74.555999, 20, "Clue 5"); //hawk watch specific area
        }
        return null;
    }

    private GeofenceData getGeofenceData(String name) {
        for(GeofenceData data : geoData) {
            if(name.equals(data.name)) return data;
        }
        return null;
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

}

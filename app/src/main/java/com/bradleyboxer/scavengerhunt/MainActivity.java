package com.bradleyboxer.scavengerhunt;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GeofencingClient mGeofencingClient;
    List<Geofence> mGeofenceList;
    PendingIntent mGeofencePendingIntent;
    List<GeofenceData> geoData;
    Clue triggeringClue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupGeofence();

        Intent intent = getIntent();
        Clue clue = (Clue) intent.getSerializableExtra("clue");

        if(clue!=null) {
            String textToDisplay;
            if(clue.hasBeenSolved()) { //after compass
                textToDisplay = clue.getClueSolvedText();
                findViewById(R.id.button).setVisibility(View.INVISIBLE);
            } else if(clue.hasBeenDiscovered()) { //after geofence
                textToDisplay = clue.getClueDiscoveredText();
                if(!clue.isSimpleClue()) {
                    findViewById(R.id.button).setVisibility(View.VISIBLE);
                }
            } else {
                textToDisplay = "tell bradley his beloved clue structure is wrong";
            }

            ((TextView) findViewById(R.id.geofenceDisplay)).setText(textToDisplay);
            triggeringClue = clue;

            Log.i("GEOFENCE STATUS", "opened notification for: " + clue.getGeofenceClue().getName());
            Log.i("GEOFENCE STATUS", "displaying text: "+textToDisplay);
            Log.i("button or something", ""+clue.hasBeenDiscovered());
        }

        //////////////////////////////////////////////////////////////////////////////////

        //try {
        //    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //    ((TextView) findViewById(R.id.testText)).setText("you are at ("+location.getLatitude()+", "+location.getLongitude()+") (accuracy: "+location.getAccuracy()+")");
        //} catch (SecurityException e) {e.printStackTrace();} catch (Exception ex) {}
    }

    private void setupClues() {
        Clues.clues.add(new Clue(Clues.getClueOne(), new GeofenceData(40.590889, -74.667181, 200, "A New Journey")));

        Clues.clues.add(new Clue(Clues.getClueOne2(), new GeofenceData(40.591897, -74.624395, 200, "Kid Street"),
                Clues.getClueTwo(), new GeofenceData(40.591363, -74.624316, 15, "Your First Find")));

        Clues.clues.add(new Clue(Clues.getClueTwo2(), new GeofenceData(40.544621, -74.624019, 1000, "Duke Estate"),
                Clues.getClueThree(), new GeofenceData(40.549693, -74.632098, 10, "Another Letter For You")));

        Clues.clues.add(new Clue(Clues.getClueThree2(), new GeofenceData(40.509199, -74.568875, 500, "Colonial Park"),
                Clues.getClueFour(), new GeofenceData(40.507904, -74.574650, 20, "Letter Again")));

        Clues.clues.add(new Clue(Clues.getClueFour2(), new GeofenceData(40.582799, -74.553179, 500, "Hawk Watch"),
                Clues.getClueFive(), new GeofenceData(40.582283, -74.555999, 20, "Surprise")));

    }

    private void setupGeofence() {
        setupClues();

        if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            try {
                throw new RuntimeException("google play not available. try and restart the service, or something, I guess");
            } catch (RuntimeException e) {e.printStackTrace();}
        }


        geoData = new ArrayList<>();
        for(Clue clue : Clues.clues) {
            geoData.add(clue.getGeofenceClue());
        }

        mGeofenceList = new ArrayList<>();
        for(GeofenceData data : geoData) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(data.getName())
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

        mGeofencingClient = LocationServices.getGeofencingClient(this);
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
     * launches compass activity
     * @param v
     */
    public void onClick(View v) {
        Intent intent = new Intent(this, CompassActivity.class);
        intent.putExtra("clue", triggeringClue);
        startActivity(intent);
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

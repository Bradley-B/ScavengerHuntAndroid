package com.bradleyboxer.scavengerhunt.v2;

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

import com.bradleyboxer.scavengerhunt.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GeofencingClient mGeofencingClient;
    PendingIntent mGeofencePendingIntent;
    Clue triggeringClue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_activity_main);

        Intent intent = getIntent();

        ArrayList<Clue> clueList = (ArrayList<Clue>) intent.getSerializableExtra("clueList");
        if(clueList!=null && clueList.size()>0) {
            setupGeofence(clueList);
        }

        Clue clue = (Clue) intent.getSerializableExtra("clue");
        if(clue!=null) {
            Clues.updateClueListWith(clue);
            // if we do this it means clicking on the notification
            // will resume the hunt where they left off, and they can't go back

            String textToDisplay = "tell the devs MainActivity onCreate() clue structure is wrong";
            if(clue.hasBeenSolved()) { //after compass

                boolean unsolved = clue.containsUnsolvedSegments();
                textToDisplay = clue.getLastSolvedCompassClueSegment().getClueText();

                if(!unsolved) { //all compass clues in the chain have been solved
                    findViewById(R.id.v2_button).setVisibility(View.INVISIBLE);
                } else { //display button to next compass clue
                    findViewById(R.id.v2_button).setVisibility(View.VISIBLE);
                }

            } else if(clue.hasBeenDiscovered()) { //after geofence
                textToDisplay = clue.getGeofenceDiscoveredText();
                if(!clue.isSimpleClue()) {
                    findViewById(R.id.v2_button).setVisibility(View.VISIBLE);
                }
            }

            ((TextView) findViewById(R.id.v2_geofenceDisplay)).setText(textToDisplay);
            triggeringClue = clue;

            Log.i("GEOFENCE STATUS", "opened notification for: " + clue.getGeofenceGeofenceData().getName());
            Log.i("GEOFENCE STATUS", "displaying text: "+textToDisplay);
            Log.i("button or something", ""+clue.hasBeenDiscovered());
        }

        //////////////////////////////////////////////////////////////////////////////////

        //try {
        //    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //    ((TextView) findViewById(R.id.v2_testText)).setText("you are at ("+location.getLatitude()+", "+location.getLongitude()+") (accuracy: "+location.getAccuracy()+")");
        //} catch (SecurityException e) {e.printStackTrace();} catch (Exception ex) {}
    }

    private void setupGeofence(ArrayList<Clue> clueList) {

        if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            try {
                throw new RuntimeException("google play not available. try and restart the service, or something, I guess");
            } catch (RuntimeException e) {e.printStackTrace();}
        }


        List<GeofenceData> geoData = new ArrayList<>();
        for(Clue clue : clueList) {
            geoData.add(clue.getGeofenceGeofenceData());
        }

        List<Geofence> mGeofenceList = new ArrayList<>();
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
                        ((TextView) findViewById(R.id.v2_geofenceDisplay)).setText("Geofence error: "+e.getMessage());
                    }
                });
        //mGeofencingClient.removeGeofences(new ArrayList<String>() {
        //});
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
        // calling addGeofences() and removeGeofences(). TODO change to UPDATE_CURRENT when implementing removeGeofences()
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_CANCEL_CURRENT);
        return mGeofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

}

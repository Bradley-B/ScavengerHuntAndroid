package com.bradleyboxer.scavengerhunt.v2;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bradleyboxer.scavengerhunt.R;

public class CompassActivity extends AppCompatActivity {
    private LocationManager lm;
    private static final String TAG = "CompassActivity";

    private Compass compass;
    private ImageView arrowView;

    private float currentAzimuth;
    private float targetRadius;
    private int compassTouches = 0;
    private boolean locationLocked = false;
    private Clue triggeringClue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_activity_compass);
        arrowView = (ImageView) findViewById(R.id.v2_main_image_hands);
        findViewById(R.id.v2_button2).setEnabled(false);

        Intent intent = getIntent();
        Clue clue = (Clue) intent.getSerializableExtra("clue");
        double latit = clue.getActiveCompassClueSegment().getGeofenceData().latitude;
        double longit = clue.getActiveCompassClueSegment().getGeofenceData().longitude;
        targetRadius = clue.getActiveCompassClueSegment().getGeofenceData().radius;
        triggeringClue = clue;

        setupCompass(latit, longit);

        lm = getSystemService(LocationManager.class);
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);
        } catch (SecurityException e) {e.printStackTrace();}

        findViewById(R.id.v2_main_image_dial).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
                    compassTouches++;
                    if(shouldDisplayNextClue(-1f)) {
                        findViewById(R.id.v2_button2).setEnabled(true);
                    }
                }
                return true;
            }
        });

        compassTouches = 0;
    }

    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location!=null) {
                if(!locationLocked) {
                    findViewById(R.id.v2_main_image_hands).setVisibility(View.VISIBLE);
                    locationLocked = true;
                }
                compass.updateLocation(location);
                float distanceToTarget = location.distanceTo(compass.getTarget());
                ((TextView)findViewById(R.id.v2_distanceView)).setText("Accuracy: "+location.getAccuracy()+"\nDistance to target: "+distanceToTarget);
                if(shouldDisplayNextClue(distanceToTarget)) {
                    findViewById(R.id.v2_button2).setEnabled(true);
                }
            }
        }
        @Override public void onProviderDisabled(String provider) {}
        @Override public void onProviderEnabled(String provider) {}
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    public void displayNextClueOnClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        triggeringClue.getActiveCompassClueSegment().setSolved();
        intent.putExtra("clue", triggeringClue);
        startActivity(intent);
    }

    private boolean shouldDisplayNextClue(float distanceToTarget) {
        if(distanceToTarget>-1) {
            return compassTouches>50 || distanceToTarget<targetRadius;
        }
        return compassTouches>50;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start compass");
        compass.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        compass.stop();
    }

    private void setupCompass(double targetLat, double targetLong) {
        compass = new Compass(this, targetLat, targetLong);
        Compass.CompassListener cl = new Compass.CompassListener() {

            @Override
            public void onNewAzimuth(float azimuth) {
                adjustArrow(azimuth);
            }
        };
        compass.setListener(cl);
    }

    private void adjustArrow(float azimuth) {
        Log.d(TAG, "will set rotation from " + currentAzimuth + " to "
                + azimuth);

        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        an.setDuration(250);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }

}

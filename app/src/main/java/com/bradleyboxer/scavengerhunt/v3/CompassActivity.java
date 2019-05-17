package com.bradleyboxer.scavengerhunt.v3;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bradleyboxer.scavengerhunt.R;
import com.bradleyboxer.scavengerhunt.v2.Compass;

public class CompassActivity extends MenuActivity {

    private LocationManager locationManager;
    private Compass compass;
    private ImageView compassHands;

    private float currentAzimuth;
    private float targetRadius;
    private boolean locationLocked = false;
    private Clue clue;
    private boolean solved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_compass);
        compassHands = (ImageView) findViewById(R.id.compass_hands);
        compassHands.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        CompassClue clue = (CompassClue) intent.getSerializableExtra("clue");

        if(clue!=null) {
            double latit = clue.getLocation().getLatitude();
            double longit = clue.getLocation().getLongitude();
            targetRadius = clue.getLocation().getRadius();
            this.clue = clue;

            setupCompass(latit, longit);

            locationManager = getSystemService(LocationManager.class);
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);
            } catch (SecurityException e) {e.printStackTrace();}

            TextView title = (TextView) findViewById(R.id.compass_title);
            title.setText("Currently Solving: " + clue.getName());
        } else {
            TextView textView  = findViewById(R.id.compass_accuracy);
            textView.setText("no active compass clues");
        }

        super.onCreate(savedInstanceState);
        setCheckedId(R.id.nav_answer_compass_clue);
    }

    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location!=null) {
                if(!locationLocked) {
                    findViewById(R.id.compass_hands).setVisibility(View.VISIBLE);
                    compass.start();
                    locationLocked = true;
                }
                compass.updateLocation(location);
                float distanceToTarget = location.distanceTo(compass.getTarget());
                ((TextView)findViewById(R.id.compass_accuracy)).setText("Accuracy: "+location.getAccuracy()+"\nDistance to target: "+distanceToTarget);
                checkClueSolved(distanceToTarget);
            }
        }
        @Override public void onProviderDisabled(String provider) {}
        @Override public void onProviderEnabled(String provider) {}
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private void checkClueSolved(float distanceToTarget) {
        if(distanceToTarget<targetRadius && distanceToTarget>-1) {
            if(!solved) {
                //solve clue
                ScavengerHunt scavengerHunt = FileUtil.loadScavengerHunt(this);
                scavengerHunt.solveClue(clue.getName());
                FileUtil.saveScavengerHunt(scavengerHunt, this);
                Notifications.sendNotification(clue.getName(), this);
                solved = true;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(compass!=null && locationManager!=null) {
            compass.stop();
            locationManager.removeUpdates(mListener);
        }
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
        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        an.setDuration(250);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        compassHands.startAnimation(an);
    }
}

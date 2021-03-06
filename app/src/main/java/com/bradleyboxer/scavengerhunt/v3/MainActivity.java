package com.bradleyboxer.scavengerhunt.v3;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bradleyboxer.scavengerhunt.R;

import java.util.List;

public class MainActivity extends MenuActivity {

    public static final int LOCATION_AND_CAMERA_REQUEST_ID = 1;
    private ScavengerHuntDatabase scavengerHuntDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA},
                    LOCATION_AND_CAMERA_REQUEST_ID);
        }

        scavengerHuntDatabase = FileUtil.loadScavengerHuntDatabase(this);
        if(scavengerHuntDatabase==null) {
            scavengerHuntDatabase = new ScavengerHuntDatabase();
            FileUtil.saveScavengerHuntDatabase(scavengerHuntDatabase, this);
        }

        setClueDisplays(scavengerHuntDatabase);
        animateProgressBar(scavengerHuntDatabase);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                try {
                    final LocationListener locationListener = new LocationListener() {
                        int locationChecks = 0;
                        @Override
                        public void onLocationChanged(Location location) {
                            locationChecks++;
                            if(location.getAccuracy()<100) {
                                locationManager.removeUpdates(this);

                                boolean changed = forceSolveGeofenceClues(location);
                                if(changed) {
                                    reloadActivity();
                                }

                                String loc = location.getLatitude() + ", " + location.getLongitude();
                                Snackbar.make(view, "Location updated: " + loc, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } else if(locationChecks>10) {
                                locationManager.removeUpdates(this);
                                Snackbar.make(view, "GeoLocation failed to update.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
                        @Override public void onStatusChanged(String s, int i, Bundle bundle) { }
                        @Override public void onProviderEnabled(String s) { }
                        @Override public void onProviderDisabled(String s) { }
                    };
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);

                    Snackbar.make(view, "Updating location... please wait", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Action", null).show();

                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

        if(getIntent().hasExtra(QrScanner.QR_CODE_KEY)) {
            handleQrEntryResult(getIntent());
        }

        super.onCreate(savedInstanceState);
        setCheckedId(R.id.nav_progress);
    }

    public void setClueDisplays(ScavengerHuntDatabase scavengerHuntDatabase) {
        int[] states = scavengerHuntDatabase.getTotalClueStates();
        TextView stateViewInactive = findViewById(R.id.clueStatus_inactive);
        TextView stateViewActive = findViewById(R.id.clueStatus_active);
        TextView stateViewSolved = findViewById(R.id.clueStatus_solved);
        stateViewInactive.setText(Html.fromHtml("<b><big>"+states[0]+"</big></b>"+"<br><small><small>inactive</small></small>"));
        stateViewActive.setText(Html.fromHtml("<b><big>"+states[1]+"</big></b>"+"<br><small><small>active</small></small>"));
        stateViewSolved.setText(Html.fromHtml("<b><big>"+states[2]+"</big></b>"+"<br><small><small>solved</small></small>"));

    }

    public void animateProgressBar(ScavengerHuntDatabase scavengerHuntDatabase) {
        float progress = scavengerHuntDatabase.getTotalProgressPercent();

        //animate progress bar to current position
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, (int)(progress*1000));
        animation.setStartDelay(500);
        animation.setDuration(1250); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator(1f));
        animation.start();
        progressBar.invalidate();

        //animate progress integer
        final TextView textView = findViewById(R.id.progressBarText);
        ValueAnimator animation2 = ValueAnimator.ofInt(0, (int)(progress*100));
        animation2.setStartDelay(500);
        animation2.setDuration(1250); // in milliseconds
        animation2.setInterpolator(new DecelerateInterpolator(1f));
        animation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                String result = valueAnimator.getAnimatedValue().toString()+"%";
                textView.setText(result);
            }
        });
        animation2.start();
        textView.invalidate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_load_test) {
            scavengerHuntDatabase = new ScavengerHuntDatabase();
            FileUtil.saveScavengerHuntDatabase(scavengerHuntDatabase, this);
            reloadActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == LOCATION_AND_CAMERA_REQUEST_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                 throw new RuntimeException();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_progress);

        if(resultCode == RESULT_OK && requestCode == QrScanner.QR_REQUEST_CODE) {
            handleQrEntryResult(data);
        }

    }

    private void handleQrEntryResult(Intent data) {
        QrEntry entry = (QrEntry) data.getSerializableExtra(QrScanner.QR_CODE_KEY);
        ScavengerHuntDatabase scavengerHuntDatabase = FileUtil.loadScavengerHuntDatabase(this);

        if(entry.getType().equals(QrEntry.Type.SCAVENGER_HUNT)) {
            Log.v(ScavengerHuntDatabase.TAG,"downloading scavenger hunt: " + entry.getUuid());
            scavengerHuntDatabase.downloadScavengerHunt(entry.getUuid(), this);
        } else if(entry.getType().equals(QrEntry.Type.CLUE)) {
            scavengerHuntDatabase.solveClue(entry.getUuid());
            reloadActivity();
        }
    }

    private void reloadActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public boolean forceSolveGeofenceClues(Location location) {
        boolean atLeastOneSolved = false;

        for(ScavengerHunt scavengerHunt : scavengerHuntDatabase.getScavengerHunts()) {
            List<Clue> clueList = scavengerHunt.getClueList();
            for(Clue clue : clueList) {
                if(clue.getType().equals(Clue.Type.GEOFENCE)) {
                    GeofenceClue geofenceClue = (GeofenceClue) clue;
                    boolean shouldBeSolved = geofenceClue.shouldBeSolved(location);
                    if(shouldBeSolved) {
                        scavengerHunt.solveClue(clue.getUuid());
                        Notifications.sendNotification(clue.getName(), this);
                        atLeastOneSolved = true;
                    }
                }
            }
        }

        if(atLeastOneSolved) {
            FileUtil.saveScavengerHuntDatabase(scavengerHuntDatabase, this);
        }

        return atLeastOneSolved;
    }
}

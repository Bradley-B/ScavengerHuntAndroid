package com.bradleyboxer.scavengerhunt.v3;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bradleyboxer.scavengerhunt.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MenuActivity {

    private ScavengerHunt scavengerHunt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        scavengerHunt = FileUtil.loadScavengerHunt(this);
        if(scavengerHunt==null) {
            scavengerHunt = Assembly.assembleMorganScavengerHunt(this);
            FileUtil.saveScavengerHunt(scavengerHunt, this);
        }

        int[] states = scavengerHunt.getClueStates();
        TextView stateViewInactive = findViewById(R.id.clueStatus_inactive);
        TextView stateViewActive = findViewById(R.id.clueStatus_active);
        TextView stateViewSolved = findViewById(R.id.clueStatus_solved);
        stateViewInactive.setText(Html.fromHtml("<b><big>"+states[0]+"</big></b>"+"<br><small><small>inactive</small></small>"));
        stateViewActive.setText(Html.fromHtml("<b><big>"+states[1]+"</big></b>"+"<br><small><small>active</small></small>"));
        stateViewSolved.setText(Html.fromHtml("<b><big>"+states[2]+"</big></b>"+"<br><small><small>solved</small></small>"));
        View.OnClickListener stateViewTouchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ClueViewActivity.class);
                startActivity(intent);
            }
        };
        stateViewInactive.setOnClickListener(stateViewTouchListener);
        stateViewActive.setOnClickListener(stateViewTouchListener);
        stateViewSolved.setOnClickListener(stateViewTouchListener);

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

        super.onCreate(savedInstanceState);
        setCheckedId(R.id.nav_progress);
    }

    @Override
    public void onStart() {
        super.onStart();
        float progress = scavengerHunt.getProgressPercent();

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
            FileUtil.saveScavengerHunt(Assembly.assembleMorganScavengerHunt(this), this);
            reloadActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_progress);
    }

    public void reloadActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public boolean forceSolveGeofenceClues(Location location) {
        boolean atLeastOneSolved = false;
        List<Clue> clueList = scavengerHunt.getClueList();
        for(Clue clue : clueList) {
            if(clue.getType().equals(Clue.Type.GEOFENCE)) {
                GeofenceClue geofenceClue = (GeofenceClue) clue;
                boolean shouldBeSolved = geofenceClue.shouldBeSolved(location);
                if(shouldBeSolved) {
                    scavengerHunt.solveClue(clue.getName());
                    FileUtil.saveScavengerHunt(scavengerHunt, this);
                    Notifications.sendNotification(clue.getName(), this);
                    atLeastOneSolved = true;
                }
            }
        }
        return atLeastOneSolved;
    }
}

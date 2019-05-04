package com.bradleyboxer.scavengerhunt.v3;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Property;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bradleyboxer.scavengerhunt.R;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ScavengerHunt scavengerHunt = createScavengerHunt();

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        float progress = scavengerHunt.getProgressPercent();
        //float progress = 0.4f;

        //animate progress bar to current position
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, (int)(progress*1000));
        animation.setStartDelay(750);
        animation.setDuration(1000); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator(1f));
        animation.start();
        progressBar.invalidate();

        //animate progress integer
        final TextView textView = findViewById(R.id.progressBarText);
        ValueAnimator animation2 = ValueAnimator.ofInt(0, (int)(progress*100));
        animation2.setStartDelay(750);
        animation2.setDuration(1000); // in milliseconds
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public ScavengerHunt createScavengerHunt() {
        ScavengerHunt scavengerHunt = new ScavengerHunt();

        //geofence test
        GeofenceManager geofenceManager = new GeofenceManager(getApplicationContext());
        GeoLocation location = new GeoLocation(43.084970f, -77.667192f, 1000);
        Clue clue = new GeofenceClue("Dorm Test", "hintText", "solvedText", location, geofenceManager);
        clue.activate();

        scavengerHunt.addClue(clue);
        return scavengerHunt;
    }

    public boolean geofenceShouldTrigger(ScavengerHunt scavengerHunt, Location location) {
        List<Clue> clueList = scavengerHunt.getClueList();
        for(Clue clue : clueList) {
            if(clue.getType().equals(Clue.Type.GEOFENCE)) {
                GeofenceClue geoClue = (GeofenceClue) clue;
                Location clueLoc = new Location("Bradley Boxer");
                clueLoc.setLatitude(geoClue.getLocation().getLatitude());
                clueLoc.setLongitude(geoClue.getLocation().getLongitude());
                float dist = clueLoc.distanceTo(location);

                if(clue.isActive() && dist < geoClue.getLocation().getRadius()) {
                    return true;
                }
            }
        }
        return false;
    }
}

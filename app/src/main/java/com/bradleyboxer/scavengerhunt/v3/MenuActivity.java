package com.bradleyboxer.scavengerhunt.v3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.bradleyboxer.scavengerhunt.R;

public abstract class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int checkedId;
    private NavigationView navigationView;

    public static final int QR_RESULT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    void setCheckedId(int id) {
        this.checkedId = id;
        navigationView.setCheckedItem(checkedId);
    }

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(checkedId==id) {
            return false;
        }

        if (id == R.id.nav_progress) {
            useIntent(MainActivity.class);
        } else if (id == R.id.nav_clues) {
            useIntent(ClueViewActivity.class);
        } else if (id == R.id.nav_export_qr) {
            useIntent(GenerateQrActivity.class);
        } else if(id == R.id.nav_import_qr) {
            Intent intent = new Intent(this, QrScanner.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, QR_RESULT_CODE);
            overridePendingTransition(0, 0);

        } else if (id == R.id.nav_answer_compass_clue) {
            ScavengerHuntDatabase scavengerHuntDatabase = FileUtil.loadScavengerHuntDatabase(this);
            ScavengerHunt scavengerHunt = scavengerHuntDatabase.getActiveScavengerHunt(this);
            Clue earliestUnsolved = null;
            if(scavengerHunt != null) {
                earliestUnsolved = scavengerHunt.getEarliestUnsolved(Clue.Type.COMPASS);
            }

            Intent intent = new Intent(this, CompassActivity.class);
            intent.putExtra("clue", earliestUnsolved);
            useIntent(intent);
        } else if (id == R.id.nav_answer_text_clue) {
            useIntent(TextInputActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void useIntent(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void useIntent(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        useIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(checkedId);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }
}
